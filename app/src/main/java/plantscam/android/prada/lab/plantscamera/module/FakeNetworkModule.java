package plantscam.android.prada.lab.plantscamera.module;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import plantscam.android.prada.lab.plantscamera.implementation.HttpCache;
import plantscam.android.prada.lab.plantscamera.implementation.HttpClient;

/**
 * Created by prada on 24/02/2018.
 */

@Module
public class FakeNetworkModule {

    @Provides
    @Singleton
    public HttpClient provideHttpClient(HttpCache cache) {
        return new HttpClient();
    }

    @Provides
    @Singleton
    public HttpCache provideHttpCache() {
        return new HttpCache();
    }
}
