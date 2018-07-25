package plantscam.android.prada.lab.plantscamera.di.module

import android.content.res.AssetManager

import com.google.firebase.ml.common.FirebaseMLException

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
