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
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import sheepshead.manager.R;
import sheepshead.manager.activities.fillgameresult.FillGameResult;
import sheepshead.manager.appcore.AbstractBaseActivity;
import sheepshead.manager.appcore.ActivityDescriptor;
import sheepshead.manager.appcore.SheepsheadManagerApplication;
import sheepshead.manager.export.EmailExport;
import sheepshead.manager.export.FileExport;
import sheepshead.manager.game.Player;
import sheepshead.manager.game.SingleGameResult;
import sheepshead.manager.serialization.SerializationActions;
import sheepshead.manager.session.Session;
import sheepshead.manager.uicontrolutils.DetailsGame;
import sheepshead.manager.uicontrolutils.TextViewUtils;
import sheepshead.manager.utils.Conversions;

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

        TableLayout totalScoreTable = findView(R.id.DisplayScores_total_score_table);
        createTotalScoreTable(totalScoreTable);

        DetailsGame detailsGame = findView(R.id.DisplayScores_latest_game_details);
        String title = getString(R.string.DisplayScores_text_last_game, session.getGameAmount());
        detailsGame.setGame(this, session.getLatestResult(), title, v -> deleteLatestGameResult());
    }

    @Override
    protected void createUserInterface(Bundle savedInstanceState) {

        //add functionality to the scoreboard button
        View showScoreboardView = findView(R.id.DisplayScores_show_score_table);
        showScoreboardView.setOnClickListener(v -> showScoreboard());

        //add functionality to the fill game result button
        FloatingActionButton addResultButton = findView(R.id.DisplayScores_btn_add_single_game_result);
        addResultButton.setOnClickListener(v -> intentToUpActivity(FillGameResult.class));
    }

    private void createTotalScoreTable(TableLayout panel) {
        //remove all old child views before adding the latest ones
        panel.removeAllViews();
        SingleGameResult latestGame = session.getLatestResult();

        List<Player> players = new ArrayList<>(session.getPlayers());
        Collections.sort(players, (p1, p2) -> -Integer.compare(p1.getSessionMoney(), p2.getSessionMoney()));
        int padding = Conversions.dpToPx(this, 4);
        for (Player player : players) {
            TableRow row = new TableRow(this);
            TextView name = new TextView(this);
            name.setText(player.getName());
            name.setPadding(padding, padding, padding, padding);
            row.addView(name);
            TextView money = TextViewUtils.createColoredText(this, player.getSessionMoney());
            money.setPadding(padding, padding, padding, padding);
            row.addView(money);
            panel.addView(row);
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
