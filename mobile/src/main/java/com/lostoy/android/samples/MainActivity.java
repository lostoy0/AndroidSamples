package com.lostoy.android.samples;

import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.lostoy.android.samples.tv.TVDemoFragment;

import java.util.ArrayList;

public class MainActivity extends BaseActivity {
    public static ArrayList<EditText> editTextList = new ArrayList<>();
    private LinearLayout mRoot;

    private ExperimentFragment experimentFragment;

    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.e("raymond", "oncreate--------------");

        experimentFragment = ExperimentFragment.newInstance();

        fragmentManager = getSupportFragmentManager();

        fragmentManager.beginTransaction().replace(R.id.container, new TVDemoFragment()).commit();

//        mRoot = (LinearLayout) findViewById(R.id.editTextRoot);
//
//        findViewById(R.id.addEditText).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                addEditText(v);
//            }
//        });
//
//        findViewById(R.id.removeEditText).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                removeEditText(v);
//            }
//        });
//
//        findViewById(R.id.requestFocus).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                requestFocus(v);
//            }
//        });

        findViewById(R.id.attachFragment).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(experimentFragment.isDetached()) {
                    Log.e("raymond", "attach fragment");
                    fragmentManager.beginTransaction().attach(experimentFragment).commit();
                } else {
                    Log.e("raymond", "detach fragment");
                    fragmentManager.beginTransaction().detach(experimentFragment).commit();
                }
            }
        });

        findViewById(R.id.testRequestFocus).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ExperimentFragment.editTextList.get(0).requestFocus();
            }
        });
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

    public void addEditText(View v) {
        editTextList.add(new EditText(this));
        mRoot.addView(editTextList.get(editTextList.size()-1), LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
    }

    public void removeEditText(View v) {
        mRoot.removeView(editTextList.get(0));
        editTextList.remove(0);
    }

    public void requestFocus(View v) {
        editTextList.get(editTextList.size()-1).requestFocus();
    }
}
