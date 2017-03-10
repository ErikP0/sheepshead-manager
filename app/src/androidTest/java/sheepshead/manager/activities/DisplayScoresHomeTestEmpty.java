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

import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import sheepshead.manager.R;
import sheepshead.manager.activities.displayscores.DisplayScoresHome;
import sheepshead.manager.appcore.SheepsheadManagerApplication;
import sheepshead.manager.game.Player;
import sheepshead.manager.session.Session;
import sheepshead.manager.session.Stake;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.hasSibling;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.allOf;

@RunWith(AndroidJUnit4.class)
public class DisplayScoresHomeTestEmpty {

    private static final Session dummySession = new DummySession();
    private static final Collection<Player> playersInSession = dummySession.getPlayers();
    @Rule
    public ActivityTestRule<DisplayScoresHome> activityRule = new ActivityTestRule<>(DisplayScoresHome.class);

    @BeforeClass
    public static void createEmptySession() {
        List<String> players = new ArrayList<>();
        for (Player player : playersInSession) {
            players.add(player.getName());
        }
        new SheepsheadManagerApplication().onCreate();
        SheepsheadManagerApplication.getInstance().setCurrentSession(new Session(players, new Stake(10, 50, 10)));
    }

    public static void checkResultFor(String name, String expected) {
        onView(allOf(withId(R.id.DisplayScoresOverview_text_player_score), hasSibling(withText(name)))).check(matches(withText(expected)));
    }

    private void createDummySession() {
        new SheepsheadManagerApplication().onCreate();
        SheepsheadManagerApplication.getInstance().setCurrentSession(dummySession);
    }

    @Test
    public void testEmptySession() {
        createEmptySession();
        for (Player p : playersInSession) {
            checkResultFor(p.getName(), "0");
        }
    }
}
