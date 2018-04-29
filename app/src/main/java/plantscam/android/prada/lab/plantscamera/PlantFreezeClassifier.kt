package plantscam.android.prada.lab.plantscamera

import android.content.res.AssetManager
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Rect
import android.renderscript.ScriptGroup
import android.util.Log

import org.tensorflow.contrib.android.TensorFlowInferenceInterface

/**
 * Created by prada on 07/04/2018.
 */

class PlantFreezeClassifier(mgr: AssetManager) {

    private val tensorflow: TensorFlowInferenceInterface = TensorFlowInferenceInterface(mgr, "file:///android_asset/plant_ab_freeze.pb")

    private val mPlantOutputs = FloatArray(10)

    fun run(pixels: FloatArray, lenPixel: Float, lenCm: Float): Int {
        tensorflow.feed(INPUT_NAMES[0], pixels, 1L, INPUT_W.toLong(), INPUT_H.toLong(), 3L)
        tensorflow.feed(INPUT_NAMES[1], floatArrayOf(lenPixel), 1L)
        tensorflow.feed(INPUT_NAMES[2], floatArrayOf(lenCm), 1L)
        tensorflow.run(mPlantOutputNames)
        tensorflow.fetch(mPlantOutputNames[0], mPlantOutputs)

        var idx = 0
        var max = mPlantOutputs[0]
        for (i in 1 until mPlantOutputs.size) {
            if (mPlantOutputs[i] > max) {
                idx = i
                max = mPlantOutputs[i]
            }
        }
        return idx
    }

    companion object {

        const val INPUT_W = 128
        const val INPUT_H = 128

        private val buffer = IntArray(INPUT_W * INPUT_H)
        private val pixelBuffer = FloatArray(INPUT_W * INPUT_H * 3)

        private val mPlantOutputNames = arrayOf("agent/Softmax")
        private val INPUT_NAMES = arrayOf("agent/img", "agent/len_pixel", "agent/len_cm")


        fun convertTo(bm: Bitmap): FloatArray {
            // bitmap pixel to float[]
            bm.getPixels(buffer, 0, INPUT_W, 0, 0, INPUT_W, INPUT_H)
            for (i in 0 until buffer.size) {
                // Set 0 for white and 255 for black pixel
                val pix = buffer[i]
                pixelBuffer[3 * i] = (Color.red(pix) - 128f) / 128f
                pixelBuffer[3 * i + 1] = (Color.green(pix) - 128f) / 128f
                pixelBuffer[3 * i + 2] = (Color.blue(pix) - 128f) / 128f

            }
            return pixelBuffer
        }
    }
}
