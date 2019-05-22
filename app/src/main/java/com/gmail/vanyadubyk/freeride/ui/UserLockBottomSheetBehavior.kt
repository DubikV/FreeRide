package com.gmail.vanyadubyk.freeride.ui

import android.content.Context
import android.support.design.widget.CoordinatorLayout
import android.view.MotionEvent
import android.support.design.widget.BottomSheetBehavior
import android.util.AttributeSet
import android.view.View


internal class UserLockBottomSheetBehavior<V : View> : BottomSheetBehavior<V> {

    constructor() : super() {}

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {}

    override fun onInterceptTouchEvent(parent: CoordinatorLayout, child: V, event: MotionEvent): Boolean {
        return false
    }

    override fun onTouchEvent(parent: CoordinatorLayout, child: V, event: MotionEvent): Boolean {
        return false
    }

    override fun onStartNestedScroll(coordinatorLayout: CoordinatorLayout, child: V, directTargetChild: View, target: View, nestedScrollAxes: Int): Boolean {
        return false
    }

    override fun onNestedPreScroll(coordinatorLayout: CoordinatorLayout, child: V, target: View, dx: Int, dy: Int, consumed: IntArray) {}

    override fun onStopNestedScroll(coordinatorLayout: CoordinatorLayout, child: V, target: View) {}

    override fun onNestedPreFling(coordinatorLayout: CoordinatorLayout, child: V, target: View, velocityX: Float, velocityY: Float): Boolean {
        return false
    }

}