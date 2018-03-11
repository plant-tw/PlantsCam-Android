package plantscam.android.prada.lab.plantscamera.module;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import plantscam.android.prada.lab.plantscamera.implementation.PlantsStore;

/**
 * Created by prada on 24/02/2018.
 */
@Module
public class StoreModule {

    @Provides
    @Singleton
    public PlantsStore provideStore() {
        return new PlantsStore();
    }
}
