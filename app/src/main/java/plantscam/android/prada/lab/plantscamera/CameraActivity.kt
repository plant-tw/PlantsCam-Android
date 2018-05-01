package plantscam.android.prada.lab.plantscamera

import android.Manifest
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.google.android.cameraview.CameraView
import com.tbruyelle.rxpermissions2.RxPermissions
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import kotlinx.android.synthetic.main.activity_camera.*

class CameraActivity : AppCompatActivity() {

    lateinit var cameraViewModel : CameraViewModel
    val cameraBuffSignal: Subject<ByteArray> = BehaviorSubject.create()
    val takePhotoSignal: Subject<ByteArray> = PublishSubject.create()

    private val disposeBag = CompositeDisposable()
    private val cameraCallback = object: CameraView.Callback() {
        override fun onPreviewFrame(cameraView: CameraView?, data: ByteArray?, width: Int, height: Int, format: Int) {
            data?.let { cameraBuffSignal.onNext(it) }
        }
        override fun onPictureTaken(cameraView: CameraView?, jpegData: ByteArray?) {
            jpegData?.let { takePhotoSignal.onNext(it) }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)

        cameraViewModel = CameraViewModel(assets)

        camera.addCallback(cameraCallback)
        btn_take_photo.setOnClickListener {
            val rxPermissions = RxPermissions(this)
            disposeBag.add(rxPermissions
                .request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe({ granted ->
                    if (granted) {
                        camera.takePicture()
                    } else {
                        Toast.makeText(baseContext, "permission denied!!", Toast.LENGTH_LONG).show()
                    }
                }))
        }
    }

    override fun onResume() {
        super.onResume()
        camera.start()

        cameraViewModel.bindIntents(cameraBuffSignal, takePhotoSignal)

        disposeBag.add(cameraViewModel.render()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                // TODO display result
            })
    }

    override fun onPause() {
        camera.stop()
        super.onPause()
        cameraViewModel.unbindIntents()
        disposeBag.clear()
    }
}
