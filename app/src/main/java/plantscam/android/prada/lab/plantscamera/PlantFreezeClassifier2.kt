package plantscam.android.prada.lab.plantscamera

import android.content.res.AssetManager
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Rect
import android.util.Log
import org.tensorflow.contrib.android.TensorFlowInferenceInterface

/**
 * Created by prada on 07/04/2018.
 */

class PlantFreezeClassifier2(mgr: AssetManager) {

    private val tensorflow: TensorFlowInferenceInterface = TensorFlowInferenceInterface(mgr, "file:///android_asset/frozen_graph.pb")
    // private String[] mOutputNames = new String[] {"sum"};
    private val mPlantOutputNames = arrayOf("resnet_v2_50/predictions/Reshape_1:0")
    private val mOutputs = FloatArray(OUTPUT_NUM)
    private val mPlantOutputs = FloatArray(OUTPUT_NUM)
    private val mInputSize = Rect(0, 0, INPUT_W, INPUT_H)

    fun run(pixels: FloatArray): Int {
        tensorflow.feed("input:0", pixels, 1L, mInputSize.width().toLong(), mInputSize.height().toLong(), 3L)
        tensorflow.run(mPlantOutputNames)
        tensorflow.fetch(mPlantOutputNames[0], mPlantOutputs)

        var idx = 0
        var max = mOutputs[0]
        for (i in 1 until mOutputs.size) {
            Log.d("TF DEMO", "output = " + mOutputs[i])
            if (mOutputs[i] < max) {
                idx = i
                max = mOutputs[i]
            }
        }
        return idx
    }


    companion object {

        const val INPUT_W = 224
        const val INPUT_H = 224
        const val OUTPUT_NUM = 10

        const val R_MEAN = 123.68
        const val G_MEAN = 116.78
        const val B_MEAN = 103.94

        private val buffer = IntArray(INPUT_W * INPUT_H)
        private val pixelBuffer = FloatArray(INPUT_W * INPUT_H * 3)

        fun convertTo(bm: Bitmap): FloatArray {
            // bitmap pixel to float[]
            bm.getPixels(buffer, 0, INPUT_W, 0, 0, INPUT_W, INPUT_H)
            for (i in 0 until buffer.size) {
                // Set 0 for white and 255 for black pixel
                val pix = buffer[i]
                pixelBuffer[3 * i] = (Color.red(pix) - R_MEAN).toFloat()
                pixelBuffer[3 * i + 1] = (Color.green(pix) - G_MEAN).toFloat()
                pixelBuffer[3 * i + 2] = (Color.blue(pix) - B_MEAN).toFloat()
            }
            return pixelBuffer
        }
    }
}