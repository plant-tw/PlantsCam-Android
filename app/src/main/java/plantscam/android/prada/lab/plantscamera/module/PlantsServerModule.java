package plantscam.android.prada.lab.plantscamera.module;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import plantscam.android.prada.lab.plantscamera.implementation.HttpClient;
import plantscam.android.prada.lab.plantscamera.implementation.PlantsRestfulAPI;

/**
 * Created by prada on 24/02/2018.
 */

@Module
public class PlantsServerModule {
    @Provides
    @Singleton
    public PlantsRestfulAPI provideResultApi(HttpClient client) {
        return new PlantsRestfulAPI(client);
    }
}
