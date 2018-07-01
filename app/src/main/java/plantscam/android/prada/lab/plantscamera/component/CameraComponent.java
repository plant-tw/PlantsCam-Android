package plantscam.android.prada.lab.plantscamera.component;

import dagger.Component;
import plantscam.android.prada.lab.plantscamera.CameraActivity;
import plantscam.android.prada.lab.plantscamera.UserScope;
import plantscam.android.prada.lab.plantscamera.module.CameraModule;
import plantscam.android.prada.lab.plantscamera.module.MLModule;

/**
 * Created by prada on 25/02/2018.
 */

@UserScope
@Component(dependencies = AppComponent.class, modules = {CameraModule.class, MLModule.class})
public interface CameraComponent {
    void inject(CameraActivity activity);
}
