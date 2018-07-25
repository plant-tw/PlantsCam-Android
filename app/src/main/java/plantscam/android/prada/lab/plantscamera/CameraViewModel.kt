package plantscam.android.prada.lab.plantscamera

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import com.cardinalblue.utils.YUVUtils
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import plantscam.android.prada.lab.plantscamera.ml.Classification
import plantscam.android.prada.lab.plantscamera.ml.Classifier
import plantscam.android.prada.lab.plantscamera.ml.ImageMLKitClassifier
import plantscam.android.prada.lab.plantscamera.utils.ImageUtils
import java.nio.ByteBuffer

/**
 * Created by prada on 30/04/2018.
 */
class CameraViewModel(private var classifierStream : Single<Classifier>) {

    private val disposeBag = CompositeDisposable()
    private val renderSignal = BehaviorSubject.create<CameraViewState>()
    private var buff1: IntArray? = null
    private var buff2: IntArray? = null

    private var rgbBitmap: Bitmap? = null
    private val croppedBitmap = Bitmap.createBitmap(TF_MODEL_INPUT_W, TF_MODEL_INPUT_H, Bitmap.Config.ARGB_8888)
    private val croppedCanvas = Canvas(croppedBitmap)
    private lateinit var frameToCropTransform : Matrix

    fun bindIntents(cameraBuffSignal: Observable<CameraActivity.CameraPreviewData>,
                    cameraPreviewReady: Observable<android.hardware.Camera.Size>,
                    takePhotoSignal: Observable<ByteArray?>) {
        disposeBag.add(cameraPreviewReady
            .subscribe {
                rgbBitmap = Bitmap.createBitmap(it.width, it.height, Bitmap.Config.ARGB_8888)
                frameToCropTransform = ImageUtils.getTransformationMatrix(
                        it.width, it.height,
                        TF_MODEL_INPUT_W, TF_MODEL_INPUT_H,
                        0, false)
            })
        disposeBag.add(Flowable.combineLatest(
            classifierStream.toFlowable(),
            cameraBuffSignal
                .toFlowable(BackpressureStrategy.DROP)
                .observeOn(Schedulers.io(), false, 1)
                .filter { rgbBitmap != null }
                .map { toRGB(it.previewData, it.width, it.height) }
                .map { crop(it, TF_MODEL_INPUT_W, TF_MODEL_INPUT_H) }
                .map { ImageMLKitClassifier.copyPixel(it) },
            BiFunction<Classifier, ByteBuffer, Classification> { tf, data ->
                 tf.recognize(data)
            })
            .subscribeOn(Schedulers.io())
            .subscribe {
                renderSignal.onNext(CameraViewState(it))
            })
    }

    fun unbindIntents() {
        disposeBag.clear()
    }

    fun render(): Observable<CameraViewState> {
        return renderSignal
    }

    data class CameraViewState(val classification: Classification)

    private fun toRGB(yuv: ByteArray, w1: Int, h1: Int): IntArray {
        val buff = getIntBuff(w1, h1)
        YUVUtils.yuv2rgb(buff, yuv, w1, h1)
        return buff
    }

    private fun crop(input: IntArray, w: Int, h: Int) : IntArray {
        val buffer = getIntBuff2(w, h)
        rgbBitmap!!.setPixels(input, 0, rgbBitmap!!.width, 0, 0, rgbBitmap!!.width, rgbBitmap!!.height)
        croppedCanvas.drawBitmap(rgbBitmap, frameToCropTransform, null)
        croppedBitmap.getPixels(buffer, 0, croppedBitmap.width, 0, 0, croppedBitmap.width, croppedBitmap.height)
        return buffer
    }

    private fun getIntBuff(width: Int, height: Int): IntArray {
        if (buff1 == null || buff1?.size != (width * height)) {
            buff1 = IntArray(width * height)
        }
        return buff1!!
    }

    private fun getIntBuff2(width: Int, height: Int): IntArray {
        if (buff2 == null || buff2?.size != (width * height)) {
            buff2 = IntArray(width * height)
        }
        return buff2!!
    }

    companion object {
        const val TF_MODEL_INPUT_W = ImageMLKitClassifier.DIM_IMG_SIZE_X
        const val TF_MODEL_INPUT_H = ImageMLKitClassifier.DIM_IMG_SIZE_Y
    }
}