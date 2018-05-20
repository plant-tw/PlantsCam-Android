package plantscam.android.prada.lab.plantscamera

import android.content.res.AssetManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import com.cardinalblue.utils.YUVUtils
import com.google.gson.annotations.SerializedName
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import plantscam.android.prada.lab.plantscamera.ml.Classification
import plantscam.android.prada.lab.plantscamera.ml.Classifier
import plantscam.android.prada.lab.plantscamera.ml.MnistClassifier
import plantscam.android.prada.lab.plantscamera.utils.ImageUtils

/**
 * Created by prada on 30/04/2018.
 */
class CameraViewModel(private val assets: AssetManager) {
    private val disposeBag = CompositeDisposable()
    private val renderSignal = BehaviorSubject.create<CameraViewState>()
    private var buff1: IntArray? = null
    private var buff2: IntArray? = null

    private var rgbBitmap: Bitmap? = null
    private val croppedBitmap = Bitmap.createBitmap(MnistClassifier.PIXEL_WIDTH, MnistClassifier.PIXEL_WIDTH, Bitmap.Config.ARGB_8888)
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
                        MnistClassifier.PIXEL_WIDTH, MnistClassifier.PIXEL_WIDTH,
                        0, false)
            })
        disposeBag.add(Observable.combineLatest(
            initTensorflow(assets),
            cameraBuffSignal
                .observeOn(Schedulers.io())
                .filter { rgbBitmap != null }
                .map { toRGB(it.previewData, it.width, it.height) }
                .map { crop(it, MnistClassifier.PIXEL_WIDTH, MnistClassifier.PIXEL_WIDTH) }
                .map { MnistClassifier.copyPixel(it)!! },
            BiFunction<Classifier, FloatArray, Classification> { tf, data ->
                 tf.recognize(data)
            })
            .subscribeOn(Schedulers.io())
            .subscribe {
                renderSignal.onNext(CameraViewState(it))
            })

//        disposeBag.add(takePhotoSignal
//            .switchMap { data ->
//                val s1 = Observable.just(data)
//                    .map {
//                        val uuid = UuidFactory(this@CameraActivity).deviceUuid.toString()
//                        val file = File(externalCacheDir, uuid + ".jpg")
//                        FileUtils.write(file, it)
//                        file
//                    }
//                val s2 = ReactiveSensors(baseContext)
//                    .observeSensor(Sensor.TYPE_GYROSCOPE)
//                    .filter(ReactiveSensorFilter.filterSensorChanged())
//                    .take(1)
//                    .subscribeOn(Schedulers.computation())
//                    .map {
//                        val event = it.sensorEvent
//                        val x = event.values[0]
//                        val y = event.values[1]
//                        val z = event.values[2]
//                        SensorData(x, y, z)
//                    }
//                    .toObservable()
//                Observable.zip(s1, s2, BiFunction<File, SensorData, File> { file, sensor ->
//                    val exif = ExifInterface(file.toString())
//                    exif.setAttribute(ExifInterface.TAG_USER_COMMENT, Gson().toJson(sensor).toString())
//                    exif.saveAttributes()
//                    file
//                })
//            }.subscribe { file ->
//                Toast.makeText(baseContext, "Saved!!", Toast.LENGTH_LONG).show()
//                System.out.println(">>>>> file : " + it.toString())
//                // FIXME make sure the Stream is closed after this callback
//                Toast.makeText(baseContext, "Error!! " + error.message, Toast.LENGTH_LONG).show()
//            }
//        )
    }

    fun unbindIntents() {
        disposeBag.clear()
    }

    fun render(): Observable<CameraViewState> {
        return renderSignal
    }

    data class CameraViewState(val classification: Classification)

    data class SensorData(
        @SerializedName("x") val x : Float,
        @SerializedName("y") val y : Float,
        @SerializedName("z") val z : Float)

    private fun initTensorflow(assets: AssetManager): Observable<Classifier> {
        return Observable.create {
            try {
                it.onNext(MnistClassifier.create(assets, "TensorFlow",
                        "mnist/opt_mnist_convnet-tf.pb", "mnist/labels.txt", MnistClassifier.PIXEL_WIDTH,
                        "input", "output", true))
//                it.onNext(PlantFreezeClassifier2(assets))
            } catch (e: Throwable) {
                it.onError(e)
            }
        }
    }

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
}