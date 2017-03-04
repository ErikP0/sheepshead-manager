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

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Collection;

import sheepshead.manager.R;
import sheepshead.manager.activities.displayscores.DisplayScoresHome;
import sheepshead.manager.appcore.SheepsheadManagerApplication;
import sheepshead.manager.game.Player;
import sheepshead.manager.game.PlayerRole;
import sheepshead.manager.game.SingleGameResult;
import sheepshead.manager.session.Session;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class DisplayScoresHomeTestDummy {
    private static final Session dummySession = new DummySession();
    private static final Collection<Player> playersInSession = dummySession.getPlayers();
    @Rule
    public ActivityTestRule<DisplayScoresHome> activityRule = new ActivityTestRule<>(DisplayScoresHome.class);


    @BeforeClass
    public static void createDummySession() {
        new SheepsheadManagerApplication().onCreate();
        SheepsheadManagerApplication.getInstance().setCurrentSession(dummySession);
    }

    @Test
    public void testDummySession() {
        SingleGameResult latestResult = dummySession.getLatestResult();
        testDisplay(latestResult);
    }

    private void testDisplay(SingleGameResult result) {
        for (Player p : playersInSession) {
            int balance = p.getSessionMoney();
            PlayerRole role = result.findRole(p);
            if (role != null) {
                DisplayScoresHomeTestEmpty.checkResultFor(p.getName(), balance + "(" + role.getMoney() + ")");
            } else {
                DisplayScoresHomeTestEmpty.checkResultFor(p.getName(), "" + balance);
            }
        }
    }

    @Test
    public void testRemoveGameResult() {
        SingleGameResult toBeRemoved = dummySession.getLatestResult();
        testDisplay(toBeRemoved);
        onView(withId(R.id.DisplayScores_btn_delete_result)).perform(click());
        onView(withText(R.string.DisplayScores_dialog_delete_confirm)).perform(click());
        SingleGameResult latest = dummySession.getLatestResult();
        assertTrue(toBeRemoved != latest);
        testDisplay(latest);
    }
}
