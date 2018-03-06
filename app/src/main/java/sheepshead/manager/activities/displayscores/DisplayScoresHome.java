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

package sheepshead.manager.activities.displayscores;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TableLayout;

import sheepshead.manager.R;
import sheepshead.manager.activities.fillgameresult.FillGameResult;
import sheepshead.manager.appcore.AbstractBaseActivity;
import sheepshead.manager.appcore.ActivityDescriptor;
import sheepshead.manager.appcore.SheepsheadManagerApplication;
import sheepshead.manager.export.EmailExport;
import sheepshead.manager.export.FileExport;
import sheepshead.manager.game.Player;
import sheepshead.manager.game.PlayerRole;
import sheepshead.manager.game.SingleGameResult;
import sheepshead.manager.serialization.SerializationActions;
import sheepshead.manager.session.Session;

/**
 * Activity that shows an overview for the current session.
 * It shows the current balance of all players inclusive their earnings/losses in the latest game.
 * The user can fill in a new game result or show the whole result table ("ScoreBoard")
 */
public class DisplayScoresHome extends AbstractBaseActivity {

    /**
     * Describes static behaviour of this activity
     */
    private static final ActivityDescriptor DISPLAY_SCORES_HOME = new ActivityDescriptor(R.layout.activity_display_scores_home)
            .toolbar(R.id.DisplayScores_toolbar)
            .title(R.string.Title_DisplayScoresHome)
            .menuAction(R.id.menu_saveSession, FileExport.saveCurrentSession(SerializationActions.sessionSaveDirectory))
            .menuAction(R.id.menu_shareEmail, EmailExport.emailCurrentSession())
            .toolbarMenu(R.menu.menu_display_scores_home)
            .enableNavigationBackToParent();

    /**
     * The current session
     */
    private Session session;

    private ImageButton deleteGameButton;

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
        deleteGameButton.setEnabled(session.getLatestResult() != null);
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
                intentToUpActivity(FillGameResult.class);
            }
        });

        //add functionality to the delete result button
        deleteGameButton = findView(R.id.DisplayScores_btn_delete_result);
        deleteGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteLatestGameResult();
            }
        });
    }

    private void createPlayerOverviewArea(TableLayout panel) {
        //remove all old child views before adding the latest ones
        panel.removeAllViews();
        SingleGameResult latestGame = session.getLatestResult();
        for (Player player : session.getPlayers()) {
            PlayerRole role = null;
            if (latestGame != null) {
                role = latestGame.findRole(player);
            }
            panel.addView(new PlayerScoreOverviewBuilder(new PlayerScoreEntry(player, role)).build(this));
        }
    }

    private void showScoreboard() {
        intentToUpActivity(DisplayScoresTable.class);
    }

    @Override
    public void onStop() {
        super.onStop();
        SheepsheadManagerApplication.getInstance().saveApplicationState();
    }

    private void deleteLatestGameResult() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.DisplayScores_dialog_delete_message);
        builder.setCancelable(true).setNegativeButton(R.string.DisplayScores_dialog_delete_abort, null);
        builder.setPositiveButton(R.string.DisplayScores_dialog_delete_confirm, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == AlertDialog.BUTTON_POSITIVE) {
                    session.removeLatestGameResult();
                    updateUserInterface();
                }
            }
        });
        builder.show();
    }
}
