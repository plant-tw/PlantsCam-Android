package plantscam.android.prada.lab.plantscamera

import android.Manifest
import android.hardware.Sensor
import android.media.ExifInterface
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.github.pwittchen.reactivesensors.library.ReactiveSensorFilter
import com.github.pwittchen.reactivesensors.library.ReactiveSensors
import com.google.android.cameraview.CameraView
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.piccollage.util.FileUtils
import com.piccollage.util.UuidFactory
import com.tbruyelle.rxpermissions2.RxPermissions
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_camera.*
import java.io.File

class CameraActivity : AppCompatActivity() {

    val disposeBag = CompositeDisposable()
    val cameraCallback = object: CameraView.Callback() {
        override fun onPictureTaken(cameraView: CameraView?, jpegData: ByteArray?) {
            jpegData.let { data ->
                val s1 = Observable.just(data)
                    .map {
                        val uuid = UuidFactory(baseContext).deviceUuid.toString()
                        val file = File(baseContext.externalCacheDir, uuid + ".jpg")
                        FileUtils.write(file, it)
                        file
                    }
                val s2 = ReactiveSensors(baseContext)
                    .observeSensor(Sensor.TYPE_GYROSCOPE)
                    .filter(ReactiveSensorFilter.filterSensorChanged())
                    .take(1)
                    .subscribeOn(Schedulers.computation())
                    .map {
                        val event = it.sensorEvent
                        val x = event.values[0]
                        val y = event.values[1]
                        val z = event.values[2]
                        SensorData(x, y, z)
                    }
                    .toObservable()
                Observable.zip(s1, s2, BiFunction<File, SensorData, File> { file, sensor ->
                    val exif = ExifInterface(file.toString())
                    exif.setAttribute(ExifInterface.TAG_USER_COMMENT, toJsonStr(sensor))
                    exif.saveAttributes()
                    file
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe ({
                    Toast.makeText(baseContext, "Saved!!", Toast.LENGTH_LONG).show()
                    System.out.println(">>>>> file : " + it.toString())
                    // FIXME make sure the Stream is closed after this callback
                }, { error ->
                    Toast.makeText(baseContext, "Error!! " + error.message, Toast.LENGTH_LONG).show()
                })
            }
        }
    }

    private fun toJsonStr(data: SensorData): String {
        return Gson().toJson(data).toString()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)
        camera.addCallback(cameraCallback)
        btn_take_photo.setOnClickListener {
            val rxPermissions = RxPermissions(this)
            rxPermissions
                .request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe({ granted ->
                    if (granted) {
                        camera.takePicture()
                    } else {
                        Toast.makeText(baseContext, "permission denied!!", Toast.LENGTH_LONG).show()
                    }
                })

        }
    }

    override fun onResume() {
        super.onResume()
        camera.start()
    }

    override fun onPause() {
        camera.stop()
        super.onPause()
        disposeBag.clear()
    }

    data class SensorData(
        @SerializedName("x") val x : Float,
        @SerializedName("y") val y : Float,
        @SerializedName("z") val z : Float)
}
