package plantscam.android.prada.lab.plantscamera.di.module

import android.content.res.AssetManager
import com.cardinalblue.androidx.util.toBytes

import com.google.firebase.ml.common.FirebaseMLException

import dagger.Module
import dagger.Provides
import io.reactivex.Single
import plantscam.android.prada.lab.plantscamera.ml.Classifier
import plantscam.android.prada.lab.plantscamera.ml.ImageMLKitClassifier
import java.io.IOException

@Module
class MLModule {

    @Provides
    fun provideImageClassifier(assets: AssetManager): Single<Classifier> {
        return try {
            Single.just(ImageMLKitClassifier(fileLoader = { filename ->
                return@ImageMLKitClassifier try {
                    String(assets.open(filename).toBytes())
                } catch (e: IOException) {
                    String()
                }
            }) as Classifier)
        } catch (e: FirebaseMLException) {
            Single.error(e)
        }
    }
}
