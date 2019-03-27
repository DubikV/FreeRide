package com.gmail.vanyadubik.freeride.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.Transformation
import com.gmail.vanyadubik.freeride.R

import java.util.ArrayList

class HorizontalDottedProgress : View {

    private val mDotRadius = 12

    private val mBounceDotRadius = 16

    private var mDotPosition: Int = 0

    private var paintList: MutableList<Paint>? = null

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    fun setPaintList(paintList: MutableList<Paint>) {
        this.paintList = paintList
    }

    fun showAnimation() {
        init()
        mDotPosition = 0
        startAnimation()
    }

    fun hideAnimation() {
        stopAnimation()
        mDotPosition = paintList!!.size
        init()
    }

    private fun init() {
        if (paintList == null) {
            paintList = ArrayList()
            val paint1 = Paint()
            paint1.color = context?.resources?.getColor(R.color.weirdGreen)!!
            paintList!!.add(paint1)

            val paint2 = Paint()
            paint2.color = context?.resources?.getColor(R.color.bananaYellow)!!
            paintList!!.add(paint2)

            val paint3 = Paint()
            paint3.color = context?.resources?.getColor(R.color.coral)!!
            paintList!!.add(paint3)
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)



        createDot(canvas)
    }

    private fun createDot(canvas: Canvas) {

        //        for(int i = 0; i < mDotAmount; i++ ){
        //            if(i == mDotPosition){
        //                canvas.drawCircle(15+(i*30), mBounceDotRadius, mBounceDotRadius, paintList.get(i));
        //            }else {
        //                canvas.drawCircle(15+(i*30), mBounceDotRadius, mDotRadius, paintList.get(i));
        //            }
        //        }

        var pos: Int

        for (paint in paintList!!) {
            pos = paintList!!.indexOf(paint)
            if (pos == mDotPosition) {
                canvas.drawCircle(
                    (15 + pos * 30).toFloat(),
                    mBounceDotRadius.toFloat(),
                    mBounceDotRadius.toFloat(),
                    paint
                )
            } else {
                canvas.drawCircle((15 + pos * 30).toFloat(), mBounceDotRadius.toFloat(), mDotRadius.toFloat(), paint)
            }
        }


    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        startAnimation()
    }

    override fun setVisibility(visibility: Int) {
        super.setVisibility(visibility)

        if (visibility == View.GONE || visibility == View.INVISIBLE) {
            stopAnimation()
        } else {
            startAnimation()
        }
    }

    override fun onDetachedFromWindow() {
        stopAnimation()
        super.onDetachedFromWindow()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val width: Int
        val height: Int

        val calculatedWidth = 20 * 9

        width = calculatedWidth
        height = mBounceDotRadius * 2

        setMeasuredDimension(width, height)
    }

    private fun startAnimation() {
        val bounceAnimation = BounceAnimation()
        bounceAnimation.duration = 350
        bounceAnimation.repeatCount = Animation.INFINITE
        bounceAnimation.interpolator = LinearInterpolator()
        bounceAnimation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {

            }

            override fun onAnimationEnd(animation: Animation) {

            }

            override fun onAnimationRepeat(animation: Animation) {
                mDotPosition++
                if (mDotPosition == paintList!!.size) {
                    mDotPosition = 0
                }

            }
        })
        startAnimation(bounceAnimation)
    }

    private fun stopAnimation() {
        this.clearAnimation()
        invalidate()
    }


    private inner class BounceAnimation : Animation() {
        override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
            super.applyTransformation(interpolatedTime, t)
            invalidate()
        }
    }
}