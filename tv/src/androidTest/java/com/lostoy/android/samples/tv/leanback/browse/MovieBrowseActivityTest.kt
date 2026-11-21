package com.lostoy.android.samples.tv.leanback.browse

import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.lostoy.android.samples.R
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MovieBrowseActivityTest {
    @Test
    @Suppress("DEPRECATION")
    fun movieBrowse_launchesTheRenamedLeanbackFragment() {
        ActivityScenario.launch(MovieBrowseActivity::class.java).use { scenario ->
            scenario.onActivity { activity ->
                val fragment = activity.fragmentManager.findFragmentById(R.id.main_browse_fragment)
                assertTrue(fragment is MovieBrowseFragment)
            }
        }
    }
}
