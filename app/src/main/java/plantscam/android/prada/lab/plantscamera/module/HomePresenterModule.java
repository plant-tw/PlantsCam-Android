package plantscam.android.prada.lab.plantscamera.module;

import android.content.SharedPreferences;

import java.util.logging.Logger;

import dagger.Module;
import dagger.Provides;
import plantscam.android.prada.lab.plantscamera.PlantsPresenter;
import plantscam.android.prada.lab.plantscamera.UserScope;
import plantscam.android.prada.lab.plantscamera.implementation.IUseCase;
import plantscam.android.prada.lab.plantscamera.implementation.PlantsRestfulAPI;

/**
 * Created by prada on 25/02/2018.
 */

@Module
public class HomePresenterModule {
    @Provides
    @UserScope
    PlantsPresenter providePresenter(SharedPreferences preferences,
                                     PlantsRestfulAPI server,
                                     IUseCase useCase,
                                     Logger logger) {
        return new PlantsPresenter(preferences, server, useCase);
    }
}
