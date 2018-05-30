package com.example.tvappdemo;

import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;

public class MainActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportFragmentManager().beginTransaction().replace(R.id.container, new TVDemoFragment()).commit();
    }

        @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        View focusView = getCurrentFocus();
        if(null != focusView) {
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
        }
        return super.dispatchKeyEvent(event);
    }
}
