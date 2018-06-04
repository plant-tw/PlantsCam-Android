package plantscam.android.prada.lab.plantscamera

import android.Manifest.permission.CAMERA
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.tbruyelle.rxpermissions2.RxPermissions
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btn_start.setOnClickListener {
            val rxPermissions = RxPermissions(this)
            rxPermissions
                .request(CAMERA)
                .subscribe({ granted ->
                    if (granted) {
                        startActivity(Intent(this, CameraActivity::class.java))
                    } else {
                        Toast.makeText(this, "permission denied!!", Toast.LENGTH_LONG).show()
                    }
                })

//             //  Test TensorFlow
//            Observable.fromCallable {
//                val bm = BitmapFactory.decodeStream(assets.open("data/3.jpg"))
//                Bitmap.createScaledBitmap(bm,
//                    PlantFreezeClassifier2.INPUT_W,
//                    PlantFreezeClassifier2.INPUT_H, false)
//            }
//            .map { PlantFreezeClassifier2.convertTo(it) }
//            // .map { classifier.run(it, fakeLenPixel, fakeLenCm) }
//            .map { classifier.run(it) }
//            .subscribeOn(Schedulers.computation())
//            .observeOn(AndroidSchedulers.mainThread())
//            .subscribe {
//                Toast.makeText(this, "detected result = " + it, Toast.LENGTH_LONG).show()
//            }
        }
    }

}