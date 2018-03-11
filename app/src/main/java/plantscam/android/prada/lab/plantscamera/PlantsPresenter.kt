package plantscam.android.prada.lab.plantscamera

import android.content.SharedPreferences
import plantscam.android.prada.lab.plantscamera.implementation.IUseCase
import plantscam.android.prada.lab.plantscamera.implementation.PlantsRestfulAPI

/**
 * Created by prada on 25/02/2018.
 */

open class PlantsPresenter(
        var preference: SharedPreferences,
        var server: PlantsRestfulAPI,
        var usecase : IUseCase) {

    fun pressButton() {
//        server.loadData().continue { data ->
//
//            preference.edit().putString("", ).apply()
//            // usecase.doSomething(data)
//        }

    }
}
