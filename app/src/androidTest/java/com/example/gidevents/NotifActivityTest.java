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

public class NotifActivityTest {
    @Rule
    public ActivityScenarioRule<NotifActivity> scenario = new
            ActivityScenarioRule<NotifActivity>(NotifActivity.class);

    /**
     * Test to check for correct navigation to notification details
     */
    @Test
    public void testNotifDetailActivity() {
        // Selecting first notification in list
        onData(anything())
            .inAdapterView(withId(R.id.notif_listview))
            .atPosition(0)
            .perform(click());

        // Verifying all information matches
        onView(withId(R.id.event_title)).check(matches(isDisplayed()));
        onView(withId(R.id.notif_date)).check(matches(isDisplayed()));
        onView(withId(R.id.notif_details)).check(matches(isDisplayed()));
    }

    /**
     * Test for correctly functioning back button from notification details
     */
    @Test
    public void testBackBtn() {
        testNotifDetailActivity();
        onView(withId(R.id.back_button)).perform(click());

        // Verifying back button returned to attendee profile
        onView(withId(R.id.notif_listview)).check(matches(isDisplayed()));
    }

}
