package plantscam.android.prada.lab.plantscamera.module;

import dagger.Module;
import dagger.Provides;
import io.reactivex.Single;
import plantscam.android.prada.lab.plantscamera.CameraViewModel;
import plantscam.android.prada.lab.plantscamera.ml.Classifier;

@Module
public class CameraModule {

    @Provides
    public CameraViewModel provideCameraViewModel(Single<Classifier> classifier) {
        return new CameraViewModel(classifier);
    }
}
