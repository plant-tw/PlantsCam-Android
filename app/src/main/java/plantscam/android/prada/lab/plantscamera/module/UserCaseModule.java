package plantscam.android.prada.lab.plantscamera.module;

import dagger.Module;
import dagger.Provides;
import plantscam.android.prada.lab.plantscamera.UserScope;
import plantscam.android.prada.lab.plantscamera.implementation.FakeUseCase;
import plantscam.android.prada.lab.plantscamera.implementation.IUseCase;

/**
 * Created by prada on 25/02/2018.
 */

@Module
public class UserCaseModule {
    @UserScope
    @Provides
    public IUseCase provideUseCase() {
        return new FakeUseCase();
    }
}
