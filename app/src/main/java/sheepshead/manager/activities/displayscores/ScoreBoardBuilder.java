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


import android.app.Activity;
import android.content.Context;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.Collection;
import java.util.Iterator;

import sheepshead.manager.R;
import sheepshead.manager.game.Player;
import sheepshead.manager.game.PlayerRole;
import sheepshead.manager.game.SingleGameResult;
import sheepshead.manager.session.Session;
import sheepshead.manager.uicontrolutils.DynamicSizeTableBuilder;
import sheepshead.manager.utils.Consumer;

class ScoreBoardBuilder {

    private static final int CELL_WIDTH = 150;
    private static final int SMALL_CELL_WIDTH = CELL_WIDTH / 3;
    private static final int FONT_SIZE_NORMAL_SP = 16;
    private static final int FONT_SIZE_HEADER_SP = 20;

    @NonNull
    private final TableLayout headerContainer;

    @NonNull
    private final TableLayout table;

    @NonNull
    private final Activity activity;

    private final DynamicSizeTableBuilder builder;

    private final String playerNotParticipating;

    ScoreBoardBuilder(@NonNull Activity a, @NonNull TableLayout header, @NonNull TableLayout existingTable) {
        activity = a;
        headerContainer = header;
        table = existingTable;
        playerNotParticipating = activity.getString(R.string.DisplayScores_text_player_not_participating);
        builder = new DynamicSizeTableBuilder(new TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT), new BoardBackgroundDrawer(), new BoardBackgroundDrawer());
    }

    void addHeader(Collection<Player> players) {
        builder.enableHeader();
        //insert "Nr." header cell
        String nr = activity.getString(R.string.DisplayScores_text_number_of_game);
        builder.putHeaderCell(new FixedSizeTextViewBuilder(nr, SMALL_CELL_WIDTH, FONT_SIZE_HEADER_SP));
        for (Player player : players) {
            String description = player.getName() + "\n(" + player.getSessionMoney() + ")";
            builder.putHeaderCell(new FixedSizeTextViewBuilder(description, CELL_WIDTH, FONT_SIZE_HEADER_SP));
        }
    }

    void addBody(@NonNull Session session, Collection<Player> players) {
        int number = session.getGameAmount();
        Iterator<SingleGameResult> revIt = session.getLatestFirstIterator();
        while (revIt.hasNext()) {
            SingleGameResult result = revIt.next();
            addGame(number, result, players);
            number--;
        }
    }

    void build() {
        builder.build(activity, table, headerContainer);
    }

    private void addGame(int number, SingleGameResult result, Collection<Player> players) {
        builder.startRow();
        DynamicSizeTableBuilder.ITableCellBuilder nr = new FixedSizeTextViewBuilder(Integer.toString(number), SMALL_CELL_WIDTH, FONT_SIZE_NORMAL_SP);
        builder.putRowCell(nr);
        for (Player p : players) {
            PlayerRole role = result.findRole(p);
            if (role == null) {
                //player did not participate in this game
                //cell is empty
                builder.putRowCell(new FixedSizeTextViewBuilder(playerNotParticipating, CELL_WIDTH, FONT_SIZE_NORMAL_SP));
            } else {
                DynamicSizeTableBuilder.ITableCellBuilder playerGameResult = new ScoreBuilder(role.getPlayerBalance(), role.getMoney());
                builder.putRowCell(playerGameResult);
            }
        }
    }

    private static class BoardBackgroundDrawer implements Consumer<View> {
        @DrawableRes
        private static final int drawable = R.drawable.display_scores_table_bg;

        @Override
        public void accept(View view) {
            view.setBackgroundResource(drawable);
        }
    }

    private static class FixedSizeTextViewBuilder implements DynamicSizeTableBuilder.ITableCellBuilder {
        private CharSequence text;
        private int width;
        private int fontsize;

        FixedSizeTextViewBuilder(CharSequence s, int cellwidth, int size) {
            text = s;
            width = cellwidth;
            fontsize = size;
        }

        @Override
        public View build(Context context) {
            TextView t = new TextView(context);
            t.setText(text);
            t.setSingleLine(false);
            t.setWidth(width);
            t.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontsize);
            return t;
        }
    }

    private static class ScoreBuilder implements DynamicSizeTableBuilder.ITableCellBuilder {
        @ColorRes
        private static final int COLOR_NEGATIVE = R.color.scoreboard_negative_text;
        @ColorRes
        private static final int COLOR_POSITIVE = R.color.scoreboard_positive_text;
        private int balance;
        private int delta;

        ScoreBuilder(int balance, int delta) {
            this.balance = balance;
            this.delta = delta;
        }

        @Override
        public View build(Context context) {
            String balanceString = Integer.toString(balance);
            String deltaString = "(" + delta + ")";
            SpannableString text = new SpannableString(balanceString + " " + deltaString);
            text.setSpan(new ForegroundColorSpan(getColor(context, balance)), 0, balanceString.length(), 0);
            text.setSpan(new ForegroundColorSpan(getColor(context, delta)), balanceString.length() + 1, balanceString.length() + 1 + deltaString.length(), 0);

            TextView textView = new TextView(context);
            textView.setText(text, TextView.BufferType.SPANNABLE);
            textView.setWidth(CELL_WIDTH);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, FONT_SIZE_NORMAL_SP);
            return textView;
        }

        private int getColor(Context c, int i) {
            if (i < 0) {
                return ContextCompat.getColor(c, COLOR_NEGATIVE);
            } else {
                return ContextCompat.getColor(c, COLOR_POSITIVE);
            }
        }
    }
}
