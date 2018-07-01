package plantscam.android.prada.lab.plantscamera.module;

import android.content.res.AssetManager;
import android.util.Log;

import com.google.firebase.ml.common.FirebaseMLException;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.reactivex.Single;
import plantscam.android.prada.lab.plantscamera.ml.Classifier;
import plantscam.android.prada.lab.plantscamera.ml.ImageMLKitClassifier;

@Module
public class MLModule {

    @Provides
    public Single<Classifier> provideImageClassifier(AssetManager assets) {
        try {
            return Single.just((Classifier) new ImageMLKitClassifier(assets));
        } catch (FirebaseMLException e) {
            return Single.error(e);
        }
    }
}
