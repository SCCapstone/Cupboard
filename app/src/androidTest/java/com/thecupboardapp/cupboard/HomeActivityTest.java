package com.thecupboardapp.cupboard;

import android.support.test.espresso.contrib.DrawerActions;
import android.support.test.espresso.contrib.NavigationViewActions;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.thecupboardapp.cupboard.activities.HomeActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;


/**
 * Created by Bradley on 2/4/2018.
 */

@RunWith(AndroidJUnit4.class)

@LargeTest
public class HomeActivityTest {
    @Rule
    public ActivityTestRule<HomeActivity> mActivityRule =
            new ActivityTestRule(HomeActivity.class);

    @Test
    public void testClickActionBarItem() {
        //checks that the Home screen opens correctly and the "home" text is present
        onView(withId(R.id.home_fragmentTextView)).check(matches(withText("Home")));
        //opens the navigation drawer
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        //navigates to cupboard screen from navigation drawer
        onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.nav_cupboard));
    }
}
