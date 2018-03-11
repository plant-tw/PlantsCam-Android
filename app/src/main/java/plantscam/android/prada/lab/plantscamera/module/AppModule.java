package plantscam.android.prada.lab.plantscamera.module;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by prada on 24/02/2018.
 */
@Module
public class AppModule {

    private final Application mApp;

    public AppModule(Application application) {
        mApp = application;
    }

    @Provides
    @Singleton
    public SharedPreferences provideSharedPreferences() { // UserDefault
        return mApp.getSharedPreferences("a", Context.MODE_PRIVATE);
    }
}
