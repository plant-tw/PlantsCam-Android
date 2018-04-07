package plantscam.android.prada.lab.plantscamera

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import android.widget.Toast
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.nio.ByteBuffer
import java.nio.FloatBuffer


class MainActivity : AppCompatActivity() {
//    @Inject lateinit var presenter: PlantsPresenter

    private val fakeCameraPixels = FloatArray(128 * 128 * 3)
    private val buffer = IntArray(128 * 128)
    private val pixelBuffer = FloatArray(128 * 128 * 3)
    private val fakeLenCm = 0.42631600000000003f
    private val fakeLenPixel = 0.2f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btn_select_photo.setOnClickListener {
//            val rxPermissions = RxPermissions(this)
//            rxPermissions
//                .request(Manifest.permission.CAMERA)
//                .subscribe({ granted ->
//                    if (granted) {
//                        startActivity(Intent(baseContext, CameraActivity::class.java))
//                    } else {
//                        Toast.makeText(baseContext, "permission denied!!", Toast.LENGTH_LONG).show()
//                    }
//                })
            loadBitmapFromAsset("data/1.jpg")
                .map {
                    // bitmap pixel to float[]
                    it.getPixels(buffer, 0, 128, 0, 0, 128, 128)
//                    val greenStep = 128 * 128
//                    val blueStep = greenStep * 2
                    for (i in 0 until buffer.size) {
                        // Set 0 for white and 255 for black pixel
                        val pix = buffer[i]
                        pixelBuffer[3 * i] = Color.red(pix).toFloat()
                        pixelBuffer[3 * i + 1] = Color.green(pix).toFloat()
                        pixelBuffer[3 * i + 2] = Color.blue(pix).toFloat()
                        // FIXME need to confirm the way to put the pixels
//                        pixelBuffer[i] = Color.red(pix).toFloat()
//                        pixelBuffer[greenStep + i] = Color.green(pix).toFloat()
//                        pixelBuffer[blueStep + i] = Color.blue(pix).toFloat()
                    }
                    pixelBuffer
                }
//            Observable.just(fakeCameraPixels)
                .map {
                    // PlantsClassifier(baseContext).classifyFrame(loadBitmapFromAsset("data/1.jpg"))
                    PlantFreezeClassifier(assets).run(it, fakeLenPixel, fakeLenCm)
                }
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    Toast.makeText(baseContext, "detected result = " + it, Toast.LENGTH_LONG).show()
                }


        }

//        DaggerHomePageComponent.builder()
//            .appComponent((application as PlantsApplication).appComponent)
//            .homePresenterModule(HomePresenterModule())
//            .userCaseModule(UserCaseModule())
//            .build()
//            .inject(this)
    }

//    private fun loadBitmapFromAsset(path: String): Bitmap? {
//        val bm = BitmapFactory.decodeStream(assets.open(path))
//        return Bitmap.createScaledBitmap(bm,
//                PlantsClassifier.DIM_IMG_SIZE_X,
//                PlantsClassifier.DIM_IMG_SIZE_Y, false)
//    }

    private fun loadBitmapFromAsset(path: String): Observable<Bitmap> {
        val bm = BitmapFactory.decodeStream(assets.open(path))
        return Observable.just(Bitmap.createScaledBitmap(bm,
            PlantsClassifier.DIM_IMG_SIZE_X,
            PlantsClassifier.DIM_IMG_SIZE_Y, false))
    }
}