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

class PlantFreezeClassifier(mgr: AssetManager) {

    private val tensorflow: TensorFlowInferenceInterface = TensorFlowInferenceInterface(mgr, "file:///android_asset/plant_ab_freeze.pb")
    private val mOutputs = FloatArray(5)
    // private String[] mOutputNames = new String[] {"sum"};
    private val mPlantOutputNames = arrayOf("agent/Softmax")
    private val mPlantOutputs = FloatArray(10)
    private val mInputSize = Rect(0, 0, 128, 128)

    fun run(pixels: FloatArray, lenPixel: Float, lenCm: Float): Int {
        tensorflow.feed("agent/img", pixels, 1L, mInputSize.width().toLong(), mInputSize.height().toLong(), 3L)
        tensorflow.feed("agent/len_pixel", floatArrayOf(lenPixel), 1L)
        tensorflow.feed("agent/len_cm", floatArrayOf(lenCm), 1L)
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

        private val buffer = IntArray(128 * 128)
        private val pixelBuffer = FloatArray(128 * 128 * 3)

        fun convertTo(bm: Bitmap): FloatArray {
            // bitmap pixel to float[]
            bm.getPixels(buffer, 0, 128, 0, 0, 128, 128)
            val greenStep = 128 * 128
            val blueStep = greenStep * 2
            for (i in 0 until buffer.size) {
                // Set 0 for white and 255 for black pixel
                val pix = buffer[i]
//                        pixelBuffer[3 * i] = Color.red(pix) / 255f
//                        pixelBuffer[3 * i + 1] = Color.green(pix) / 255f
//                        pixelBuffer[3 * i + 2] = Color.blue(pix) / 255f

                pixelBuffer[i] = Color.red(pix) / 255f
                pixelBuffer[greenStep + i] = Color.green(pix) / 255f
                pixelBuffer[blueStep + i] = Color.blue(pix) / 255f
            }
            return pixelBuffer
        }
    }
}
