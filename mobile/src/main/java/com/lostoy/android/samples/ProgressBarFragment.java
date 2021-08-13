package com.lostoy.android.samples;

import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.Nullable;
import android.view.View;

import com.lostoy.android.samples.widget.CustomHorizontalProgressBar;

import java.util.Random;

/**
 * Created by lifanlong on 2017/11/30.
 */

public class ProgressBarFragment extends BaseFragment {

    private CustomHorizontalProgressBar mProgressBar;

    private Handler mHandler;

    private float mProgress = 0.0f;

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_progressbar;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mHandler = new Handler();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mProgressBar = (CustomHorizontalProgressBar) getView().findViewById(R.id.progressBar);

        getView().findViewById(R.id.startButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mProgress = 0.0f;
                mProgressBar.reset();
                mHandler.postDelayed(mProgressRunnable, 3000);
            }
        });

    }

    private Runnable mProgressRunnable = new Runnable() {
        @Override
        public void run() {
            mProgress = mProgressBar.getProgress()+0.05f;
            mProgressBar.setProgress(mProgress);
            if(mProgress < 1.0f) {
                mHandler.postDelayed(mProgressRunnable, new Random().nextInt(1000));
            }
        }
    };

}
