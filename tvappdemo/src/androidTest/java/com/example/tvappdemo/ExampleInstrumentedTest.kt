package com.example.tvappdemo

import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.tvappdemo.focus.FocusedItemRecyclerView
import com.example.tvappdemo.focus.TvFocusActivity
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    fun useAppContext() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.example.tvappdemo", context.packageName)
    }

    @Test
    fun focusDemo_launchesAndInflatesTheMovieRow() {
        ActivityScenario.launch(TvFocusActivity::class.java).use { scenario ->
            scenario.onActivity { activity ->
                val row = activity.findViewById<FocusedItemRecyclerView>(R.id.recyclerView)
                assertEquals(20, row.adapter?.itemCount)
            }
        }
    }
}
