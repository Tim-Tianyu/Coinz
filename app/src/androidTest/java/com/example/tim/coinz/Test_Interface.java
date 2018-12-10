package com.example.tim.coinz;


import android.support.test.espresso.ViewInteraction;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.rule.GrantPermissionRule;
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
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class Test_Interface {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Rule
    public GrantPermissionRule mGrantPermissionRule =
            GrantPermissionRule.grant(
                    "android.permission.ACCESS_FINE_LOCATION");

    @Test
    public void test_Interface() {
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

        ViewInteraction editText = onView(withId(R.id.activity_main_et_email));
        editText.check(matches(withText("")));

        ViewInteraction editText2 = onView(withId(R.id.activity_main_et_password));
        editText2.check(matches(withText("")));

        ViewInteraction button = onView(withId(R.id.activity_main_btn_log_in));
        button.check(matches(isDisplayed()));

        ViewInteraction button2 = onView(withId(R.id.activity_main_btn_sign_up));
        button2.check(matches(isDisplayed()));

        ViewInteraction appCompatButton = onView(
                allOf(withId(R.id.activity_main_btn_sign_up), withText("Sign up"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                1),
                        isDisplayed()));
        appCompatButton.perform(click());

        ViewInteraction editText3 = onView(withId(R.id.sign_up_dialog_et_email));
        editText3.check(matches(isDisplayed()));

        ViewInteraction editText4 = onView(withId(R.id.sign_up_dialog_et_name));
        editText4.check(matches(isDisplayed()));

        ViewInteraction editText5 = onView(withId(R.id.sign_up_dialog_et_password));
        editText5.check(matches(isDisplayed()));

        ViewInteraction editText6 = onView(withId(R.id.sign_up_dialog_et_password_re));
        editText6.check(matches(isDisplayed()));

        ViewInteraction button3 = onView(withId(R.id.sign_up_dialog_btn_confirm));
        button3.check(matches(isDisplayed()));

        ViewInteraction button4 = onView(withId(R.id.sign_up_dialog_btn_cancel));
        button4.check(matches(isDisplayed()));

        ViewInteraction appCompatButton2 = onView(
                allOf(withId(R.id.sign_up_dialog_btn_cancel), withText("Cancel"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.LinearLayout")),
                                        6),
                                1),
                        isDisplayed()));
        appCompatButton2.perform(click());

        try {
            Thread.sleep(700);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction appCompatEditText = onView(
                allOf(withId(R.id.activity_main_et_email),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                4),
                        isDisplayed()));
        appCompatEditText.perform(replaceText("tim.wang.tianyu@gmail.com"), closeSoftKeyboard());

        ViewInteraction appCompatEditText2 = onView(
                allOf(withId(R.id.activity_main_et_password),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                2),
                        isDisplayed()));
        appCompatEditText2.perform(replaceText("timtim"), closeSoftKeyboard());

        ViewInteraction appCompatButton3 = onView(
                allOf(withId(R.id.activity_main_btn_log_in), withText("LOG IN"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                0),
                        isDisplayed()));
        appCompatButton3.perform(click());

        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction textView5 = onView(withText("Select Game Mode"));
        textView5.check(matches(withText("Select Game Mode")));

        ViewInteraction button5 = onView(withId(R.id.activity_game_select_btn_normal));
        button5.check(matches(isDisplayed()));

        ViewInteraction button6 = onView(withId(R.id.activity_game_select_btn_hunt));
        button6.check(matches(isDisplayed()));

        ViewInteraction appCompatButton4 = onView(
                allOf(withId(R.id.activity_game_select_btn_normal), withText("Normal"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                0),
                        isDisplayed()));
        appCompatButton4.perform(click());

        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        try {
            Thread.sleep(700);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction button7 = onView(withId(R.id.activity_map_btn_log_out));
        button7.check(matches(isDisplayed()));

        ViewInteraction button8 = onView(withId(R.id.activity_map_btn_reward));
        button8.check(matches(isDisplayed()));

        ViewInteraction button9 = onView(withId(R.id.activity_map_btn_friends));
        button9.check(matches(isDisplayed()));

        ViewInteraction button10 = onView(withId(R.id.activity_map_btn_bank));
        button10.check(matches(isDisplayed()));

        ViewInteraction button11 = onView(withId(R.id.activity_map_btn_wallet));
        button11.check(matches(isDisplayed()));

        ViewInteraction appCompatButton5 = onView(withId(R.id.activity_map_btn_log_out));
        appCompatButton5.perform(click());

        try {
            Thread.sleep(700);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction appCompatButton6 = onView(
                allOf(withId(android.R.id.button2), withText("Cancel"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.ScrollView")),
                                        0),
                                2)));
        appCompatButton6.perform(scrollTo(), click());

        try {
            Thread.sleep(700);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction appCompatButton7 = onView(
                allOf(withId(R.id.activity_map_btn_reward), withText("Reward"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                5),
                        isDisplayed()));
        appCompatButton7.perform(click());

        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        try {
            Thread.sleep(700);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction textView7 = onView(withId(R.id.activity_reward_txt_distance));
        textView7.check(matches(withText("Daily walking distance: 0.00")));

        ViewInteraction textView8 = onView(withId(R.id.activity_reward_text_explanation));
        textView8.check(matches(withText("Reached 0.00 meters, collect reward now!")));

        ViewInteraction button12 = onView(withId(R.id.activity_reward_btn_collect));
        button12.check(matches(isDisplayed()));

        pressBack();

        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        try {
            Thread.sleep(700);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction appCompatButton8 = onView(
                allOf(withId(R.id.activity_map_btn_friends), withText("Friends"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                3),
                        isDisplayed()));
        appCompatButton8.perform(click());

        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        try {
            Thread.sleep(700);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction textView9 = onView(withId(R.id.textView3));
        textView9.check(matches(withText("Friends")));

        ViewInteraction button13 = onView(withId(R.id.activity_friend_btn_requests));
        button13.check(matches(isDisplayed()));

        ViewInteraction button14 = onView(withId(R.id.activity_friend_btn_add));
        button14.check(matches(isDisplayed()));

        ViewInteraction appCompatButton9 = onView(withId(R.id.activity_friend_btn_requests));
        appCompatButton9.perform(click());

        try {
            Thread.sleep(700);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction appCompatButton10 = onView(
                allOf(withId(R.id.list_view_dialog_btn_close), withText("Close"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                1),
                        isDisplayed()));
        appCompatButton10.perform(click());

        try {
            Thread.sleep(700);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction appCompatButton11 = onView(
                allOf(withId(R.id.activity_friend_btn_add), withText("Add"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                0),
                        isDisplayed()));
        appCompatButton11.perform(click());

        try {
            Thread.sleep(700);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction button15 = onView(withId(R.id.add_friend_dialog_btn_send));
        button15.check(matches(isDisplayed()));

        ViewInteraction button16 = onView(withId(R.id.add_friend_dialog_btn_copy));
        button16.check(matches(isDisplayed()));

        ViewInteraction button17 = onView(withId(R.id.add_friend_dialog_btn_close));
        button17.check(matches(isDisplayed()));

        ViewInteraction editText7 = onView(withId(R.id.add_friend_dialog_et_uid));
        editText7.check(matches(withText("")));

        ViewInteraction appCompatButton12 = onView(
                allOf(withId(R.id.add_friend_dialog_btn_close), withText("Close"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                7),
                        isDisplayed()));
        appCompatButton12.perform(click());

        pressBack();

        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        try {
            Thread.sleep(700);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction appCompatButton13 = onView(
                allOf(withId(R.id.activity_map_btn_bank), withText("Bank"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                2),
                        isDisplayed()));
        appCompatButton13.perform(click());

        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        try {
            Thread.sleep(700);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction seekBar = onView(withId(R.id.activity_bank_sb_gold));
        seekBar.check(matches(isDisplayed()));

        ViewInteraction button18 = onView(withId(R.id.activity_bank_btn_transfer_gold));
        button18.check(matches(isDisplayed()));

        ViewInteraction button19 = onView(withId(R.id.activity_bank_btn_transfer_currency));
        button19.check(matches(isDisplayed()));

        pressBack();

        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        try {
            Thread.sleep(700);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction appCompatButton14 = onView(
                allOf(withId(R.id.activity_map_btn_wallet), withText("Wallet"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                1),
                        isDisplayed()));
        appCompatButton14.perform(click());

        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        try {
            Thread.sleep(700);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction textView10 = onView(withId(R.id.textView4));
        textView10.check(matches(withText("Wallet")));

        ViewInteraction button20 = onView(withId(R.id.activity_wallet_btn_receive));
        button20.check(matches(isDisplayed()));
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
