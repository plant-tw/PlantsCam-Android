package plantscam.android.prada.lab.plantscamera

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
//    @Inject lateinit var presenter: PlantsPresenter

    private val fakeLenCm = 0.42631600000000003f
    private val fakeLenPixel = 0.2f
    private lateinit var classifier: PlantFreezeClassifier2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        classifier = PlantFreezeClassifier2(assets)

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
            loadBitmapFromAsset("data/3.jpg")
                .map { PlantFreezeClassifier2.convertTo(it) }
//            Observable.just(fakeCameraPixels)
                .map { classifier.run(it) }
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