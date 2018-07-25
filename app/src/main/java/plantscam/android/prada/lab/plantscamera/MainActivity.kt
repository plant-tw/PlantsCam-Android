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
            RxPermissions(this)
                .request(CAMERA)
                .subscribe { granted ->
                    if (granted) {
                        startActivity(Intent(this, CameraActivity::class.java))
                    } else {
                        Toast.makeText(this, "permission denied!!", Toast.LENGTH_LONG).show()
                    }
                }
        }
    }
}