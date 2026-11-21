package com.lostoy.android.samples.tv.focus

import android.os.Build
import android.view.View
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.lostoy.android.samples.R
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TvFocusActivityTest {
    @Test
    fun focusDemo_inflatesTheMovieRowAndFocusesPlay() {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        instrumentation.setInTouchMode(false)
        try {
            ActivityScenario.launch(TvFocusActivity::class.java).use { scenario ->
                scenario.onActivity { activity ->
                    val row = activity.findViewById<FocusedItemRecyclerView>(R.id.recyclerView)
                    assertEquals(20, row.adapter?.itemCount)
                    assertTrue(activity.findViewById<View>(R.id.playButton).hasFocus())
                }
            }
        } finally {
            if (Build.VERSION.SDK_INT >= 33) instrumentation.resetInTouchMode()
        }
    }
}
