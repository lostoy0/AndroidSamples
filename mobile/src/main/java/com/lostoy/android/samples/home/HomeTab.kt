package com.lostoy.android.samples.home

import androidx.annotation.IdRes
import com.lostoy.android.samples.R
import com.lostoy.android.samples.catalog.Sample

internal enum class HomeTab(@IdRes val id: Int, val samples: List<Sample> = emptyList()) {
    WIDGETS(R.id.home_widgets_tab, listOf(Sample.INPUT, Sample.RECYCLER_ANIMATION, Sample.PROGRESS)),
    LAYOUT(R.id.home_layout_tab, listOf(Sample.FLOW)),
    DEVICE(R.id.home_device_tab, listOf(Sample.GAID, Sample.OAID)),
    TOOLS(R.id.home_tools_tab, listOf(Sample.JNI, Sample.NUMBERS)),
    FLUTTER(R.id.home_flutter_tab)
}
