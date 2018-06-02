package plantscam.android.prada.lab.plantscamera.ml

import android.content.res.AssetManager
import android.graphics.Bitmap
import android.graphics.Color
import android.util.Log
import org.tensorflow.contrib.android.TensorFlowInferenceInterface

/**
 * Created by prada on 07/04/2018.
 */

class PlantFreezeClassifier2(mgr: AssetManager) : Classifier {

    private val tensorflow: TensorFlowInferenceInterface = TensorFlowInferenceInterface(mgr, "file:///android_asset/xbase/frozen_graph.pb")
    private val mPlantOutputs = FloatArray(OUTPUT_NUM)
    private val labels = listOf(
        "0:三色堇",
        "1:久留米杜鵑",
        "2:九重葛",
        "3:五爪金龍",
        "4:仙丹花",
        "5:四季秋海棠",
        "6:垂花懸鈴花",
        "7:大花咸豐草",
        "8:天竺葵",
        "9:射干",
        "10:平戶杜鵑",
        "11:木棉",
        "12:木茼蒿",
        "13:杜鵑花仙子",
        "14:烏來杜鵑",
        "15:玫瑰",
        "16:白晶菊",
        "17:皋月杜鵑",
        "18:矮牽牛",
        "19:石竹",
        "20:紫嬌花",
        "21:羊蹄甲",
        "22:美人蕉",
        "23:艾氏香茶菜",
        "24:萬壽菊",
        "25:著生杜鵑",
        "26:蔓花生",
        "27:蛇苺",
        "28:蛇莓",
        "29:蜀葵",
        "30:蟛蜞菊",
        "31:通泉草",
        "32:酢漿草",
        "33:野菊花",
        "34:金毛杜鵑",
        "35:金盞花",
        "36:金絲桃",
        "37:金雞菊",
        "38:金魚草",
        "39:銀葉菊",
        "40:鳳仙花",
        "41:黃秋英",
        "42:黃金菊",
        "43:龍船花"
    )

    override fun recognize(pixels: FloatArray?): Classification {
        tensorflow.feed(INPUT_NAME, pixels, 1L, INPUT_W.toLong(), INPUT_H.toLong(), 3L)
        tensorflow.run(OUTPUT_NAMES)
        tensorflow.fetch(OUTPUT_NAMES[0], mPlantOutputs)

        var idx = 0
        var max = mPlantOutputs[0]
        for (i in 1 until mPlantOutputs.size) {
            // Log.d("TF DEMO", "output = " + mPlantOutputs[i])
            if (mPlantOutputs[i] > max) {
                idx = i
                max = mPlantOutputs[i]
            }
        }
        return Classification(max, labels[idx])
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

        private val pixelBuffer = FloatArray(INPUT_W * INPUT_H * 3)

        fun copyPixel(input: IntArray): FloatArray {
            if (input.size != INPUT_W * INPUT_H) {
                throw IllegalArgumentException("input buffer size isn't the same as the expected size for Tensorflow")
            }
            for (i in 0 until input.size) {
                val pix = input[i]
                pixelBuffer[3 * i] = (Color.red(pix) - R_MEAN).toFloat()
                pixelBuffer[3 * i + 1] = (Color.green(pix) - G_MEAN).toFloat()
                pixelBuffer[3 * i + 2] = (Color.blue(pix) - B_MEAN).toFloat()
            }
            return pixelBuffer
        }
    }
}