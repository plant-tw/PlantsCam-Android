package plantscam.android.prada.lab.plantscamera

import android.annotation.SuppressLint
import android.content.Context
import android.hardware.Camera
import android.os.Build
import android.os.Bundle
import android.support.constraint.ConstraintSet
import android.support.transition.TransitionManager
import android.support.v7.app.AppCompatActivity
import android.view.MotionEvent
import android.webkit.WebView
import com.commonsware.cwac.camera.CameraHost
import com.commonsware.cwac.camera.CameraHostProvider
import com.commonsware.cwac.camera.SimpleCameraHost
import com.jakewharton.rxbinding2.view.RxView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import kotlinx.android.synthetic.main.activity_camera.*
import plantscam.android.prada.lab.plantscamera.di.component.DaggerCameraComponent
import plantscam.android.prada.lab.plantscamera.utils.AnimUtils
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject

class CameraActivity : AppCompatActivity(), CameraHostProvider {

    private val javaScriptLoadImages = "doc.loadImages();"
    @Inject
    lateinit var cameraViewModel : CameraViewModel

    private val cameraBuffSignal: PublishSubject<CameraPreviewData> = PublishSubject.create()
    private val cameraPreviewReadySignal: Subject<Camera.Size> = BehaviorSubject.create()
    private val takePhotoSignal: Subject<ByteArray> = PublishSubject.create()
    private val myHost: MyHost by lazy {
        MyHost(this)
    }

    private val disposeBag = CompositeDisposable()

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)

        DaggerCameraComponent.builder()
            .appComponent((application as PlantsApplication).appComponent)
            .build()
            .inject(this)

        camera.setPreviewCallback { data, camera ->
            val size = camera.parameters.previewSize
            data?.let { cameraBuffSignal.onNext(CameraPreviewData(it, size.width, size.height)) }
        }
        camera.setOnTouchListener { _, event ->
            setCameraFocus(event)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true)
        }

        webview.loadUrl("https://plant-tw.github.io/PlantsData/")
        webview.settings.javaScriptEnabled = true
    }

    override fun onResume() {
        super.onResume()
        cameraViewEnable(true)

        disposeBag.add(cameraViewModel.bindIntents(
            cameraBuffSignal,
            cameraPreviewReadySignal,
            takePhotoSignal,
            RxView.clicks(btn_detail_page)))

        disposeBag.add(cameraViewModel.pickerUiEvent()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { isCollapsed ->
                changeDetailPage(isCollapsed)
                refreshWebViewImage()
                // FIXME it might has the performance issue, I will like to solve it in ViewModel layer
//                val enable = !isCollapsed
//                cameraViewEnable(enable)
            })

        disposeBag.add(cameraViewModel.classifierUiEvent()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                updateWebView(it.label)
                refreshWebViewImage()
            })
    }

    private fun cameraViewEnable(enable: Boolean) {
        if (enable) {
            camera.onResume()
            camera.restartPreview()
        } else {
            camera.onPause()
        }
    }

    private fun changeDetailPage(isCollapsed: Boolean) {
        TransitionManager.beginDelayedTransition(root_constraint_layout)
        val set = ConstraintSet()
        set.clone(root_constraint_layout)
        if (isCollapsed) {
            set.setGuidelinePercent(R.id.guide_top_of_picker, 0.2f)
        } else {
            set.setGuidelinePercent(R.id.guide_top_of_picker, 0.8f)
        }
        set.applyTo(root_constraint_layout)
    }

    private fun refreshWebViewImage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            webview.evaluateJavascript(javaScriptLoadImages) {
                // do nothing
            }
        } else {
            webview.loadUrl("javascript:$javaScriptLoadImages")
        }
    }

    private fun updateWebView(plant: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            webview.evaluateJavascript("doc.show(\"$plant\");", null)
        } else {
            webview.loadUrl("javascript:doc.show(\"$plant\");")
            webview.loadUrl("javascript:$javaScriptLoadImages")
        }
    }

    override fun onPause() {
        cameraViewEnable(false)
        super.onPause()
        disposeBag.clear()
    }

    private fun setCameraFocus(event: MotionEvent) : Boolean {
        if (myHost.isFocusable()) {
            try {
                camera.setCameraFocus(event)
                AnimUtils.showFocusRect(focus_rect, event.x, event.y)
                return true
            } catch (e: RuntimeException) { }
        }
        return false
    }

    override fun getCameraHost(): CameraHost {
        return myHost
    }

    data class CameraPreviewData(
        val previewData: ByteArray,
        val width: Int,
        val height: Int
    )

    // add more configuration later
    inner class MyHost(ctx: Context) : SimpleCameraHost(ctx) {
        var _cameraId : Int = -1
        val isAutoFocusAvaliable = AtomicBoolean(false)

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


        override fun autoFocusAvailable() {
            isAutoFocusAvaliable.set(true)
        }

        override fun autoFocusUnavailable() {
            isAutoFocusAvaliable.set(false)
        }

        fun isFocusable(): Boolean {
            return isAutoFocusAvaliable.get()
        }
    }
}