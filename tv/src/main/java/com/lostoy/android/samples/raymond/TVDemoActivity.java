package com.lostoy.android.samples.raymond;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;

import com.lostoy.android.samples.R;

/**
 * Created by lifanlong on 2017/12/27.
 */

public class TVDemoActivity extends BaseActivity {

    private Button mEpisodesSelectionButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tv_demo);

        getSupportFragmentManager().beginTransaction().replace(R.id.container, new TVDemoFragment()).commit();
    }

//    @Override
//    public boolean dispatchKeyEvent(KeyEvent event) {
//        View focusView = getCurrentFocus();
//        if(null != focusView) {
//            switch (focusView.getId()) {
//                case R.id.playButton:
//                    if(event.getAction() == KeyEvent.ACTION_DOWN) {
//                        switch (event.getKeyCode()) {
//                            case KeyEvent.KEYCODE_DPAD_UP:
//                                break;
//                            case KeyEvent.KEYCODE_DPAD_LEFT:
//                                break;
//                            case KeyEvent.KEYCODE_DPAD_RIGHT:
//                                mEpisodesSelectionButton.requestFocus();
//                                return true;
//                            case KeyEvent.KEYCODE_DPAD_DOWN:
//                                break;
//                        }
//                    }
//                    break;
//
//                case R.id.nextButton:
//
//                    break;
//
//                case R.id.favouriteButton:
//
//                    break;
//            }
//        }
//        return super.dispatchKeyEvent(event);
//    }
}
