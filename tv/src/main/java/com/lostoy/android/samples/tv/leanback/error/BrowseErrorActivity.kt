/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.lostoy.android.samples.tv.leanback.error

import android.app.Fragment
import android.os.Bundle
import android.os.Handler
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ProgressBar
import androidx.fragment.app.FragmentActivity
import com.lostoy.android.samples.R

/*
 * BrowseErrorActivity shows how to use BrowseErrorFragment
 */
class BrowseErrorActivity : FragmentActivity() {
    private var mErrorFragment: BrowseErrorFragment? = null
    private var mSpinnerFragment: SpinnerFragment? = null

    /**
     * Called when the activity is first created.
     */
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        testError()
    }

    private fun testError() {
        mErrorFragment = BrowseErrorFragment()
        getFragmentManager().beginTransaction().add(R.id.main_browse_fragment, mErrorFragment)
            .commit()

        mSpinnerFragment = SpinnerFragment()
        getFragmentManager().beginTransaction().add(R.id.main_browse_fragment, mSpinnerFragment)
            .commit()

        val handler = Handler()
        handler.postDelayed(object : Runnable {
            override fun run() {
                getFragmentManager().beginTransaction().remove(mSpinnerFragment).commit()
                mErrorFragment!!.setErrorContent()
            }
        }, TIMER_DELAY.toLong())
    }

    class SpinnerFragment : Fragment() {
        override fun onCreateView(
            inflater: LayoutInflater?, container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View {
            val progressBar = ProgressBar(activity)
            if (container is FrameLayout) {
                val layoutParams =
                    FrameLayout.LayoutParams(SPINNER_WIDTH, SPINNER_HEIGHT, Gravity.CENTER)
                progressBar.setLayoutParams(layoutParams)
            }
            return progressBar
        }
    }

    companion object {
        private const val TIMER_DELAY = 3000
        private const val SPINNER_WIDTH = 100
        private const val SPINNER_HEIGHT = 100
    }
}
