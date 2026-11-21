package com.example.tvappdemo.focus

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.example.tvappdemo.R

class TvFocusActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, TvFocusFragment())
                .commit()
        }
    }
}
