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

package sheepshead.manager.activities.displayscores;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;

import sheepshead.manager.R;
import sheepshead.manager.activities.fillgameresult.FillGameResult;
import sheepshead.manager.appcore.AbstractBaseActivity;
import sheepshead.manager.appcore.ActivityDescriptor;
import sheepshead.manager.appcore.SheepsheadManagerApplication;
import sheepshead.manager.game.Player;
import sheepshead.manager.game.PlayerRole;
import sheepshead.manager.game.SingleGameResult;
import sheepshead.manager.session.Session;

/**
 * Activity that shows an overview for the current session.
 * It shows the current balance of all players inclusive their earnings/losses in the latest game.
 * The user can fill in a new game result or show the whole result table ("ScoreBoard")
 */
public class DisplayScoresHome extends AbstractBaseActivity {

    private static final ActivityDescriptor DISPLAY_SCORES_HOME = new ActivityDescriptor(R.layout.activity_display_scores_home)
            .toolbar(R.id.DisplayScores_toolbar)
            .title(R.string.Title_DisplayScoresHome)
            .enableNavigationBackToParent();

    /**
     * The current session
     */
    private Session session;

    /**
     * Don't create this activity by hand. Use an intent
     */
    public DisplayScoresHome() {
        super(DISPLAY_SCORES_HOME);
        session = SheepsheadManagerApplication.getInstance().getCurrentSession();
        if (session == null) {
            throw new IllegalStateException(DisplayScoresHome.class.getSimpleName() + ": Cannot find session");
        }
    }

    @Override
    protected void registerActivitySpecificServices() {

    }

    @Override
    protected void removeActivitySpecificServices() {

    }

    @Override
    protected void updateUserInterface() {
        //generate and show the overview of players balances
        TableLayout playerPanel = findView(R.id.DisplayScores_panel_player_scores);
        createPlayerOverviewArea(playerPanel);
    }

    @Override
    protected void createUserInterface(Bundle savedInstanceState) {

        //add functionality to the scoreboard button
        Button showScoreboardButton = findView(R.id.DisplayScores_btn_show_table);
        showScoreboardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showScoreboard();
            }
        });

        //add functionality to the fill game result button
        Button addResultButton = findView(R.id.DisplayScores_btn_add_single_game_result);
        addResultButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DisplayScoresHome.this, FillGameResult.class);
                startActivity(intent);
            }
        });
    }

    private void createPlayerOverviewArea(TableLayout panel) {
        panel.removeAllViews();
        SingleGameResult latestGame = session.getLatestResult();
        for (Player player : session.getPlayers()) {
            PlayerRole role = null;
            if (latestGame != null) {
                role = latestGame.findRole(player);
            }
            panel.addView(new PlayerScoreOverwievBuilder(new PlayerScoreEntry(player, role)).build(this));
        }
    }

    private void showScoreboard() {
        Intent intent = new Intent(DisplayScoresHome.this, DisplayScoresTable.class);
        startActivity(intent);
    }
}
