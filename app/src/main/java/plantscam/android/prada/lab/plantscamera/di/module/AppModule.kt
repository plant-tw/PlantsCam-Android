package plantscam.android.prada.lab.plantscamera.di.module

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.content.res.AssetManager

import javax.inject.Singleton

import dagger.Module
import dagger.Provides

/**
 * Created by prada on 24/02/2018.
 */
@Module
class AppModule(private val mApp: Application) {

    @Provides
    @Singleton
    fun provideSharedPreferences(): SharedPreferences { // UserDefault
        return mApp.getSharedPreferences("a", Context.MODE_PRIVATE)
    }

    @Provides
    @Singleton
    fun provideAssetManager(): AssetManager {
        return mApp.assets
    }
}
