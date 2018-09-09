package plantscam.android.prada.lab.plantscamera.ml

import android.os.SystemClock
import android.util.Log
import com.google.android.gms.tasks.Tasks
import com.google.firebase.ml.common.FirebaseMLException
import com.google.firebase.ml.custom.*
import com.google.firebase.ml.custom.model.FirebaseCloudModelSource
import com.google.firebase.ml.custom.model.FirebaseLocalModelSource
import com.google.firebase.ml.custom.model.FirebaseModelDownloadConditions
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.*

class ImageMLKitClassifier
@Throws(FirebaseMLException::class)
internal constructor(fileLoader: (String) -> String) : Classifier {

    private val interpreter: FirebaseModelInterpreter?
    private val inputOutputOptions: FirebaseModelInputOutputOptions

    private val EMPTY_CLASSIFICATION = Classification(label = "")

    /** An instance of the driver class to run model inference with Tensorflow Lite.  */

    /** Labels corresponding to the output of the vision model.  */
    private val labelList: List<String>

    /** An array to hold inference results, to be feed into Tensorflow Lite as outputs.  */
    private var labelProbArray: Array<FloatArray>? = null
    /** multi-stage low pass filter  */
    private var filterLabelProbArray: Array<FloatArray>? = null

    private val sortedLabels = PriorityQueue<Pair<String, Float>>(
            RESULTS_TO_SHOW, Comparator { o1, o2 -> o1.second.compareTo(o2.second) })

    init {
        val conditions = FirebaseModelDownloadConditions.Builder()
                .build()

        // Build a FirebaseCloudModelSource object by specifying the name you assigned the model
        // when you uploaded it in the Firebase console.
        val localModelSource = FirebaseLocalModelSource.Builder("assets")
                .setAssetFilePath("plant.tflite").build()
        val cloudSource = FirebaseCloudModelSource.Builder("plant-mobilenet")
                .enableModelUpdates(true)
                .setInitialDownloadConditions(conditions)
                .setUpdatesDownloadConditions(conditions)
                .build()
        FirebaseModelManager.getInstance().registerLocalModelSource(localModelSource)
        FirebaseModelManager.getInstance().registerCloudModelSource(cloudSource)
        val options = FirebaseModelOptions.Builder()
                .setCloudModelName("plant-mobilenet")
                .setLocalModelName("assets")
                .build()
        interpreter = FirebaseModelInterpreter.getInstance(options)

        val label = fileLoader("labels.txt")
        labelList = label.split("\n")

        labelProbArray = Array(1) { FloatArray(labelList.size) }
        filterLabelProbArray = Array(FILTER_STAGES) { FloatArray(labelList.size) }

        inputOutputOptions = FirebaseModelInputOutputOptions.Builder()
            .setInputFormat(0, FirebaseModelDataType.FLOAT32, intArrayOf(DIM_BATCH_SIZE, DIM_IMG_SIZE_X, DIM_IMG_SIZE_Y, DIM_PIXEL_SIZE))
            .setOutputFormat(0, FirebaseModelDataType.FLOAT32, intArrayOf(1, labelList.size))
            .build()
    }

    override fun recognize(pixels: ByteBuffer): Classification {
        // Here's where the magic happens!!!
        val startTime = SystemClock.uptimeMillis()
        val inputs = FirebaseModelInputs.Builder().add(pixels).build()
        val result = interpreter!!.run(inputs, inputOutputOptions)
            .addOnFailureListener { e -> e.printStackTrace() }
            .addOnSuccessListener { firebaseModelOutputs -> Log.d(TAG, "firebaseModelOutputs = $firebaseModelOutputs") }
        try {
            Tasks.await(result)
        } catch (e : InterruptedException) {
            return EMPTY_CLASSIFICATION
        }

        val output = result.result.getOutput<Array<FloatArray>>(0)
        labelProbArray = output
        result.result

        val endTime = SystemClock.uptimeMillis()
        // smooth the results
        applyFilter()

        // print the results
        val labels = printTopKLabels(1)
        System.out.println((endTime - startTime).toString() + "ms")
        return labels[0]
    }

    private fun applyFilter() {
        val numOfLabels = labelList.size

        // Low pass filter `labelProbArray` into the first stage of the filter.
        for (j in 0 until numOfLabels) {
            filterLabelProbArray!![0][j] += FILTER_FACTOR *
                (labelProbArray!![0][j] - filterLabelProbArray!![0][j])
        }
        // Low pass filter each stage into the next.
        for (i in 1 until FILTER_STAGES) {
            for (j in 0 until numOfLabels) {
                filterLabelProbArray!![i][j] += FILTER_FACTOR *
                    (filterLabelProbArray!![i - 1][j] - filterLabelProbArray!![i][j])
            }
        }

        // Copy the last stage filter output back to `labelProbArray`.
        for (j in 0 until numOfLabels) {
            labelProbArray!![0][j] = filterLabelProbArray!![FILTER_STAGES - 1][j]
        }
    }

    /** Prints top-K labels, to be shown in UI as the results.  */
    private fun printTopKLabels(k : Int): List<Classification> {
        for (i in labelList.indices) {
            sortedLabels.add(Pair(labelList[i], labelProbArray!![0][i]))
            if (sortedLabels.size > RESULTS_TO_SHOW) {
                sortedLabels.poll()
            }
        }
        val size = Math.min(sortedLabels.size, k)
        val result = mutableListOf<Classification>()
        for (i in 0 until size) {
            val label = sortedLabels.poll()
            result.add(Classification(conf = label.second, label = label.first))
        }
        return result
    }

    companion object {

        /** A ByteBuffer to hold image data, to be feed into Tensorflow Lite as inputs.  */
        private val imgData: ByteBuffer by lazy {
            val data = ByteBuffer.allocateDirect(4 * DIM_BATCH_SIZE * DIM_IMG_SIZE_X * DIM_IMG_SIZE_Y * DIM_PIXEL_SIZE)
            data.order(ByteOrder.nativeOrder())
            data
        }

        fun copyPixel(input: IntArray): ByteBuffer {
            if (input.size != DIM_IMG_SIZE_X * DIM_IMG_SIZE_Y) {
                throw IllegalArgumentException("input buffer size isn't the same as the expected size for Tensorflow")
            }
            imgData.rewind()
            var k = 0
            for (i in 0 until DIM_IMG_SIZE_X) {
                for (j in 0 until DIM_IMG_SIZE_Y) {
                    val pix = input[k++]
                    imgData.putFloat(((pix shr 16 and 0xFF) - IMAGE_MEAN) / IMAGE_STD)
                    imgData.putFloat(((pix shr 8 and 0xFF) - IMAGE_MEAN) / IMAGE_STD)
                    imgData.putFloat(((pix and 0xFF) - IMAGE_MEAN) / IMAGE_STD)
                }
            }
            return imgData

        }

        /** Tag for the [Log].  */
        private const val TAG = "TfLiteCameraDemo"

        /** Number of results to show in the UI.  */
        private const val RESULTS_TO_SHOW = 3

        /** Dimensions of inputs.  */
        private const val DIM_BATCH_SIZE = 1

        private const val DIM_PIXEL_SIZE = 3

        const val DIM_IMG_SIZE_X = 224
        const val DIM_IMG_SIZE_Y = 224

        private const val IMAGE_MEAN = 128f
        private const val IMAGE_STD = 128f
        private const val FILTER_STAGES = 3
        private const val FILTER_FACTOR = 0.4f
    }
}
