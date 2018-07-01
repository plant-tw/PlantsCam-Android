package plantscam.android.prada.lab.plantscamera.module

import android.content.res.AssetManager
import android.util.Log

import com.google.firebase.ml.common.FirebaseMLException

import javax.inject.Singleton

import dagger.Module
import dagger.Provides
import io.reactivex.Single
import plantscam.android.prada.lab.plantscamera.ml.Classifier
import plantscam.android.prada.lab.plantscamera.ml.ImageMLKitClassifier

@Module
class MLModule {

    @Provides
    fun provideImageClassifier(assets: AssetManager): Single<Classifier> {
        return try {
            Single.just(ImageMLKitClassifier(assets) as Classifier)
        } catch (e: FirebaseMLException) {
            Single.error(e)
        }

    }
}
