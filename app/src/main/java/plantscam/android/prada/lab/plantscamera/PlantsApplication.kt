package plantscam.android.prada.lab.plantscamera

import android.app.Application

import plantscam.android.prada.lab.plantscamera.component.AppComponent
import plantscam.android.prada.lab.plantscamera.component.DaggerAppComponent
import plantscam.android.prada.lab.plantscamera.module.AppModule

import com.crashlytics.android.Crashlytics
import io.fabric.sdk.android.Fabric

/**
 * Created by prada on 24/02/2018.
 */

class PlantsApplication : Application() {
    var appComponent: AppComponent? = null
        private set

    override fun onCreate() {
        super.onCreate()

        appComponent = DaggerAppComponent.builder()
                .appModule(AppModule(this))
                .build()
        Fabric.with(this, Crashlytics())
    }
}
