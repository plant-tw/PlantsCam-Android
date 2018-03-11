package plantscam.android.prada.lab.plantscamera.component;

import android.content.SharedPreferences;

import java.util.logging.Logger;

import javax.inject.Singleton;

import dagger.Component;
import plantscam.android.prada.lab.plantscamera.implementation.HttpCache;
import plantscam.android.prada.lab.plantscamera.implementation.HttpClient;
import plantscam.android.prada.lab.plantscamera.implementation.PlantsRestfulAPI;
import plantscam.android.prada.lab.plantscamera.module.AppModule;
import plantscam.android.prada.lab.plantscamera.module.NetworkModule;
import plantscam.android.prada.lab.plantscamera.module.PlantsServerModule;

/**
 * Created by prada on 25/02/2018.
 */
@Singleton
@Component(modules = {AppModule.class, NetworkModule.class, PlantsServerModule.class})
public interface AppComponent {
    SharedPreferences preference();
    HttpCache cache();
    HttpClient client();
    PlantsRestfulAPI server();
}
