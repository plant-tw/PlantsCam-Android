package plantscam.android.prada.lab.plantscamera

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.BitmapFactory
import android.hardware.Camera
import android.os.Build
import android.os.Bundle
import android.support.constraint.ConstraintSet
import android.support.transition.TransitionManager
import android.support.v7.app.AppCompatActivity
import android.view.MotionEvent
import android.view.View
import android.webkit.WebView
import com.blankj.utilcode.util.NetworkUtils
import com.commonsware.cwac.camera.CameraHost
import com.commonsware.cwac.camera.CameraHostProvider
import com.commonsware.cwac.camera.SimpleCameraHost
import com.jakewharton.rxbinding2.view.RxView
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import kotlinx.android.synthetic.main.activity_camera.*
import plantscam.android.prada.lab.plantscamera.di.component.DaggerCameraComponent
import plantscam.android.prada.lab.plantscamera.utils.AnimUtils
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject

class CameraActivity : AppCompatActivity() {

    private val javaScriptLoadImages = "doc.loadImages();"
    @Inject
    lateinit var cameraViewModel : CameraViewModel

    private val cameraBuffSignal: PublishSubject<CameraPreviewData> = PublishSubject.create()
    private val cameraPreviewReadySignal: Subject<Camera.Size> = BehaviorSubject.create()
    private val takePhotoSignal: Subject<ByteArray> = PublishSubject.create()

    private val disposeBag = CompositeDisposable()

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)

        DaggerCameraComponent.builder()
            .appComponent((application as PlantsApplication).appComponent)
            .build()
            .inject(this)

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
            Observable.just(BitmapFactory.decodeResource(getResources(), R.drawable.test))
                    .delay(1, TimeUnit.SECONDS)
                    .repeat(10),
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
                updateDescription(it.label)
                refreshWebViewImage()
            })
    }

    private fun cameraViewEnable(enable: Boolean) {

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

    private fun updateDescription(plant: String) {
        if (NetworkUtils.isConnected()) {
            detail_page_offline.visibility = View.INVISIBLE
            detail_page.visibility = View.VISIBLE

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                webview.evaluateJavascript("doc.show(\"$plant\");", null)
            } else {
                webview.loadUrl("javascript:doc.show(\"$plant\");")
                webview.loadUrl("javascript:$javaScriptLoadImages")
            }
        } else {
            detail_page_offline.visibility = View.VISIBLE
            detail_page.visibility = View.INVISIBLE
            detail_title.text = plant
        }
    }

    override fun onPause() {
        cameraViewEnable(false)
        super.onPause()
        disposeBag.clear()
    }

    data class CameraPreviewData(
        val previewData: ByteArray,
        val width: Int,
        val height: Int
    )
}