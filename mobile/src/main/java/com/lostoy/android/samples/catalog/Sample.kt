package com.lostoy.android.samples.catalog

import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import com.lostoy.android.samples.R
import com.lostoy.android.samples.device.AdvertisingIdSampleFragment
import com.lostoy.android.samples.layout.ConstraintFlowSampleFragment
import com.lostoy.android.samples.tools.ToolSampleFragment
import com.lostoy.android.samples.widgets.input.InputFocusSampleFragment
import com.lostoy.android.samples.widgets.progress.ProgressBarSampleFragment
import com.lostoy.android.samples.widgets.recycler.RecyclerAnimationSampleFragment

internal enum class Sample(val slug: String, @StringRes val titleRes: Int) {
    INPUT("input", R.string.sample_input),
    FLOW("flow", R.string.sample_flow),
    RECYCLER_ANIMATION("recycler-animation", R.string.sample_recycler_animation),
    PROGRESS("progress", R.string.sample_progress),
    JNI("jni", R.string.sample_jni),
    GAID("gaid", R.string.sample_gaid),
    OAID("oaid", R.string.sample_oaid),
    NUMBERS("numbers", R.string.sample_numbers);

    fun createFragment(): Fragment = when (this) {
        INPUT -> InputFocusSampleFragment.newInstance()
        FLOW -> ConstraintFlowSampleFragment()
        RECYCLER_ANIMATION -> RecyclerAnimationSampleFragment()
        PROGRESS -> ProgressBarSampleFragment()
        GAID, OAID -> AdvertisingIdSampleFragment.newInstance(googleId = this == GAID)
        JNI, NUMBERS -> ToolSampleFragment.newInstance(this)
    }
}
