package com.lostoy.android.samples.widgets.progress

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import com.lostoy.android.samples.R

/**
 * Created by lifanlong@le.com on 2017/3/9.
 */
class CustomHorizontalProgressBar : FrameLayout {
    private var mBackgroundImageView: ImageView? = null
    private var mCursorImageView: ImageView? = null

    private var mBackgroundWidth = 0
    private var mCursorWidth = 0

    private var mBackgroundLeft = 0

    private var mProgress = 0.0f

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init()
    }

    private fun init() {
        addView(
            LayoutInflater.from(getContext())
                .inflate(R.layout.custom_horizontal_progress_bar, this, false)
        )

        mBackgroundImageView = findViewById<View?>(R.id.iv_bg) as ImageView
        mCursorImageView = findViewById<View?>(R.id.iv_cursor) as ImageView
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)

        mBackgroundWidth = mBackgroundImageView!!.getWidth()
        mCursorWidth = mCursorImageView!!.getWidth()

        mBackgroundLeft = mBackgroundImageView!!.getLeft()
    }

    /** Current progress, from 0.0 to 1.0. */
    var progress: Float
        get() = mProgress
        set(progress) {
            var progress = progress
            if (progress > 1.0f) progress = 1.0f
            if (progress < 0.0f) progress = 0.0f
            mProgress = progress
            if (mBackgroundWidth > 0 && mCursorWidth > 0) {
                val cursorLeft = ((mBackgroundWidth - mCursorWidth) * mProgress).toInt()
                mCursorImageView!!.animate().translationX(cursorLeft.toFloat()).setDuration(300)
                    .start()
            }
        }

    fun reset() {
        mProgress = 0.0f
        mCursorImageView!!.animate().translationX(mBackgroundLeft.toFloat()).setDuration(0).start()
    }
}
