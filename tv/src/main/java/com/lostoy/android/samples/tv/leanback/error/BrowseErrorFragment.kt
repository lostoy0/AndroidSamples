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

import android.os.Bundle
import android.util.Log
import android.view.View
import com.lostoy.android.samples.R

/*
 * This class demonstrates how to extend BrowseErrorFragment
 */
class BrowseErrorFragment : androidx.leanback.app.ErrorFragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate")
        super.onCreate(savedInstanceState)
        setTitle(getResources().getString(R.string.app_name))
    }

    fun setErrorContent() {
        setImageDrawable(getResources().getDrawable(androidx.leanback.R.drawable.lb_ic_sad_cloud))
        setMessage(getResources().getString(R.string.error_fragment_message))
        setDefaultBackground(TRANSLUCENT)

        setButtonText(getResources().getString(R.string.dismiss_error))
        setButtonClickListener(object : View.OnClickListener {
            override fun onClick(arg0: View?) {
                getFragmentManager().beginTransaction().remove(this@BrowseErrorFragment).commit()
            }
        })
    }

    companion object {
        private const val TAG = "BrowseErrorFragment"
        private const val TRANSLUCENT = true
    }
}
