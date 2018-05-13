package plantscam.android.prada.lab.plantscamera.component;

import dagger.Component;
import plantscam.android.prada.lab.plantscamera.MainActivity;
import plantscam.android.prada.lab.plantscamera.UserScope;
import plantscam.android.prada.lab.plantscamera.module.UserCaseModule;

/**
 * Created by prada on 25/02/2018.
 */

@UserScope
@Component(dependencies = AppComponent.class, modules = {UserCaseModule.class})
public interface HomePageComponent {
    void inject(MainActivity activity);
}
