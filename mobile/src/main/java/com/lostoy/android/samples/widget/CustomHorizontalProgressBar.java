package com.lostoy.android.samples.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.lostoy.android.samples.R;

/**
 * Created by lifanlong@le.com on 2017/3/9.
 */
public class CustomHorizontalProgressBar extends FrameLayout {

    private ImageView mBackgroundImageView, mCursorImageView;

    private int mBackgroundWidth;
    private int mCursorWidth;

    private int mBackgroundLeft;

    private float mProgress = 0.0f;

    public CustomHorizontalProgressBar(Context context) {
        super(context);
        init();
    }

    public CustomHorizontalProgressBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CustomHorizontalProgressBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        addView(LayoutInflater.from(getContext()).inflate(R.layout.custom_horizontal_progress_bar, this, false));

        mBackgroundImageView = (ImageView) findViewById(R.id.iv_bg);
        mCursorImageView = (ImageView) findViewById(R.id.iv_cursor);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        mBackgroundWidth = mBackgroundImageView.getWidth();
        mCursorWidth = mCursorImageView.getWidth();

        mBackgroundLeft = mBackgroundImageView.getLeft();
    }

    public float getProgress() {
        return mProgress;
    }

    public void reset() {
        mProgress = 0.0f;
        mCursorImageView.animate().translationX(mBackgroundLeft).setDuration(0).start();
    }

    /**
     *
     * @param progress >= 0.0f <= 1.0f
     */
    public void setProgress(float progress) {
        if(progress > 1.0f) progress = 1.0f;
        if(progress < 0.0f) progress = 0.0f;
        mProgress = progress;
        if(mBackgroundWidth > 0 && mCursorWidth > 0) {
            int cursorLeft = (int) ((mBackgroundWidth-mCursorWidth) * mProgress);
            mCursorImageView.animate().translationX(cursorLeft).setDuration(300).start();
        }
    }
}
