package plantscam.android.prada.lab.plantscamera.utils

import android.support.v4.view.ViewCompat
import android.view.MotionEvent
import android.view.View


class AnimUtils {
    companion object {
        fun rotate(view: View, o: Float) {
            var o = o
            if (o == 270f && view.rotation == 0f) {
                view.rotation = 360f
            } else if (view.rotation == 270f && o == 0f) {
                o = 360f
            }
            ViewCompat.animate(view).rotation(o).setDuration(200).start()
        }

        fun delete(view: View?, end: Runnable?) {
            if (view == null) {
                end?.run()
                return
            }
            val sx = view.scaleX
            val sy = view.scaleY
            val tx = view.translationX
            val ty = view.translationY
            val r = view.rotation

            ViewCompat.animate(view)
                .alpha(0f)
                .rotation(view.rotation - 90)
                .scaleX(0.1f).scaleY(0.1f)
                .translationX(-(view.width / 2f))
                .translationY(view.height * 2f)
                .setDuration(200).withEndAction {
                    // recover view state
                    view.scaleX = sx
                    view.scaleY = sy
                    view.translationX = tx
                    view.translationY = ty
                    view.rotation = r
                    view.alpha = 1f
                    end?.run()
                }.start()
        }

        fun showFocusRect(v: View, event: MotionEvent) {
            v.x = event.x
            v.y = event.y
            v.scaleX = 1.4f
            v.scaleY = 1.4f
            v.alpha = 1f

            v.visibility = View.VISIBLE
            ViewCompat.animate(v)
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(200)
                .withEndAction {
                    ViewCompat.animate(v)
                        .rotation(v.rotation + 120)
                        .scaleX(0.9f)
                        .scaleY(0.9f)
                        .setDuration(600)
                        .withEndAction {
                            ViewCompat.animate(v)
                                .rotation(v.rotation - 120)
                                .scaleX(1f)
                                .scaleY(1f)
                                .setDuration(600)
                                .withEndAction {
                                    ViewCompat.animate(v)
                                        .alpha(0f)
                                        .setDuration(200)
                                        .withEndAction { v.visibility = View.INVISIBLE }
                                }
                        }
                }
        }
    }
}