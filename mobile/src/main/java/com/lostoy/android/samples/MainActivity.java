package com.lostoy.android.samples;

import android.os.Bundle;
import android.util.Log;

public class MainActivity extends BaseActivity {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "stringFromJIN: " + stringFromJNI());

        getSupportFragmentManager().beginTransaction().replace(
                R.id.container,
                ExperimentFragment.newInstance(),
                ExperimentFragment.class.getSimpleName()).commit();
    }

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
