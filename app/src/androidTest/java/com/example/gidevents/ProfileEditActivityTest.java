package com.example.gidevents;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import androidx.test.espresso.action.ViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.filters.LargeTest;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class ProfileEditActivityTest {
    @Rule
    public ActivityScenarioRule<MainActivity> scenario = new ActivityScenarioRule<MainActivity>(MainActivity.class);
    @Test
    // Testing Firebase anonymous sign in US 02.06.01
    public void testLogin() {
        onView(withId(R.id.attendeeButton)).perform(click());
        onView(withId(R.id.profileEditButton)).perform(click());
        onView(withText("Username")).check(matches(isDisplayed()));
    }

    @Test
    // Testing updating of key information
    public void testEditOption(){
        onView(withId(R.id.attendeeButton)).perform(click());
        onView(withId(R.id.profileEditButton)).perform(click());
        onView(withText("Garmond")).check(doesNotExist());
        onView(withText("Username")).perform(click());
        onView(withId(R.id.attendeeEditOption)).perform(ViewActions.clearText());
        onView(withId(R.id.attendeeEditOption)).perform(ViewActions.typeText("Garmond"));
        onView(withText("Confirm")).perform(click());
        onView(withText("Garmond")).check(matches(isDisplayed()));
    }
}