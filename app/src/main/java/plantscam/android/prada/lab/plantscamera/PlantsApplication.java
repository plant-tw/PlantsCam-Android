package plantscam.android.prada.lab.plantscamera;

import android.app.Application;

import plantscam.android.prada.lab.plantscamera.component.AppComponent;
import plantscam.android.prada.lab.plantscamera.component.DaggerAppComponent;
import plantscam.android.prada.lab.plantscamera.module.AppModule;
import plantscam.android.prada.lab.plantscamera.module.NetworkModule;
import plantscam.android.prada.lab.plantscamera.module.PlantsServerModule;

/**
 * Created by prada on 24/02/2018.
 */

public class PlantsApplication extends Application {
    private AppComponent mCompoent;

    @Override
    public void onCreate() {
        super.onCreate();

        mCompoent = DaggerAppComponent.builder()
            .appModule(new AppModule(this))
            .networkModule(new NetworkModule())
            .plantsServerModule(new PlantsServerModule())
//            .logger(new FakeLogger())
            .build();
    }

    public AppComponent getAppComponent() {
        return mCompoent;
    }
}