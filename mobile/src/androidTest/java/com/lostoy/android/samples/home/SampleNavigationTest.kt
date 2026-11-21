package com.lostoy.android.samples.home

import android.view.View
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isChecked
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.lostoy.android.samples.R
import org.hamcrest.Matcher
import org.hamcrest.Matchers.containsString
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SampleNavigationTest {
    @Test
    fun everyHomeEntry_opensItsSampleAndReturnsHome() {
        ActivityScenario.launch(SampleHomeActivity::class.java).use { scenario ->
            openAndReturn("输入框与焦点", withId(R.id.addEditText))
            selectTab("布局")
            openAndReturn("ConstraintLayout Flow", withId(R.id.tv1))
            selectTab("组件")
            openAndReturn("RecyclerView 动画", withText("添加条目"))
            openAndReturn("自定义进度条", withId(R.id.startButton))
            selectTab("设备")
            openAndReturn("GAID 获取", withText("读取 GAID"))
            openAndReturn("OAID 获取", withText("读取 OAID"))
            selectTab("工具")
            openAndReturn("JNI 调用", withText("Hello from C++"))
            openAndReturn("数值运算", withText(containsString("8 ÷ 2 = 4")))

            scenario.recreate()
            onView(withText("工具")).check(matches(isChecked()))
            onView(withText("数值运算")).check(matches(isDisplayed()))
        }
    }

    private fun selectTab(title: String) {
        onView(withText(title)).perform(click()).check(matches(isChecked()))
    }

    private fun openAndReturn(title: String, sampleContent: Matcher<View>) {
        onView(withText(title)).perform(click())
        onView(sampleContent).check(matches(isDisplayed()))
        pressBack()
        onView(withText(title)).check(matches(isDisplayed()))
    }
}
