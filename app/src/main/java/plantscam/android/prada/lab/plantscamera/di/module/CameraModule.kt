package plantscam.android.prada.lab.plantscamera.di.module

import dagger.Module
import dagger.Provides
import io.reactivex.Single
import plantscam.android.prada.lab.plantscamera.CameraViewModel
import plantscam.android.prada.lab.plantscamera.ml.Classifier

@Module
class CameraModule {

    @Provides
    fun provideCameraViewModel(classifier: Single<Classifier>): CameraViewModel {
        return CameraViewModel(classifier)
    }
}
