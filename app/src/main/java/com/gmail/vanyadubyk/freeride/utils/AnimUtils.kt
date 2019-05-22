package com.gmail.vanyadubyk.freeride.utils

import android.animation.ObjectAnimator
import android.animation.TimeInterpolator
import android.view.View
import android.view.ViewTreeObserver
import android.view.animation.AccelerateInterpolator
import android.view.animation.Animation
import android.view.animation.DecelerateInterpolator
import android.view.animation.Transformation
import android.widget.LinearLayout
import com.gmail.vanyadubyk.freeride.R

object AnimUtils {

    private val ANIM_DURATION = 200
    private var mLeftDelta: Int = 0
    private var mTopDelta: Int = 0
    private var mWidthScale: Float = 0.toFloat()
    private var mHeightScale: Float = 0.toFloat()

    fun enterAnimation(viewParent: View, viewChild: View) {

        val observer = viewChild.viewTreeObserver
        observer.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {

            override fun onPreDraw(): Boolean {
                viewChild.viewTreeObserver.removeOnPreDrawListener(this)

                val screenLocation1 = IntArray(2)
                viewParent.getLocationOnScreen(screenLocation1)

                val thumbnailTop = screenLocation1[1]
                val thumbnailLeft = screenLocation1[0]
                val thumbnailWidth = viewParent.width
                val thumbnailHeight = viewParent.height

                val screenLocation = IntArray(2)
                viewChild.getLocationOnScreen(screenLocation)

                mLeftDelta = thumbnailLeft - screenLocation[0]
                mTopDelta = thumbnailTop - screenLocation[1]
                mWidthScale = thumbnailWidth.toFloat() / viewChild.width
                mHeightScale = thumbnailHeight.toFloat() / viewChild.height

                viewChild.pivotX = 0f
                viewChild.pivotY = 0f
                viewChild.scaleX = mWidthScale
                viewChild.scaleY = mHeightScale
                viewChild.translationX = mLeftDelta.toFloat()
                viewChild.translationY = mTopDelta.toFloat()

                // interpolator where the rate of change starts out quickly and then decelerates.
                val sDecelerator = DecelerateInterpolator()

                // Animate scale and translation to go from thumbnail to full size
                viewChild.animate().setDuration(ANIM_DURATION.toLong()).scaleX(1f).scaleY(1f).translationX(0f)
                    .translationY(0f).interpolator = sDecelerator

                val bgAnim = ObjectAnimator.ofInt(R.color.colorTrans, "alpha", 0, 255)
                bgAnim.duration = ANIM_DURATION.toLong()
                bgAnim.start()

                return true
            }
        })
    }

    fun exitAnimation(view: View, endAction: Runnable) {

        val sInterpolator = AccelerateInterpolator()
        view.animate().setDuration(ANIM_DURATION.toLong()).scaleX(mWidthScale).scaleY(mHeightScale)
            .translationX(mLeftDelta.toFloat()).translationY(mTopDelta.toFloat())
            .setInterpolator(sInterpolator).withEndAction(endAction)

        // Fade out background
        val bgAnim = ObjectAnimator.ofInt(R.color.colorTrans, "alpha", 0)
        bgAnim.duration = ANIM_DURATION.toLong()
        bgAnim.start()
    }

    fun expand(v: View) {
        v.measure(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        val targetHeight = v.measuredHeight

        // Older versions of android (pre API 21) cancel animations for views with a height of 0.
        v.layoutParams.height = 1
        v.visibility = View.VISIBLE
        val a = object : Animation() {
            override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
                v.layoutParams.height = if (interpolatedTime == 1f)
                    LinearLayout.LayoutParams.WRAP_CONTENT
                else
                    (targetHeight * interpolatedTime).toInt()
                v.requestLayout()
            }

            override fun willChangeBounds(): Boolean {
                return true
            }
        }

        // 1dp/ms
        a.duration = (targetHeight / v.context.resources.displayMetrics.density).toInt().toLong()
        v.startAnimation(a)
    }

    fun collapse(v: View) {
        val initialHeight = v.measuredHeight

        val a = object : Animation() {
            override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
                if (interpolatedTime == 1f) {
                    v.visibility = View.GONE
                } else {
                    v.layoutParams.height = initialHeight - (initialHeight * interpolatedTime).toInt()
                    v.requestLayout()
                }
            }

            override fun willChangeBounds(): Boolean {
                return true
            }
        }

        // 1dp/ms
        a.duration = (initialHeight / v.context.resources.displayMetrics.density).toInt().toLong()
        v.startAnimation(a)
    }
}
