package com.gmail.vanyadubik.freeride.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.Transformation;

import java.util.ArrayList;
import java.util.List;

public class HorizontalDottedProgress extends View {

    private int mDotRadius = 12;

    private int mBounceDotRadius = 16;

    private int  mDotPosition;

    private List<Paint> paintList;

    public HorizontalDottedProgress(Context context) {
        super(context);
        init();
    }

    public HorizontalDottedProgress(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public HorizontalDottedProgress(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void setPaintList(List<Paint> paintList) {
        this.paintList = paintList;
    }

    public void showAnimation() {
        init();
        mDotPosition = 0;
        startAnimation();
    }

    public void hideAnimation() {
        stopAnimation();
        mDotPosition = paintList.size();
        init();
    }

    private void init(){
        if(paintList==null) {
            paintList = new ArrayList<>();
            Paint paint1 = new Paint();
            paint1.setColor(Color.parseColor("#34ff8a"));
            paintList.add(paint1);

            Paint paint2 = new Paint();
            paint2.setColor(Color.parseColor("#ffff47"));
            paintList.add(paint2);

            Paint paint3 = new Paint();
            paint3.setColor(Color.parseColor("#ff4b4b"));
            paintList.add(paint3);
        }
    }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);



        createDot(canvas);
    }

    private void createDot(Canvas canvas) {

//        for(int i = 0; i < mDotAmount; i++ ){
//            if(i == mDotPosition){
//                canvas.drawCircle(15+(i*30), mBounceDotRadius, mBounceDotRadius, paintList.get(i));
//            }else {
//                canvas.drawCircle(15+(i*30), mBounceDotRadius, mDotRadius, paintList.get(i));
//            }
//        }

        int pos;

        for(Paint paint: paintList){
            pos = paintList.indexOf(paint);
            if(pos == mDotPosition){
                canvas.drawCircle(15+(pos*30), mBounceDotRadius, mBounceDotRadius, paint);
            }else {
                canvas.drawCircle(15+(pos*30), mBounceDotRadius, mDotRadius, paint);
            }
        }


    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        startAnimation();
    }

    @Override
    public void setVisibility(int visibility) {
        super.setVisibility(visibility);

        if (visibility == GONE || visibility == INVISIBLE) {
            stopAnimation();
        } else {
            startAnimation();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        stopAnimation();
        super.onDetachedFromWindow();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width;
        int height;

        int calculatedWidth = (20*9);

        width = calculatedWidth;
        height = (mBounceDotRadius*2);

        setMeasuredDimension(width, height);
    }

    private void startAnimation() {
        BounceAnimation bounceAnimation = new BounceAnimation();
        bounceAnimation.setDuration(350);
        bounceAnimation.setRepeatCount(Animation.INFINITE);
        bounceAnimation.setInterpolator(new LinearInterpolator());
        bounceAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                mDotPosition++;
                if (mDotPosition == paintList.size()) {
                    mDotPosition = 0;
                }

            }
        });
        startAnimation(bounceAnimation);
    }

    private void stopAnimation() {
        this.clearAnimation();
    }


    private class BounceAnimation extends Animation {
        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            super.applyTransformation(interpolatedTime, t);
            invalidate();
        }
    }
}