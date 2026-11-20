package com.lostoy.android.samples;

import android.view.View;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.hamcrest.Matcher;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.containsString;

@RunWith(AndroidJUnit4.class)
public class SampleNavigationTest {
    @Test
    public void everyHomeEntry_opensItsSampleAndReturnsHome() {
        try (ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class)) {
            openAndReturn("输入框与焦点", withId(R.id.addEditText));
            openAndReturn("ConstraintLayout Flow", withId(R.id.tv1));
            openAndReturn("RecyclerView 动画", withText("添加条目"));
            openAndReturn("自定义进度条", withId(R.id.startButton));
            openAndReturn("JNI 调用", withText("Hello from C++"));
            openAndReturn("GAID 获取", withText("读取 GAID"));
            openAndReturn("OAID 获取", withText("读取 OAID"));
            openAndReturn("数值运算", withText(containsString("8 ÷ 2 = 4")));
        }
    }

    private void openAndReturn(String title, Matcher<View> sampleContent) {
        onView(withText(title)).perform(click());
        onView(sampleContent).check(matches(isDisplayed()));
        pressBack();
        onView(withText(title)).check(matches(isDisplayed()));
    }
}
