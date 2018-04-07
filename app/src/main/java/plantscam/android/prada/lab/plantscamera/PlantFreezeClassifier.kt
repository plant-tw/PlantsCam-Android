package plantscam.android.prada.lab.plantscamera

import android.content.res.AssetManager
import android.graphics.Rect
import android.util.Log

import org.tensorflow.contrib.android.TensorFlowInferenceInterface

/**
 * Created by prada on 07/04/2018.
 */

class PlantFreezeClassifier(mgr: AssetManager) {

    private val tensorflow: TensorFlowInferenceInterface
    private val mOutputs = FloatArray(5)
    // private String[] mOutputNames = new String[] {"sum"};
    private val mPlantOutputNames = arrayOf("agent/Softmax")
    private val mPlantOutputs = FloatArray(10)
    private val mInputSize = Rect(0, 0, 128, 128)

    init {
        tensorflow = TensorFlowInferenceInterface(mgr, "file:///android_asset/plant_ab_freeze.pb")
    }

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
}
