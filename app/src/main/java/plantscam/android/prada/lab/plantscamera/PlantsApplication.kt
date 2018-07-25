package plantscam.android.prada.lab.plantscamera

import android.app.Application

import plantscam.android.prada.lab.plantscamera.di.component.AppComponent
import plantscam.android.prada.lab.plantscamera.component.DaggerAppComponent
import plantscam.android.prada.lab.plantscamera.di.module.AppModule

import com.crashlytics.android.Crashlytics
import io.fabric.sdk.android.Fabric

/**
 * Created by prada on 24/02/2018.
 */

class PlantsApplication : Application() {
    lateinit var appComponent: AppComponent

    override fun onCreate() {
        super.onCreate()

        appComponent = DaggerAppComponent.builder()
            .appModule(AppModule(this))
            .build()
        Fabric.with(this, Crashlytics())
    }
}
