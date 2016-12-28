/*
 * Copyright 2016  Erik Pohle
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package sheepshead.manager.activities;


import android.support.annotation.StringRes;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;

import org.hamcrest.Matcher;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.List;

import sheepshead.manager.R;
import sheepshead.manager.activities.fillgameresult.FillGameResult;
import sheepshead.manager.appcore.SheepsheadManagerApplication;
import sheepshead.manager.game.GameType;
import sheepshead.manager.session.Session;
import sheepshead.manager.session.Stake;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.RootMatchers.isDialog;
import static android.support.test.espresso.matcher.ViewMatchers.hasSibling;
import static android.support.test.espresso.matcher.ViewMatchers.isChecked;
import static android.support.test.espresso.matcher.ViewMatchers.isClickable;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isEnabled;
import static android.support.test.espresso.matcher.ViewMatchers.isNotChecked;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static junit.framework.Assert.fail;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.not;

@RunWith(AndroidJUnit4.class)
public class FillGameResultTest {

    private static String[] players = {"p1", "p2", "p3", "p4"};

    @Rule
    public ActivityTestRule<FillGameResult> activityRule = new ActivityTestRule<>(FillGameResult.class);

    @BeforeClass
    public static void setupSession() {
        List<String> allPlayers = Arrays.asList(players);
        Session s = new Session(allPlayers, new Stake(10, 50, 10));
        new SheepsheadManagerApplication().onCreate();
        SheepsheadManagerApplication.getInstance().setCurrentSession(s);
    }

    @Test
    public void testGameTypeSelection() {
        final int idSauspiel = R.id.FillGameResult_toggleBtn_sauspiel;
        final int idSolo = R.id.FillGameResult_toggleBtn_solo;
        final int idWenz = R.id.FillGameResult_toggleBtn_wenz;
        //test that only one game type button is pressed at all time

        //at the beginning, no button is pressed
        int[] ids = {idSauspiel, idSolo, idWenz};
        for (int id : ids) {
            onView(withId(id)).check(matches(isNotChecked()));
        }

        //click on SAUSPIEL
        onView(withId(idSauspiel)).perform(click());
        //assert SAUSPIEL is pressed
        onView(withId(idSauspiel)).check(matches(isChecked()));

        //click on WENZ
        onView(withId(idWenz)).perform(click());
        //assert that SAUSPIEL & SOLO are not pressed, WENZ is pressed
        onView(withId(idSauspiel)).check(matches(isNotChecked()));
        onView(withId(idSolo)).check(matches(isNotChecked()));
        onView(withId(idWenz)).check(matches(isChecked()));

        //click on SOLO
        onView(withId(idSolo)).perform(click());
        //assert that SAUSPIEL & WENZ are not pressed, SOLO is pressed
        onView(withId(idSauspiel)).check(matches(isNotChecked()));
        onView(withId(idSolo)).check(matches(isChecked()));
        onView(withId(idWenz)).check(matches(isNotChecked()));

        //another click on SOLO to deselect
        onView(withId(idSolo)).perform(click());
        //assert that no button is pressed
        for (int id : ids) {
            onView(withId(id)).check(matches(isNotChecked()));
        }
    }

    @Test
    public void testSchneiderSchwarzCheckboxes() {
        testDependentCheckboxes(R.id.FillGameResult_checkbox_is_schneider, R.id.FillGameResult_checkbox_is_schwarz);
    }

    @Test
    public void testKontraRe() {
        testDependentCheckboxes(R.id.FillGameResult_checkbox_is_kontra, R.id.FillGameResult_checkbox_is_re);
    }

    private void testDependentCheckboxes(int idPrimary, int idSecondary) {
        //test both are unchecked
        onView(withId(idPrimary)).check(matches(isNotChecked()));
        onView(withId(idSecondary)).check(matches(isNotChecked()));

        //click primary, expect primary checked, secondary unchecked
        onView(withId(idPrimary)).perform(click());
        onView(withId(idPrimary)).check(matches(isChecked()));
        onView(withId(idSecondary)).check(matches(isNotChecked()));

        //click secondary, expect both checked
        onView(withId(idSecondary)).perform(click());
        onView(withId(idPrimary)).check(matches(isChecked()));
        onView(withId(idSecondary)).check(matches(isChecked()));

        //click on primary, expect both unchecked (secondary cannot exist without primary)
        onView(withId(idPrimary)).perform(click());
        onView(withId(idPrimary)).check(matches(isNotChecked()));
        onView(withId(idSecondary)).check(matches(isNotChecked()));

        //click on secondary, expect both checked
        onView(withId(idSecondary)).perform(click());
        onView(withId(idPrimary)).check(matches(isChecked()));
        onView(withId(idSecondary)).check(matches(isChecked()));
    }

    @Test
    public void testToutSieCheckboxes() {
        final int idTout = R.id.FillGameResult_checkbox_is_tout;
        final int idSie = R.id.FillGameResult_checkbox_is_sie;
        //select Sauspiel
        onView(withId(R.id.FillGameResult_toggleBtn_solo)).perform(click());

        //test both are unchecked
        onView(withId(idTout)).check(matches(isNotChecked()));
        onView(withId(idSie)).check(matches(isNotChecked()));

        //click tout, expect tout checked, sie unchecked
        onView(withId(idTout)).perform(click());
        onView(withId(idTout)).check(matches(isChecked()));
        onView(withId(idSie)).check(matches(isNotChecked()));

        //click sie, expect tout unchecked, sie checked
        onView(withId(idSie)).perform(click());
        onView(withId(idTout)).check(matches(isNotChecked()));
        onView(withId(idSie)).check(matches(isChecked()));

        //click sie again, expect both unchecked
        onView(withId(idSie)).perform(click());
        onView(withId(idTout)).check(matches(isNotChecked()));
        onView(withId(idSie)).check(matches(isNotChecked()));
    }

    @Test
    public void testDefaultState() {
        //expect no game type selected
        onView(withId(R.id.FillGameResult_toggleBtn_sauspiel)).check(matches(isNotChecked()));
        onView(withId(R.id.FillGameResult_toggleBtn_wenz)).check(matches(isNotChecked()));
        onView(withId(R.id.FillGameResult_toggleBtn_solo)).check(matches(isNotChecked()));

        //expect caller won checked
        onView(withId(R.id.FillGameResult_checkbox_callerside_won)).check(matches(isChecked()));

        //expect all point modifiers unchecked
        int[] pointModifier = {R.id.FillGameResult_checkbox_is_schneider,
                R.id.FillGameResult_checkbox_is_schwarz,
                R.id.FillGameResult_checkbox_is_kontra,
                R.id.FillGameResult_checkbox_is_re,
                R.id.FillGameResult_checkbox_is_tout,
                R.id.FillGameResult_checkbox_is_sie};
        for (int id : pointModifier) {
            onView(withId(id)).check(matches(isNotChecked()));
        }

        onView(withId(R.id.FillGameResult_checkbox_is_tout)).check(matches(not(isEnabled())));
        onView(withId(R.id.FillGameResult_checkbox_is_sie)).check(matches(not(isEnabled())));

        //expect laufende spinner displaying one item (no game type selected)
        //TODO

    }

    private void selectGameTypeButton(GameType gameTypeToSelect) {
        int btnId = -1;
        switch (gameTypeToSelect) {
            case LEER://default game type, do nothing
                break;
            case SAUSPIEL:
                btnId = R.id.FillGameResult_toggleBtn_sauspiel;
                break;
            case WENZ:
                btnId = R.id.FillGameResult_toggleBtn_wenz;
                break;
            case SOLO:
                btnId = R.id.FillGameResult_toggleBtn_solo;
                break;
            default:
                fail("Unknown gametype " + gameTypeToSelect);
        }
        if (btnId > 0) {
            onView(withId(btnId)).perform(click());
        }
    }

    private void testGameTypeToutSie(GameType gameTypeToSelect, boolean enabledValueTout, boolean enabledValueSie) {
        selectGameTypeButton(gameTypeToSelect);
        Matcher<View> toutMatcher = enabledValueTout ? isEnabled() : not(isEnabled());
        Matcher<View> sieMatcher = enabledValueSie ? isEnabled() : not(isEnabled());
        onView(withId(R.id.FillGameResult_checkbox_is_tout)).check(matches(toutMatcher));
        onView(withId(R.id.FillGameResult_checkbox_is_sie)).check(matches(sieMatcher));
    }

    @Test
    public void testSauspielToutSieDisabled() {
        testGameTypeToutSie(GameType.SAUSPIEL, false, false);
    }

    @Test
    public void testWenzToutEnabledSieDisabled() {
        testGameTypeToutSie(GameType.WENZ, true, false);
    }

    @Test
    public void testSoloToutSieEnabled() {
        testGameTypeToutSie(GameType.SOLO, true, true);
    }

    @Test
    public void testNoGameTypeToutSieDisabled() {
        testGameTypeToutSie(GameType.LEER, false, false);
    }

    private void testPlayerAmount(@StringRes int buttonText, String[] namesToClick) {
        onView(allOf(withText(buttonText), isClickable())).check(matches(isEnabled()));
        onView(allOf(withText(buttonText), isClickable())).perform(click());
        for (String name : namesToClick) {
            //check if dialog is displayed
            onView(withId(R.id.PlayerSelectionDialog_title)).inRoot(isDialog()).check(matches(isDisplayed()));
            onView(allOf(withText(name), hasSibling(isClickable()), hasSibling(isNotChecked()))).perform(click());
        }
        //check if dialog has closed
        onView(withId(R.id.PlayerSelectionDialog_title)).check(doesNotExist());
    }

    @Test
    public void testPlayerSelectionSauspiel() {
        onView(allOf(withText(R.string.FillGameResult_editCallers), isClickable())).check(matches(not(isEnabled())));
        onView(allOf(withText(R.string.FillGameResult_editNonCallers), isClickable())).check(matches(not(isEnabled())));
        selectGameTypeButton(GameType.SAUSPIEL);
        String[] callers = {"p1", "p2"};
        testPlayerAmount(R.string.FillGameResult_editCallers, callers);
    }

    @Test
    public void testPlayerSelectionSauspiel2() {
        onView(allOf(withText(R.string.FillGameResult_editCallers), isClickable())).check(matches(not(isEnabled())));
        onView(allOf(withText(R.string.FillGameResult_editNonCallers), isClickable())).check(matches(not(isEnabled())));
        selectGameTypeButton(GameType.SAUSPIEL);
        String[] nonCallers = {"p3", "p4"};
        testPlayerAmount(R.string.FillGameResult_editNonCallers, nonCallers);
    }

    @Test
    public void testPlayerSelectionSolo() {
        onView(allOf(withText(R.string.FillGameResult_editCallers), isClickable())).check(matches(not(isEnabled())));
        onView(allOf(withText(R.string.FillGameResult_editNonCallers), isClickable())).check(matches(not(isEnabled())));
        selectGameTypeButton(GameType.SOLO);
        String[] callers = {"p1"};
        testPlayerAmount(R.string.FillGameResult_editCallers, callers);
    }

    @Test
    public void testPlayerSelectionSolo2() {
        onView(allOf(withText(R.string.FillGameResult_editCallers), isClickable())).check(matches(not(isEnabled())));
        onView(allOf(withText(R.string.FillGameResult_editNonCallers), isClickable())).check(matches(not(isEnabled())));
        selectGameTypeButton(GameType.SOLO);
        String[] nonCallers = {"p2", "p3", "p4"};
        testPlayerAmount(R.string.FillGameResult_editNonCallers, nonCallers);
    }

    @Test
    public void testPlayerSelectionWenz() {
        onView(allOf(withText(R.string.FillGameResult_editCallers), isClickable())).check(matches(not(isEnabled())));
        onView(allOf(withText(R.string.FillGameResult_editNonCallers), isClickable())).check(matches(not(isEnabled())));
        selectGameTypeButton(GameType.SOLO);
        String[] callers = {"p1"};
        testPlayerAmount(R.string.FillGameResult_editCallers, callers);
    }

    @Test
    public void testPlayerSelectionWenz2() {
        onView(allOf(withText(R.string.FillGameResult_editCallers), isClickable())).check(matches(not(isEnabled())));
        onView(allOf(withText(R.string.FillGameResult_editNonCallers), isClickable())).check(matches(not(isEnabled())));
        selectGameTypeButton(GameType.SOLO);
        String[] nonCallers = {"p2", "p3", "p4"};
        testPlayerAmount(R.string.FillGameResult_editNonCallers, nonCallers);
    }


}
