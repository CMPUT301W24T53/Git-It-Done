package com.example.gidevents;

import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.isDialog;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.anything;
import static org.hamcrest.CoreMatchers.is;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;


import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class BrowseEventsTest {
    @Rule
    public ActivityScenarioRule<BrowseEventActivity> scenario = new
            ActivityScenarioRule<BrowseEventActivity>(BrowseEventActivity.class);
    @Before
    public void setUp() {
        Intents.init();
    }

    /** This tests if the activity switches to EventsDetailsPage after clicking on listview
     * Then tests if the sign up dialog pops up after clicking the sign up button
     */
    @Test
    public void testActivitySwitch() {
        onData(anything()).inAdapterView(withId(R.id.browze_events_listview)).atPosition(0).perform(click());
        intended(hasComponent(EventDetailsPageActivity.class.getName()));
        onView(withId(R.id.sign_up_button)).perform(click());
        onView(withText("Username:"))
                .inRoot(isDialog()) 
                .check(matches(isDisplayed()));

    }
    @After
    public void tearDown() {
        Intents.release();
    }
}