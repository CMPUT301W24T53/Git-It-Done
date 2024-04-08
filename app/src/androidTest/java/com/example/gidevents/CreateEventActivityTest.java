package com.example.gidevents;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.Intents.intending;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.net.Uri;

import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class CreateEventActivityTest {
    @Rule
    public ActivityScenarioRule<CreateEventActivity> scenario = new ActivityScenarioRule<CreateEventActivity>(CreateEventActivity.class);
    @Before
    public void setUp() {
        Intents.init();
    }

    @After
    public void tearDown() {
        Intents.release();
    }
    @Test
    // Testing updating of key information
    public void testEventInfoInput(){
        onView(withId(R.id.etEventOrganizer)).perform(ViewActions.typeText("Test Organizer"));
        onView(withId(R.id.etEventTitle)).perform(ViewActions.typeText("Test Title"));
        onView(withId(R.id.etEventLocation)).perform(ViewActions.typeText("Test Location"));
        onView(withId(R.id.etEventDescription)).perform(ViewActions.typeText("Test Description"));
        onView(withId(R.id.btnSelectDate)).perform(click());

        onView(withText("30")).perform(click());
        onView(withText("OK")).perform(click());
        onView(withText("12")).perform(click());
        onView(withText("12")).perform(click());
        onView(withText("AM")).perform(click());
        onView(withText("OK")).perform(click());


        onView(withId(R.id.btnUploadPoster)).perform(click());
        Intent resultData = new Intent();
        Uri imageUri = Uri.parse("android.resource://com.example.gidevents/drawable/ic_event");
        resultData.setData(imageUri);
        Instrumentation.ActivityResult result = new Instrumentation.ActivityResult(Activity.RESULT_OK, resultData);
        intending(hasAction(Intent.ACTION_GET_CONTENT)).respondWith(result);

        onView(withId(R.id.btnGenerateQRCodes)).perform(click());

        intended(hasComponent(QRCodeActivity.class.getName()));

    }
}
