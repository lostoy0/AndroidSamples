package com.example.tvappdemo.focus

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import androidx.recyclerview.widget.RecyclerView

/**
 * Created by lifanlong on 2017/12/27.
 */
class FocusedItemRecyclerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : RecyclerView(context, attrs, defStyle) {
    private var mSelectedPosition = 0

    init {
        init()
    }

    private fun init() {
        setChildrenDrawingOrderEnabled(true)
    }

    override fun onDraw(c: Canvas) {
        mSelectedPosition = indexOfChild(getFocusedChild())
        super.onDraw(c!!)
    }

    override fun getChildDrawingOrder(childCount: Int, i: Int): Int {
        var position = mSelectedPosition
        if (position < 0) {
            return i
        } else {
            if (i == childCount - 1) {
                if (position > i) {
                    position = i
                }
                return position
            }
            if (i == position) {
                return childCount - 1
            }
        }
        return i
    }
}
