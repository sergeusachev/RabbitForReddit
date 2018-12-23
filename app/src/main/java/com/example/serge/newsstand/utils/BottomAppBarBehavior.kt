package com.example.serge.newsstand.utils

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.ViewCompat.SCROLL_AXIS_VERTICAL
import kotlin.math.max
import kotlin.math.min

class BottomAppBarBehavior<V : View>(
        context: Context,
        attrs: AttributeSet) : CoordinatorLayout.Behavior<V>(context, attrs) {

    override fun onStartNestedScroll(
            coordinatorLayout: CoordinatorLayout,
            child: V,
            directTargetChild: View,
            target: View,
            axes: Int,
            type: Int) = axes == SCROLL_AXIS_VERTICAL

    override fun onNestedPreScroll(
            coordinatorLayout: CoordinatorLayout,
            child: V,
            target: View,
            dx: Int,
            dy: Int,
            consumed: IntArray,
            type: Int) {
        super.onNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed, type)
        Log.d("SCROLL_BEHAVIOR_CHECK", "dY: $dy")
        Log.d("SCROLL_BEHAVIOR_CHECK", "tY: ${child.translationY}")

        child.translationY = max(0F, min(child.height.toFloat(), child.translationY + dy))
    }

}
