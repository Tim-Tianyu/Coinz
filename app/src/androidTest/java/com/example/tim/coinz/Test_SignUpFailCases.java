package com.example.tim.coinz;


import android.support.test.espresso.ViewInteraction;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class Test_SignUpFailCases {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void test_SignUpFailCases() {
        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        FeedReaderDbHelper dbHelper = new FeedReaderDbHelper(getInstrumentation().getTargetContext());
        dbHelper.cleanDatabase(dbHelper.getWritableDatabase());

        try {
            Thread.sleep(7000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction appCompatButton = onView(
                allOf(withId(R.id.btnSignUp), withText(" Sign Up"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                1),
                        isDisplayed()));
        appCompatButton.perform(click());

        ViewInteraction textView = onView(withId(R.id.sign_up_dialog_txt_helper));
        textView.check(matches(withText("")));

        ViewInteraction appCompatButton2 = onView(
                allOf(withId(R.id.sign_up_dialog_btn_confirm), withText("confirm"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.LinearLayout")),
                                        6),
                                0),
                        isDisplayed()));
        appCompatButton2.perform(click());

        ViewInteraction textView2 = onView(withId(R.id.sign_up_dialog_txt_helper));
        textView2.check(matches(withText("Invalid email address")));


        ViewInteraction appCompatEditText3 = onView(
                allOf(withId(R.id.sign_up_dialog_et_email),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                1),
                        isDisplayed()));
        appCompatEditText3.perform(replaceText("xx@xx.com"));

        ViewInteraction appCompatButton3 = onView(
                allOf(withId(R.id.sign_up_dialog_btn_confirm), withText("confirm"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.LinearLayout")),
                                        6),
                                0),
                        isDisplayed()));
        appCompatButton3.perform(click());

        ViewInteraction textView3 = onView(withId(R.id.sign_up_dialog_txt_helper));
        textView3.check(matches(withText("Empty username")));

        ViewInteraction appCompatEditText5 = onView(
                allOf(withId(R.id.sign_up_dialog_et_name),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                2),
                        isDisplayed()));
        appCompatEditText5.perform(replaceText("xx"), closeSoftKeyboard());

        ViewInteraction appCompatButton4 = onView(
                allOf(withId(R.id.sign_up_dialog_btn_confirm), withText("confirm"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.LinearLayout")),
                                        6),
                                0),
                        isDisplayed()));
        appCompatButton4.perform(click());

        ViewInteraction textView4 = onView(withId(R.id.sign_up_dialog_txt_helper));
        textView4.check(matches(withText("Empty password")));

        ViewInteraction appCompatEditText6 = onView(
                allOf(withId(R.id.sign_up_dialog_et_password),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                3),
                        isDisplayed()));
        appCompatEditText6.perform(replaceText("xx"), closeSoftKeyboard());

        ViewInteraction appCompatButton5 = onView(
                allOf(withId(R.id.sign_up_dialog_btn_confirm), withText("confirm"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.LinearLayout")),
                                        6),
                                0),
                        isDisplayed()));
        appCompatButton5.perform(click());

        ViewInteraction textView5 = onView(withId(R.id.sign_up_dialog_txt_helper));
        textView5.check(matches(withText("Confirm password not same as password")));

        ViewInteraction appCompatEditText7 = onView(
                allOf(withId(R.id.sign_up_dialog_et_password_re),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                4),
                        isDisplayed()));
        appCompatEditText7.perform(replaceText("xx"), closeSoftKeyboard());

        ViewInteraction appCompatButton6 = onView(
                allOf(withId(R.id.sign_up_dialog_btn_confirm), withText("confirm"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.LinearLayout")),
                                        6),
                                0),
                        isDisplayed()));
        appCompatButton6.perform(click());

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction textView6 = onView(withId(R.id.sign_up_dialog_txt_helper));
        textView6.check(matches(withText("Password too weak")));

        ViewInteraction appCompatEditText9 = onView(
                allOf(withId(R.id.sign_up_dialog_et_email), withText("xx@xx.com"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                1),
                        isDisplayed()));
        appCompatEditText9.perform(replaceText("tim.wang.tianyu@gmail.com"));

        ViewInteraction appCompatEditText11 = onView(
                allOf(withId(R.id.sign_up_dialog_et_password), withText("xx"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                3),
                        isDisplayed()));
        appCompatEditText11.perform(replaceText("xxeeww"));

        ViewInteraction appCompatEditText13 = onView(
                allOf(withId(R.id.sign_up_dialog_et_password_re), withText("xx"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                4),
                        isDisplayed()));
        appCompatEditText13.perform(replaceText("xxeeww"));

        ViewInteraction appCompatButton8 = onView(
                allOf(withId(R.id.sign_up_dialog_btn_confirm), withText("confirm"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.LinearLayout")),
                                        6),
                                0),
                        isDisplayed()));
        appCompatButton8.perform(click());

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction textView7 = onView(withId(R.id.sign_up_dialog_txt_helper));
        textView7.check(matches(withText("E-mail address already sign up")));
    }

    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }
}
