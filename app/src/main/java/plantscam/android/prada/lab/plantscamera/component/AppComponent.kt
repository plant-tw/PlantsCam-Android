package plantscam.android.prada.lab.plantscamera.component

import android.content.SharedPreferences
import android.content.res.AssetManager

import javax.inject.Singleton

import dagger.Component
import plantscam.android.prada.lab.plantscamera.module.AppModule

/**
 * Created by prada on 25/02/2018.
 */
@Singleton
@Component(modules = arrayOf(AppModule::class))
interface AppComponent {
    fun preference(): SharedPreferences
    fun assets(): AssetManager
}
