package com.example.gidevents;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import static org.hamcrest.Matchers.anything;

import androidx.test.ext.junit.rules.ActivityScenarioRule;

import org.junit.Rule;
import org.junit.Test;


public class AttendeeProfileTest {
    @Rule
    public ActivityScenarioRule<AttendeeActivity> scenario = new
            ActivityScenarioRule<AttendeeActivity>(AttendeeActivity.class);

    /**
     * Test for proper navigation to notifications from attendee main menu
     */
    @Test
    public void testNotifActivity() {
        onView(withId(R.id.notificationButton)).perform(click());

        // Verifying all information matches
        onView(withId(R.id.notif_listview)).check(matches(isDisplayed()));
    }
}
