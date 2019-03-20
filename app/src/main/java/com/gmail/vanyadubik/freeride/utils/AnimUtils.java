package com.gmail.vanyadubik.freeride.utils;

import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Transformation;
import android.widget.LinearLayout;
import com.gmail.vanyadubik.freeride.R;

public class AnimUtils {

    private static final int ANIM_DURATION = 200;
    private static int mLeftDelta;
    private static int mTopDelta;
    private static float mWidthScale;
    private static float mHeightScale;

    public static  void enterAnimation(final View viewParent, final View viewChild) {

        ViewTreeObserver observer = viewChild.getViewTreeObserver();
        observer.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {

            @Override
            public boolean onPreDraw() {
                viewChild.getViewTreeObserver().removeOnPreDrawListener(this);

                int[] screenLocation1 = new int[2];
                viewParent.getLocationOnScreen(screenLocation1);

                int thumbnailTop = screenLocation1[1];
                int thumbnailLeft = screenLocation1[0];
                int thumbnailWidth = viewParent.getWidth();
                int thumbnailHeight = viewParent.getHeight();

                int[] screenLocation = new int[2];
                viewChild.getLocationOnScreen(screenLocation);

                mLeftDelta = thumbnailLeft - screenLocation[0];
                mTopDelta = thumbnailTop - screenLocation[1];
                mWidthScale = (float) thumbnailWidth / viewChild.getWidth();
                mHeightScale = (float) thumbnailHeight / viewChild.getHeight();

                viewChild.setPivotX(0);
                viewChild.setPivotY(0);
                viewChild.setScaleX(mWidthScale);
                viewChild.setScaleY(mHeightScale);
                viewChild.setTranslationX(mLeftDelta);
                viewChild.setTranslationY(mTopDelta);

                // interpolator where the rate of change starts out quickly and then decelerates.
                TimeInterpolator sDecelerator = new DecelerateInterpolator();

                // Animate scale and translation to go from thumbnail to full size
                viewChild.animate().setDuration(ANIM_DURATION).scaleX(1).scaleY(1).
                        translationX(0).translationY(0).setInterpolator(sDecelerator);

                ObjectAnimator bgAnim = ObjectAnimator.ofInt(R.color.colorTrans, "alpha", 0, 255);
                bgAnim.setDuration(ANIM_DURATION);
                bgAnim.start();

                return true;
            }
        });
    }

    public static void exitAnimation(View view, final Runnable endAction) {

        TimeInterpolator sInterpolator = new AccelerateInterpolator();
        view.animate().setDuration(ANIM_DURATION).scaleX(mWidthScale).scaleY(mHeightScale).
                translationX(mLeftDelta).translationY(mTopDelta)
                .setInterpolator(sInterpolator).withEndAction(endAction);

        // Fade out background
        ObjectAnimator bgAnim = ObjectAnimator.ofInt(R.color.colorTrans, "alpha", 0);
        bgAnim.setDuration(ANIM_DURATION);
        bgAnim.start();
    }

    public static void expand(final View v) {
        v.measure(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        final int targetHeight = v.getMeasuredHeight();

        // Older versions of android (pre API 21) cancel animations for views with a height of 0.
        v.getLayoutParams().height = 1;
        v.setVisibility(View.VISIBLE);
        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                v.getLayoutParams().height = interpolatedTime == 1
                        ? LinearLayout.LayoutParams.WRAP_CONTENT
                        : (int)(targetHeight * interpolatedTime);
                v.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        a.setDuration((int)(targetHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }

    public static void collapse(final View v) {
        final int initialHeight = v.getMeasuredHeight();

        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if(interpolatedTime == 1){
                    v.setVisibility(View.GONE);
                }else{
                    v.getLayoutParams().height = initialHeight - (int)(initialHeight * interpolatedTime);
                    v.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        a.setDuration((int)(initialHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }
}
