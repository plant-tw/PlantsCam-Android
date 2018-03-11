package plantscam.android.prada.lab.plantscamera

import android.Manifest
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import android.Manifest.permission
import android.widget.Toast
import com.tbruyelle.rxpermissions2.RxPermissions


class MainActivity : AppCompatActivity() {
//    @Inject lateinit var presenter: PlantsPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btn_select_photo.setOnClickListener {
            val rxPermissions = RxPermissions(this)
            rxPermissions
                .request(Manifest.permission.CAMERA)
                .subscribe({ granted ->
                    if (granted) {
                        startActivity(Intent(baseContext, CameraActivity::class.java))
                    } else {
                        Toast.makeText(baseContext, "permission denied!!", Toast.LENGTH_LONG).show()
                    }
                })

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