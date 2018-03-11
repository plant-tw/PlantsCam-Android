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
public class NetworkModule {

    @Provides
    @Singleton
    public HttpClient provideHttpClient(HttpCache cache) {
        return new HttpClient();
        // return new FakeHttpClient()
    }

    @Provides
    @Singleton
    public HttpCache provideHttpCache() {
        return new HttpCache();
    }
}
