package plantscam.android.prada.lab.plantscamera.di.component

import dagger.Component
import plantscam.android.prada.lab.plantscamera.CameraActivity
import plantscam.android.prada.lab.plantscamera.UserScope
import plantscam.android.prada.lab.plantscamera.di.module.CameraModule
import plantscam.android.prada.lab.plantscamera.di.module.MLModule

/**
 * Created by prada on 25/02/2018.
 */

@UserScope
@Component(dependencies = arrayOf(AppComponent::class), modules = arrayOf(CameraModule::class, MLModule::class))
interface CameraComponent {
    fun inject(activity: CameraActivity)
}
