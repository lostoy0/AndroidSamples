package com.lostoy.android.samples;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.lostoy.android.samples.widget.CustomHorizontalProgressBar;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private CustomHorizontalProgressBar mProgressBar;

    private Handler mHandler;

    private float mProgress = 0.0f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mHandler = new Handler();

        // Example of a call to a native method
        TextView tv = (TextView) findViewById(R.id.sample_text);
        tv.setText(stringFromJNI());

        mProgressBar = (CustomHorizontalProgressBar) findViewById(R.id.progressBar);

        findViewById(R.id.startButton).setOnClickListener(new View.OnClickListener() {
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

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }
}
