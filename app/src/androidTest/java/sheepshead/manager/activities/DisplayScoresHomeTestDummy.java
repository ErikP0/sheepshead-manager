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

import sheepshead.manager.activities.displayscores.DisplayScoresHome;
import sheepshead.manager.appcore.SheepsheadManagerApplication;
import sheepshead.manager.game.Player;
import sheepshead.manager.game.PlayerRole;
import sheepshead.manager.game.SingleGameResult;
import sheepshead.manager.session.Session;

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
        for (Player p : playersInSession) {
            int balance = p.getSessionMoney();
            PlayerRole role = latestResult.findRole(p);
            if (role != null) {
                DisplayScoresHomeTestEmpty.checkResultFor(p.getName(), balance + "(" + role.getMoney() + ")");
            } else {
                DisplayScoresHomeTestEmpty.checkResultFor(p.getName(), "" + balance);
            }
        }
    }
}
