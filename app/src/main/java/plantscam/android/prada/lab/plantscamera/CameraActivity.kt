package plantscam.android.prada.lab.plantscamera

import android.Manifest
import android.content.Context
import android.hardware.Camera
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.commonsware.cwac.camera.CameraHost
import com.commonsware.cwac.camera.CameraHostProvider
import com.commonsware.cwac.camera.SimpleCameraHost
import com.tbruyelle.rxpermissions2.RxPermissions
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import kotlinx.android.synthetic.main.activity_camera.*


class CameraActivity : AppCompatActivity(), CameraHostProvider {

    private lateinit var cameraViewModel : CameraViewModel
    val cameraBuffSignal: Subject<CameraPreviewData> = BehaviorSubject.create()
    val cameraPreviewReadySignal: Subject<Camera.Size> = BehaviorSubject.create()
    val takePhotoSignal: Subject<ByteArray> = PublishSubject.create()
    private var myHost: MyHost? = null

    private val disposeBag = CompositeDisposable()
//    private val cameraCallback = object: CameraView.Callback() {
//        override fun onPreviewFrame(cameraView: CameraView?, data: ByteArray?, width: Int, height: Int, format: Int) {
//            data?.let { cameraBuffSignal.onNext(CameraPreviewData(it, width, height)) }
//        }
//        override fun onPictureTaken(cameraView: CameraView?, jpegData: ByteArray?) {
//            jpegData?.let { takePhotoSignal.onNext(it) }
//        }
//    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)

        cameraViewModel = CameraViewModel(assets)

        camera.setPreviewCallback { data, camera ->
            val size = camera.parameters.previewSize
            data?.let { cameraBuffSignal.onNext(CameraPreviewData(it, size.width, size.height)) }

        }
        btn_take_photo.setOnClickListener {
            val rxPermissions = RxPermissions(this)
            disposeBag.add(rxPermissions
                .request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe({ granted ->
                    if (granted) {
                        camera.takePicture(false, true)
                    } else {
                        Toast.makeText(baseContext, "permission denied!!", Toast.LENGTH_LONG).show()
                    }
                }))
        }
    }

    override fun onResume() {
        super.onResume()
        camera.onResume()
        camera.restartPreview()

        cameraViewModel.bindIntents(cameraBuffSignal, cameraPreviewReadySignal, takePhotoSignal)

        disposeBag.add(cameraViewModel.render()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                text_result.text = it.classification.label + " (" + it.classification.conf + ")"
            })
    }

    override fun onPause() {
        camera.onPause()
        super.onPause()
        cameraViewModel.unbindIntents()
        disposeBag.clear()
    }

    override fun getCameraHost(): CameraHost {
        if (myHost == null) {
            myHost = MyHost(this)
        }
        return myHost!!
    }

    data class CameraPreviewData(
        val previewData: ByteArray,
        val width: Int,
        val height: Int
    )

    // add more configuration later
    inner class MyHost(ctx: Context) : SimpleCameraHost(ctx) {
        var _cameraId : Int = -1

        override fun getCameraId(): Int {
            if (_cameraId == -1) {
                initCameraId()
            }
            return _cameraId
        }

        override fun getPreviewSize(displayOrientation: Int,
                                    width: Int,
                                    height: Int,
                                    parameters: Camera.Parameters): Camera.Size? {
            previewSize = getOptimalPreviewSize(parameters.supportedPreviewSizes, width / 2, height / 2)
            cameraPreviewReadySignal.onNext(previewSize)
            return previewSize
        }

        private fun initCameraId() {
            val count = Camera.getNumberOfCameras()
            var result = -1
            if (count > 0) {
                result = 0 // if we have a camera, default to this one
                val info = Camera.CameraInfo()
                for (i in 0 until count) {
                    Camera.getCameraInfo(i, info)
                    if (info.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                        result = i
                        break
                    }
                }
            }
            _cameraId = result
        }

        override fun getRecordingHint(): CameraHost.RecordingHint {
            return CameraHost.RecordingHint.NONE
        }
    }


}
