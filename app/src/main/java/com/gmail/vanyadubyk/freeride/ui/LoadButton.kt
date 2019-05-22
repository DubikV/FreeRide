package com.gmail.vanyadubyk.freeride.ui

import android.animation.*
import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.AnimatedVectorDrawable
import android.os.Build
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import com.gmail.vanyadubyk.freeride.R

@SuppressLint("ObjectAnimatorBinding")
class LoadButton : FrameLayout, View.OnClickListener {

    private var listener: ButtonLoadOnClickListener? = null
    private var text: TextView? = null
    private var progressBar: ProgressBar? = null
    private var imageView: ImageView? = null
    private var colorProgressBar: Int = 0
    private var heightProgressBar: Int = 0
    private var measuredWidthView: Int  = 0
    private var measuredHeightView: Int  = 0
    private var corner: Float = 0f
    private var isShowSync: Boolean = false

    constructor(context: Context) : super(context) {
        setOnClickListener(this)
        init()
    }

    constructor(context: Context, attrs: AttributeSet?) :
            super(context, attrs) {
        setOnClickListener(this)
        init()
        initAttributes(attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
            super(context, attrs, defStyleAttr) {
        setOnClickListener(this)
        init()
        initAttributes(attrs)
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) :
            super(context, attrs, defStyleAttr, defStyleRes) {
        setOnClickListener(this)
        init()
        initAttributes(attrs)
    }

    fun setButtonLoadOnClickListener(listener: ButtonLoadOnClickListener) {
        this.listener = listener
    }

    fun getText(): CharSequence {
        return text!!.text
    }

    fun setText(text: CharSequence) {
        this.text!!.text = text
    }

    private fun init() {
        View.inflate(context, R.layout.load_button, this)
        this.text = findViewById(R.id.text)
        this.progressBar = findViewById(R.id.progress_bar)
        this.imageView = findViewById(R.id.imageView)
        this.isShowSync = false
    }

    private fun initAttributes(attrs: AttributeSet?) {

        if (attrs != null) {
            val a = context.obtainStyledAttributes(attrs, R.styleable.LoadButton)
            try {

                text!!.text = a.getString(R.styleable.LoadButton_text)

                text!!.setTextColor(a.getColor(R.styleable.LoadButton_textColor, Color.parseColor("#ffffff")))

                val line1Size = a.getDimensionPixelSize(R.styleable.LoadButton_textSize, 0)

                if (line1Size > 0) {
                    text!!.setTextSize(TypedValue.COMPLEX_UNIT_PX, line1Size.toFloat())
                }

                heightProgressBar = a.getDimensionPixelSize(R.styleable.LoadButton_progressBarSize,
                    context.resources.getDimension(R.dimen.button_load_height).toInt())

                if (heightProgressBar > 0) {
                    progressBar!!.layoutParams.width = heightProgressBar
                    progressBar!!.layoutParams.height = heightProgressBar
                }

                colorProgressBar = a.getInt(R.styleable.LoadButton_colorProgressBar, 0)

                corner = a.getDimensionPixelSize(R.styleable.LoadButton_cornerRadiusDim, 0).toFloat()
            } finally {
                a.recycle()
            }
        }
    }

    fun showLoad() {

        this.isShowSync = true

        animateButtonWidth(true)

        fadeOutTextAndShowProgressDialog()

    }

    fun cancelLoad() {

        this.isShowSync = false

        animateButtonWidth(false)

        fadeOutProgressDialog()

    }

    fun showLoad(animationListener: ButtonLoadOnAnimationListener) {
        this.isShowSync = true

        animateButtonWidth(true)

        text!!.visibility = View.GONE
        text!!.animate().alpha(0f)
            .setDuration(ANIMATION_DURATION.toLong())
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    super.onAnimationEnd(animation)
                    animationListener.onEndAnimation()
                }
            })
            .start()
    }

    fun cancelLoad(animationListener: ButtonLoadOnAnimationListener) {

        this.isShowSync = false

        animateButtonWidth(false)

        progressBar!!.animate().alpha(0f).setDuration(200).setListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                text!!.animate().alpha(1f)
                    .setDuration(ANIMATION_DURATION.toLong())
                    .setListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator) {
                            super.onAnimationEnd(animation)
                            animationListener.onEndAnimation()
                        }
                    })
                    .start()
            }
        }).start()

    }

    fun showText(message: String, isError: Boolean, animationListener: ButtonLoadOnAnimationListener) {
        this.isShowSync = false

        text!!.text = message
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            imageView!!.setImageDrawable(
                if (isError)
                    context.resources.getDrawable(R.drawable.ic_clear_anim)
                else
                    context.resources.getDrawable(R.drawable.ic_check_anim)
            )
        } else {
            imageView!!.setImageDrawable(
                if (isError)
                    context.resources.getDrawable(R.drawable.ic_clear)
                else
                    context.resources.getDrawable(R.drawable.ic_check)
            )
        }

        val cornerAnimation = ObjectAnimator.ofFloat(this.background, "cornerRadius", 1000f, corner)

        val widthAnimation =
            ValueAnimator.ofInt(progressBar!!.measuredWidth, if (measuredWidthView == 0) 500 else measuredWidthView)
        widthAnimation.addUpdateListener { valueAnimator ->
            val layoutParams = this.layoutParams
            layoutParams.width = valueAnimator.animatedValue as Int
            this.requestLayout()
        }


        val heightAnimation =
            ValueAnimator.ofInt(progressBar!!.measuredHeight, if (measuredHeightView == 0) 500 else measuredHeightView)
        heightAnimation.addUpdateListener { valueAnimator ->
            val layoutParams = layoutParams
            layoutParams.height = valueAnimator.animatedValue as Int
            setLayoutParams(layoutParams)
        }

        val mMorphingAnimatorSet = AnimatorSet()
        mMorphingAnimatorSet.duration = ANIMATION_DURATION.toLong()
        mMorphingAnimatorSet.playTogether(cornerAnimation, heightAnimation, widthAnimation)
        mMorphingAnimatorSet.start()

        text!!.visibility = View.VISIBLE
        text!!.animate().alpha(1f)
            .setDuration(ANIMATION_DURATION.toLong())
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    super.onAnimationEnd(animation)
                    progressBar!!.animate().alpha(0f).setDuration(200).setListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator) {
                            super.onAnimationEnd(animation)

                            imageView!!.alpha = 1f
                            imageView!!.visibility = View.VISIBLE
                            animationListener.onEndAnimation()

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                val drawable = imageView!!.drawable as AnimatedVectorDrawable
                                drawable.start()
                            }
                        }
                    })
                        .start()
                }
            }).start()

    }

    private fun animateButtonWidth(showProgressBar: Boolean) {

        val cornerAnimation: ObjectAnimator
        val widthAnimation: ValueAnimator
        val heightAnimation: ValueAnimator

        if (showProgressBar) {
            measuredWidthView = this.measuredWidth
            measuredHeightView = this.measuredHeight

            widthAnimation = ValueAnimator.ofInt(measuredWidthView, progressBar!!.measuredWidth)
            heightAnimation = ValueAnimator.ofInt(measuredHeightView, progressBar!!.measuredHeight)
            cornerAnimation = ObjectAnimator.ofFloat(this.background, "cornerRadius", corner, 1000f)
        } else {
            widthAnimation =
                ValueAnimator.ofInt(progressBar!!.measuredWidth, if (measuredWidthView == 0) 500 else measuredWidthView)
            heightAnimation =
                ValueAnimator.ofInt(progressBar!!.measuredHeight, if (measuredHeightView == 0) 500 else measuredHeightView)
            cornerAnimation = ObjectAnimator.ofFloat(this.background, "cornerRadius", 1000f, corner)
        }

        widthAnimation.addUpdateListener { valueAnimator ->
            val layoutParams = this.layoutParams
            layoutParams.width = valueAnimator.animatedValue as Int
            this.requestLayout()
        }

        heightAnimation.addUpdateListener { valueAnimator ->
            val layoutParams = layoutParams
            layoutParams.height = valueAnimator.animatedValue as Int
            setLayoutParams(layoutParams)
        }

        val mMorphingAnimatorSet = AnimatorSet()
        mMorphingAnimatorSet.duration = ANIMATION_DURATION.toLong()
        if (showProgressBar) {
            mMorphingAnimatorSet.playTogether(cornerAnimation, widthAnimation, heightAnimation)
        } else {
            mMorphingAnimatorSet.playTogether(cornerAnimation, heightAnimation, widthAnimation)
        }

        mMorphingAnimatorSet.start()
    }

    private fun showProgressDialog() {
        imageView!!.visibility = View.INVISIBLE
        progressBar!!.alpha = 1f
        progressBar!!.indeterminateDrawable.setColorFilter(
                if (colorProgressBar == 0) Color.parseColor("#ffffff") else colorProgressBar, PorterDuff.Mode.SRC_IN
            )
        progressBar!!.visibility = View.VISIBLE
//        progressBar!!.layoutParams.height = heightProgressBar
//        progressBar!!.layoutParams.width = heightProgressBar
        progressBar!!.requestLayout()
    }

    private fun fadeOutTextAndShowProgressDialog() {
        text!!.visibility = View.GONE
        text!!.animate().alpha(0f)
            .setDuration(ANIMATION_DURATION.toLong())
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    super.onAnimationEnd(animation)
                    showProgressDialog()
                }
            })
            .start()
    }

    private fun fadeOutProgressDialog() {
        progressBar!!.animate().alpha(0f).setDuration(200).setListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                fadeOnTextAndHideProgressDialog()
            }
        }).start()
    }


    private fun fadeOnTextAndHideProgressDialog() {
        text!!.visibility = View.VISIBLE
        text!!.animate().alpha(1f)
            .setDuration(ANIMATION_DURATION.toLong())
            .setListener(object : AnimatorListenerAdapter() {
            })
            .start()
    }

    override fun onClick(v: View) {

        if (isShowSync) return

        isShowSync = true

//        if (context != null) {
//            (context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager)
//                .toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0)
//        }

        measuredWidthView = this.measuredWidth
        measuredHeightView = this.measuredHeight

        val cornerAnimation = ObjectAnimator.ofFloat(this.background, "cornerRadius", corner, 1000f)

        val widthAnimation = ValueAnimator.ofInt(measuredWidthView, progressBar!!.measuredWidth)
        widthAnimation.addUpdateListener { valueAnimator ->
            val layoutParams = layoutParams
            layoutParams.width = valueAnimator.animatedValue as Int
            requestLayout()
//            progressBar!!.layoutParams.width = heightProgressBar
//            progressBar!!.requestLayout()
        }

        val heightAnimation = ValueAnimator.ofInt(measuredHeightView, progressBar!!.measuredHeight)
        heightAnimation.addUpdateListener { valueAnimator ->
            val layoutParams = layoutParams
            layoutParams.height = valueAnimator.animatedValue as Int
            setLayoutParams(layoutParams)
//            progressBar!!.layoutParams.height = heightProgressBar
//            progressBar!!.requestLayout()
        }


        val mMorphingAnimatorSet = AnimatorSet()
        mMorphingAnimatorSet.duration = ANIMATION_DURATION.toLong()
        mMorphingAnimatorSet.playTogether(cornerAnimation, widthAnimation, heightAnimation)
        mMorphingAnimatorSet.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                if (listener != null) {
                    listener!!.onClick(v)
                }
            }
        })
        mMorphingAnimatorSet.start()

        fadeOutTextAndShowProgressDialog()
    }

    interface ButtonLoadOnClickListener {

        fun onClick(v: View)
    }

    interface ButtonLoadOnAnimationListener {

        fun onEndAnimation()
    }

    companion object {

        private val ANIMATION_DURATION = 150
    }
}