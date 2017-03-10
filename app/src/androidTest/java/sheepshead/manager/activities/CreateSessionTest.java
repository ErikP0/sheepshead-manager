/*
 * Copyright 2017  Erik Pohle
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


import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;
import android.widget.EditText;

import org.hamcrest.Matcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import sheepshead.manager.R;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.clearText;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeTextIntoFocusedView;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.hasFocus;
import static android.support.test.espresso.matcher.ViewMatchers.hasSibling;
import static android.support.test.espresso.matcher.ViewMatchers.isEnabled;
import static android.support.test.espresso.matcher.ViewMatchers.supportsInputMethods;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.not;

@RunWith(AndroidJUnit4.class)
public class CreateSessionTest {

    @Rule
    public ActivityTestRule<CreateSession> activityRule = new ActivityTestRule<>(CreateSession.class);

    @Test
    public void testStakeInput() {

        //click on select stake header
        onView(withText(R.string.CreateSession_header_stake)).perform(click());
        //check that stake for sauspiel and solo are displayed
        onView(withText(R.string.CreateSession_text_stake_sauspiel)).check(matches(withText(R.string.CreateSession_text_stake_sauspiel)));
        onView(withText(R.string.CreateSession_text_stake_solo)).check(matches(withText(R.string.CreateSession_text_stake_solo)));

        //check that default values are displayed
        onView(withId(R.id.CreateSession_input_sauspiel)).check(matches(withText(R.string.CreateSession_default_value_sauspiel)));
        onView(withId(R.id.CreateSession_input_solo)).check(matches(withText(R.string.CreateSession_default_value_solo)));

        openPlayerSelection();
        for (int i = 0; i < 4; i++) {
            addPlayer("Spieler " + i);
        }
        closePlayerSelection();

        //type invalid values
        int sauspiel = R.id.CreateSession_input_sauspiel;
        int solo = R.id.CreateSession_input_solo;
        testInvalidStakeInput(sauspiel);
        testInvalidStakeInput(solo);
    }

    private void testInvalidStakeInput(int stakeId) {
        checkIfCreateSessionButtonIs(true);
        typeStakeInput(stakeId, "");
        checkIfCreateSessionButtonIs(false);
        typeStakeInput(stakeId, "10");
        checkIfCreateSessionButtonIs(true);
        typeStakeInput(stakeId, "0");
        checkIfCreateSessionButtonIs(false);
        typeStakeInput(stakeId, "10");
        checkIfCreateSessionButtonIs(true);
    }

    private void typeStakeInput(int inputId, String input) {
        final EditText text = (EditText) activityRule.getActivity().findViewById(inputId);
        getInstrumentation().runOnMainSync(new Runnable() {
            public void run() {
                text.requestFocus();
            }
        });
        onView(withId(inputId)).perform(clearText());
        onView(allOf(hasFocus(), withId(inputId), supportsInputMethods())).perform(typeTextIntoFocusedView(input), closeSoftKeyboard());
    }

    private void openPlayerSelection() {
        //click on select player
        onView(withText(R.string.CreateSession_header_players)).perform(click());
    }

    private void closePlayerSelection() {
        //close select player
        onView(withText(R.string.CreateSession_header_players)).perform(click());
    }

    private void addPlayer(String name) {
        //click + Button
        onView(withId(R.id.CreateSession_AddPlayer)).perform(click());
        //enter name
        onView(allOf(hasFocus(), supportsInputMethods())).perform(typeTextIntoFocusedView(name));
        //click ok (button1 = ok, button2 = cancel)
        onView(withId(android.R.id.button1)).perform(click());
        //check if name is displayed somewhere
        onView(withText(name)).check(matches(withText(name)));


    }

    private void checkIfCreateSessionButtonIs(boolean enabled) {
        Matcher<View> matcher = enabled ? isEnabled() : not(isEnabled());
        onView(withId(R.id.CreateSession_btn_create)).check(matches(matcher));
    }

    private void checkIfPlayerPresent(String name, boolean isPresent) {
        if (isPresent) {
            onView(withText(name)).check(matches(withText(name)));
        } else {
            onView(withText(name)).check(doesNotExist());
        }
    }

    @Test
    public void testAddPlayer() {
        String[] players = {"Simon", "Clemens", "Arian", "Nico", "Erik"};
        checkIfCreateSessionButtonIs(false);
        openPlayerSelection();
        for (int i = 0; i < players.length; i++) {

            //check if before added players are present
            for (int k = 0; k < i; k++) {
                checkIfPlayerPresent(players[k], true);
            }

            addPlayer(players[i]);

            //check if create session button is enabled when >=4 players are added
            if (i < 3) {
                checkIfCreateSessionButtonIs(false);
            } else {
                checkIfCreateSessionButtonIs(true);
            }
        }
        closePlayerSelection();
    }

    @Test
    public void testAddPlayerDialogBack() {
        openPlayerSelection();

        //click + Button
        onView(withId(R.id.CreateSession_AddPlayer)).perform(click());
        //enter name
        onView(allOf(hasFocus(), supportsInputMethods())).perform(typeTextIntoFocusedView("very important name"));
        //click ok (button1 = ok, button2 = cancel)
        onView(withId(android.R.id.button2)).perform(click());

        //close select player
        onView(withText(R.string.CreateSession_header_players)).perform(click());

        checkIfPlayerPresent("very important name", false);
    }

    @Test
    public void testAddInvalidPlayerName() {
        String invalidName = "";

        openPlayerSelection();

        //click + Button
        onView(withId(R.id.CreateSession_AddPlayer)).perform(click());
        //enter name
        onView(allOf(hasFocus(), supportsInputMethods())).perform(typeTextIntoFocusedView(invalidName));
        //click ok (button1 = ok, button2 = cancel)
        onView(withId(android.R.id.button1)).perform(click());

        //check if another dialog appeared
        onView(withText(R.string.CreateSession_warning_invalid_player_name))
                .check(matches(withText(R.string.CreateSession_warning_invalid_player_name)));
    }

    @Test
    public void testAddExistingPlayer() {
        String name = "Doppelgaenger";
        openPlayerSelection();
        addPlayer(name);

        //click + Button
        onView(withId(R.id.CreateSession_AddPlayer)).perform(click());
        //enter name
        onView(allOf(hasFocus(), supportsInputMethods())).perform(typeTextIntoFocusedView(name));
        //click ok (button1 = ok, button2 = cancel)
        onView(withId(android.R.id.button1)).perform(click());

        //check if another dialog appeared
        onView(withText(R.string.CreateSession_warning_player_already_exists))
                .check(matches(withText(R.string.CreateSession_warning_player_already_exists)));

        //press ok
        onView(withId(android.R.id.button1)).perform(click());

        //click + Button
        onView(withId(R.id.CreateSession_AddPlayer)).perform(click());

        //enter name
        onView(allOf(hasFocus(), supportsInputMethods())).perform(typeTextIntoFocusedView(name));
        //click back
        onView(withId(android.R.id.button2)).perform(click());

        //expect no dialog to display
        onView(withText(R.string.CreateSession_warning_player_already_exists)).check(doesNotExist());
    }

    @Test
    public void testRemovePlayer() {
        openPlayerSelection();
        String[] namesToAdd = {"Don't remove me pls", "Don't remove me pls!", "Don't remove me pls!!"};
        String[] nameToBeRemoved = {"Remove me", "Remove me!"};
        addPlayer(namesToAdd[0]);
        addPlayer(nameToBeRemoved[0]);
        addPlayer(namesToAdd[1]);
        addPlayer(namesToAdd[2]);
        addPlayer(nameToBeRemoved[1]);
        checkIfCreateSessionButtonIs(true);

        //remove players
        removePlayer(nameToBeRemoved[0]);
        //check that the rest is still there
        for (String name : namesToAdd) {
            checkIfPlayerPresent(name, true);
        }
        checkIfPlayerPresent(nameToBeRemoved[1], true);
        //4 players left
        checkIfCreateSessionButtonIs(true);

        //remove next player
        removePlayer(nameToBeRemoved[1]);
        //check that the rest is still there
        for (String name : namesToAdd) {
            checkIfPlayerPresent(name, true);
        }
        //3 players left
        checkIfCreateSessionButtonIs(false);
    }

    private void removePlayer(String name) {
        onView(allOf(withId(R.id.CreateSession_RemovePlayer), hasSibling(withText(name)))).perform(click());
        checkIfPlayerPresent(name, false);
    }
}
