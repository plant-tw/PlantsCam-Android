package plantscam.android.prada.lab.plantscamera

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
//    @Inject lateinit var presenter: PlantsPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btn_select_photo.setOnClickListener {
//            presenter.pressButton()
        }

//        DaggerHomePageComponent.builder()
//            .appComponent((application as PlantsApplication).appComponent)
//            .homePresenterModule(HomePresenterModule())
//            .userCaseModule(UserCaseModule())
//            .build()
//            .inject(this)
    }

    private fun loadBitmapFromAsset(path: String): Bitmap? {
        val bm = BitmapFactory.decodeStream(assets.open(path))
        return Bitmap.createScaledBitmap(bm,
                PlantsClassifier.DIM_IMG_SIZE_X,
                PlantsClassifier.DIM_IMG_SIZE_Y, false)
    }
}