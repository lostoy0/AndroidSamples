package com.lostoy.android.samples.tv.focus

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.lostoy.android.samples.R

class TvFocusActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tv_demo)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, TvFocusFragment())
                .commit()
        }
    }
}
