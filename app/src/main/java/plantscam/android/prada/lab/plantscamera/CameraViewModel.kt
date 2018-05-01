package plantscam.android.prada.lab.plantscamera

import android.content.res.AssetManager
import android.hardware.Sensor
import android.media.ExifInterface
import com.github.pwittchen.reactivesensors.library.ReactiveSensorFilter
import com.github.pwittchen.reactivesensors.library.ReactiveSensors
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.piccollage.util.FileUtils
import com.piccollage.util.UuidFactory
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import plantscam.android.prada.lab.imageutils.ImageUtils
import java.io.File

/**
 * Created by prada on 30/04/2018.
 */
class CameraViewModel(private val assets: AssetManager) {
    private val disposeBag = CompositeDisposable()
    private val renderSignal = BehaviorSubject.create<CameraViewState>()
    private var buff: IntArray? = null


    fun bindIntents(cameraBuffSignal: Observable<CameraActivity.CameraPreviewData>,
                    takePhotoSignal: Observable<ByteArray?>) {
        disposeBag.add(Observable.combineLatest(
            initTensorflow(assets),
            cameraBuffSignal
                .map {
                    val w = PlantFreezeClassifier2.INPUT_W
                    val h = PlantFreezeClassifier2.INPUT_H
                    val bytes = getBytes(w, h)
                    ImageUtils.yuv2rgb(bytes, it.previewData, 0, 0, w, h)
                    bytes
                } // TODO have better way to crop pixel buffer
                // convert pixel to tensorflow
                .map { PlantFreezeClassifier2.convertTo(it) },
            BiFunction<PlantFreezeClassifier2, FloatArray, Int> { tf, data ->
                tf.run(data)
            })
            .subscribeOn(Schedulers.computation())
            .subscribe {
                renderSignal.onNext(CameraViewState(it.toString()))
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
//                    exif.setAttribute(ExifInterface.TAG_USER_COMMENT, toJsonStr(sensor))
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

    data class CameraViewState(val toastMsg: String)

    data class SensorData(
        @SerializedName("x") val x : Float,
        @SerializedName("y") val y : Float,
        @SerializedName("z") val z : Float)

    private fun initTensorflow(assets: AssetManager): Observable<PlantFreezeClassifier2> {
        return Observable.create {
            try {
                it.onNext(PlantFreezeClassifier2(assets))
            } catch (e: Throwable) {
                it.onError(e)
            }
        }
    }

    private fun toJsonStr(data: SensorData): String {
        return Gson().toJson(data).toString()
    }

    private fun getBytes(width: Int, height: Int): IntArray {
        if (buff == null || buff?.size != (width * height)) {
            buff = IntArray(width * height)
        }
        return buff!!
    }
}