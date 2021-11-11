package com.lostoy.android.samples;

import android.os.Bundle;
import android.util.Log;

import com.lostoy.android.common.deviceid.AdvertisingIdClient;
import com.lostoy.android.common.deviceid.AdvertisingIdClientX;
import com.lostoy.android.samples.utils.GAIDUtil;
import com.lostoy.android.samples.utils.OAIDUtil;

import java.math.BigDecimal;
import java.util.concurrent.Executors;

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

        GAIDUtil.init(this);
        GAIDUtil.initByRxJava(this);
        OAIDUtil.init(this);

        float testInfinity = 1/0.0f;
        Log.e("raymond", "origin: " + testInfinity);
        if (Math.abs(testInfinity) > Integer.MAX_VALUE) {
            Log.e("raymond", "> Integer.MAX_VALUE");
        } else {
            Log.e("raymond", "not > Integer.MAX_VALUE");
        }

        BigDecimal decimal = BigDecimal.valueOf(8).divide(BigDecimal.valueOf(2));
        Log.e("raymond", "8 divide 2 = " + decimal.toString());
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
