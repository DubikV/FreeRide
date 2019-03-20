package com.gmail.vanyadubik.freeride.utils

import android.app.Activity
import android.content.Context
import android.util.DisplayMetrics
import android.view.Display
import android.view.View
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import com.gmail.vanyadubik.freeride.R

class ScreenUtils(internal var ctx: Context) {
    internal var metrics: DisplayMetrics

    val height: Int
        get() = metrics.heightPixels

    val width: Int
        get() = metrics.widthPixels

    val realHeight: Int
        get() = metrics.heightPixels / metrics.densityDpi

    val realWidth: Int
        get() = metrics.widthPixels / metrics.densityDpi

    val density: Int
        get() = metrics.densityDpi

    init {
        val wm = ctx
            .getSystemService(Context.WINDOW_SERVICE) as WindowManager

        val display = wm.defaultDisplay
        metrics = DisplayMetrics()
        display.getMetrics(metrics)

    }

    fun getScale(picWidth: Int): Int {
        val display = (ctx.getSystemService(Context.WINDOW_SERVICE) as WindowManager)
            .defaultDisplay
        val width = display.width
        var `val`: Int = width / picWidth
        `val` = (`val`!! * 100.0).toInt()
        return `val`.toInt()
    }

    companion object {

        fun closeKeyboard(context: Context) {
            val view = (context as Activity).currentFocus
            if (view != null) {
                val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm?.hideSoftInputFromWindow(view.windowToken, 0)
            }
        }

        fun slideViewUp(context: Context, view: View) {

            val slide_up = AnimationUtils.loadAnimation(
                context.applicationContext,
                R.anim.slide_up
            )

            view.startAnimation(slide_up)
            slide_up.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation) {

                }

                override fun onAnimationEnd(animation: Animation) {
                    view.visibility = View.GONE
                }

                override fun onAnimationRepeat(animation: Animation) {

                }
            })

        }

        fun slideViewDown(context: Context, view: View) {

            view.visibility = View.VISIBLE

            val slide_down = AnimationUtils.loadAnimation(
                context.applicationContext,
                R.anim.slide_down
            )

            view.startAnimation(slide_down)
        }
    }


}