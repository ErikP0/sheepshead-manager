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

package sheepshead.manager.uicontrolutils;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import sheepshead.manager.R;
import sheepshead.manager.game.GameType;
import sheepshead.manager.game.PlayerRole;
import sheepshead.manager.game.SingleGameResult;
import sheepshead.manager.game.StakeModifier;
import sheepshead.manager.utils.Conversions;

/**
 * View that provides detailed information about one {@link SingleGameResult}.
 * The view can be customized with a title and will display the {@link GameType}, participating players,
 * their wins/losses and interesting modifier, like 3 Laufende.
 */
public class DetailsGame extends CardView {

    /**
     * the parent view
     */
    private CardView rootView;

    /**
     * title textview
     */
    private TextView title;
    /**
     * {@link GameType} textview
     */
    private TextView mode;
    /**
     * table for adding participants
     */
    private TableLayout participantTable;
    /**
     * container for adding interesting modifier as checkboxes
     */
    private LinearLayout properties;
    /**
     * the remove button
     */
    private Button removeBtn;

    //Constructor for XML
    public DetailsGame(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        inflateLayout(context);
    }

    private void inflateLayout(Context context) {
        rootView = (CardView) inflate(context, R.layout.details_game, this);
        title = rootView.findViewById(R.id.details_game_title);
        mode = rootView.findViewById(R.id.details_game_mode);
        participantTable = rootView.findViewById(R.id.details_game_player_table);
        properties = rootView.findViewById(R.id.details_game_properties_container);
        removeBtn = rootView.findViewById(R.id.details_game_btn_remove);
        reset();
    }

    /**
     * Resets the view to display no game result
     */
    private void reset() {
        title.setText(R.string.details_game_no_game);
        mode.setText("");
        participantTable.removeAllViews();
        properties.removeAllViews();
        removeBtn.setEnabled(false);
    }

    /**
     * Sets the game result for this view to display
     *
     * @param context      the creation context of the dynamic content
     * @param game         the game result to display
     * @param titlemessage the title to display
     * @param removeAction a click listener for the remove button, may be null
     */
    public void setGame(Context context, @Nullable SingleGameResult game, String titlemessage, @Nullable OnClickListener removeAction) {
        reset();
        if (game == null) {
            return;
        }
        title.setText(titlemessage);
        String gamemode = game.getGameType().toDisplayString(context);
        mode.setText(gamemode);
        removeBtn.setEnabled(removeAction != null);
        removeBtn.setOnClickListener(removeAction);

        List<PlayerRole> winners = new ArrayList<>();
        List<PlayerRole> losers = new ArrayList<>();
        for (PlayerRole role : game.getParticipants()) {
            if (role.isWinner()) {
                winners.add(role);
            } else {
                losers.add(role);
            }
        }

        int padding = Conversions.dpToPx(context, 4);
        int length = Math.max(winners.size(), losers.size());
        for (int i = 0; i < length; i++) {
            TableRow row = new TableRow(context);
            //winner side
            if (i < winners.size()) {
                PlayerRole winner = winners.get(i);
                TextView winnerName = new TextView(context);
                winnerName.setText(winner.getPlayer().getName());
                winnerName.setPadding(padding, padding, padding, padding);
                row.addView(winnerName);
                TextView delta = TextViewUtils.createColoredText(context, winner.getMoney());
                delta.setPadding(padding, padding, padding, padding);
                row.addView(delta);
            } else {
                row.addView(new Space(context));
                row.addView(new Space(context));
            }
            row.addView(new Space(context));
            if (i < losers.size()) {
                PlayerRole loser = losers.get(i);
                TextView loserName = new TextView(context);
                loserName.setText(loser.getPlayer().getName());
                loserName.setPadding(padding, padding, padding, padding);
                row.addView(loserName);
                TextView delta = TextViewUtils.createColoredText(context, loser.getMoney());
                delta.setPadding(padding, padding, padding, padding);
                row.addView(delta);
            } else {
                row.addView(new Space(context));
                row.addView(new Space(context));
            }
            participantTable.addView(row);
        }

        StakeModifier mods = game.getStakeModifier();
        if ((game.getGameType().equals(GameType.WENZ) && mods.getNumberOfLaufende() >= 2) || mods.getNumberOfLaufende() >= 3) {
            CheckBox ck = new CheckBox(context);
            ck.setEnabled(false);
            ck.setChecked(true);
            ck.setText(context.getString(R.string.details_game_laufende, mods.getNumberOfLaufende()));
            properties.addView(ck);
        }
        if (mods.isSchneider()) {
            if (mods.isSchwarz()) {
                CheckBox ck = new CheckBox(context);
                ck.setEnabled(false);
                ck.setChecked(true);
                ck.setText(context.getString(R.string.FillGameResult_schwarz));
                properties.addView(ck);
            } else {
                CheckBox ck = new CheckBox(context);
                ck.setEnabled(false);
                ck.setChecked(true);
                ck.setText(context.getString(R.string.FillGameResult_schneider));
                properties.addView(ck);
            }
        }
        if (mods.isKontra()) {
            if (mods.isRe()) {
                CheckBox ck = new CheckBox(context);
                ck.setEnabled(false);
                ck.setChecked(true);
                ck.setText(context.getString(R.string.FillGameResult_re));
                properties.addView(ck);
            } else {
                CheckBox ck = new CheckBox(context);
                ck.setEnabled(false);
                ck.setChecked(true);
                ck.setText(context.getString(R.string.FillGameResult_kontra));
                properties.addView(ck);
            }
        }
        if (mods.isTout()) {
            if (mods.isSie()) {
                CheckBox ck = new CheckBox(context);
                ck.setEnabled(false);
                ck.setChecked(true);
                ck.setText(context.getString(R.string.FillGameResult_sie));
                properties.addView(ck);
            } else {
                CheckBox ck = new CheckBox(context);
                ck.setEnabled(false);
                ck.setChecked(true);
                ck.setText(context.getString(R.string.FillGameResult_tout));
                properties.addView(ck);
            }
        }

    }
}
