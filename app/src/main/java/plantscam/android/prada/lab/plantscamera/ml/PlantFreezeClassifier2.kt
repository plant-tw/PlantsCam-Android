package plantscam.android.prada.lab.plantscamera.ml

import android.content.res.AssetManager
import android.graphics.Bitmap
import android.graphics.Color
import android.util.Log
import org.tensorflow.contrib.android.TensorFlowInferenceInterface

/**
 * Created by prada on 07/04/2018.
 */

class PlantFreezeClassifier2(mgr: AssetManager) {

    private val tensorflow: TensorFlowInferenceInterface = TensorFlowInferenceInterface(mgr, "file:///android_asset/frozen_graph.pb")
    private val mPlantOutputs = FloatArray(OUTPUT_NUM)

    fun run(pixels: FloatArray): Int {
        tensorflow.feed(INPUT_NAME, pixels, 1L, INPUT_W.toLong(), INPUT_H.toLong(), 3L)
        tensorflow.run(OUTPUT_NAMES)
        tensorflow.fetch(OUTPUT_NAMES[0], mPlantOutputs)

        var idx = 0
        var max = mPlantOutputs[0]
        for (i in 1 until mPlantOutputs.size) {
            Log.d("TF DEMO", "output = " + mPlantOutputs[i])
            if (mPlantOutputs[i] > max) {
                idx = i
                max = mPlantOutputs[i]
            }
        }
        return idx
    }

    companion object {

        const val INPUT_W = 224
        const val INPUT_H = 224
        const val OUTPUT_NUM = 11

        const val R_MEAN = 123.68
        const val G_MEAN = 116.78
        const val B_MEAN = 103.94

        private val OUTPUT_NAMES = arrayOf("resnet_v2_50/predictions/Reshape_1:0")
        private val INPUT_NAME = "input:0"

        private val buffer = IntArray(INPUT_W * INPUT_H)
        private val pixelBuffer = FloatArray(INPUT_W * INPUT_H * 3)

        fun convertTo(bm: Bitmap): FloatArray {
            bm.getPixels(buffer, 0, INPUT_W, 0, 0, INPUT_W, INPUT_H)
            return convertTo(buffer)
        }

        fun convertTo(buff: IntArray): FloatArray {
            if (buff.size != INPUT_W * INPUT_H) {
                throw IllegalArgumentException("input buffer size isn't the same as the expected size for Tensorflow")
            }
            for (i in 0 until buff.size) {
                val pix = buff[i]
                pixelBuffer[3 * i] = (Color.red(pix) - R_MEAN).toFloat()
                pixelBuffer[3 * i + 1] = (Color.green(pix) - G_MEAN).toFloat()
                pixelBuffer[3 * i + 2] = (Color.blue(pix) - B_MEAN).toFloat()
            }
            return pixelBuffer
        }
    }
}