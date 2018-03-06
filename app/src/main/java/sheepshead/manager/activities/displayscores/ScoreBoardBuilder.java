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


import android.app.Activity;
import android.content.Context;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;

import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;

import sheepshead.manager.R;
import sheepshead.manager.game.Player;
import sheepshead.manager.game.PlayerRole;
import sheepshead.manager.game.SingleGameResult;
import sheepshead.manager.session.Session;
import sheepshead.manager.uicontrolutils.table.BgDrawables;
import sheepshead.manager.uicontrolutils.table.DynamicSizeTableBuilder;
import sheepshead.manager.uicontrolutils.table.ITableCellBuilder;

/**
 * Convenience builder class for populating the score board table with the desired content.
 * Note: The content-methods (like {@link #addHeader(Collection)}, {@link #addBody(Session, Collection)})
 * can be called in any order, but before calling {@link #build()}.
 * The table uses a fixed cell width and a fixed font size.
 * The score board consists of a header bar and the table body. The header is a separate table with
 * the same widths in order to make the header fixed while scrolling.
 * The header: Nr. | PlayerName (balance) | PlayerName2 (balance2) | ...
 * The body:   42  | 120 (+50)            | ausgesetzt             | ...
 */
class ScoreBoardBuilder {

    /**
     * Width for cells
     */
    private static final int CELL_WIDTH = 300;

    /**
     * Height for cells
     */
    private static final int CELL_HEIGHT = 200;

    /**
     * Font size for text in the table body
     */
    private static final int FONT_SIZE_NORMAL_SP = 16;
    /**
     * Font size for text in the header
     */
    private static final int FONT_SIZE_HEADER_SP = 20;

    private static final int CELL_PADDING = 10;

    /**
     * The view used as container for dynamic_table
     */
    @NonNull
    private final View tableContainer;

    @NonNull
    private final Activity activity;

    /**
     * A internal builder for building the table
     */
    private final DynamicSizeTableBuilder builder;

    /**
     * The string description displayed when a player did not participate in that game
     */
    private final String playerNotParticipating;

    /**
     * Creates the builder
     *
     * @param a              The current activity
     * @param tableContainer The view used as container for dynamic_table
     */
    ScoreBoardBuilder(@NonNull Activity a, @NonNull View tableContainer) {
        activity = a;
        this.tableContainer = tableContainer;
        playerNotParticipating = activity.getString(R.string.DisplayScores_text_player_not_participating);
        BgDrawables drawables = new BgDrawables(R.drawable.display_scores_table_bg, R.drawable.display_scores_table_bg, R.drawable.display_scores_table_bg_arrow_up, R.drawable.display_scores_table_bg_arrow_down);
        builder = new DynamicSizeTableBuilder(drawables, null, null,
                new ColumnOrder());
    }

    /**
     * Sets the header for this score board.
     * This creates the following header: Nr. | PlayerName (balance) | PlayerName2 (balance2) | ...
     *
     * @param players The players of the session
     */
    void addHeader(Collection<Player> players) {
        builder.enableHeader(true);
        builder.fixFirstColumn();
        //insert "Nr." header cell
        String nr = activity.getString(R.string.DisplayScores_text_number_of_game);
        builder.putHeaderCell(new FixedSizeTextViewBuilder(nr, CELL_WIDTH / 2, CELL_HEIGHT, FONT_SIZE_HEADER_SP, CELL_PADDING));
        for (Player player : players) {
            String description = player.getName() + " (" + player.getSessionMoney() + ")";
            builder.putHeaderCell(new FixedSizeTextViewBuilder(description, CELL_WIDTH, CELL_HEIGHT, FONT_SIZE_HEADER_SP, CELL_PADDING));
        }
    }

    /**
     * Sets the body for this score board
     *
     * @param session The current session to get game results from
     * @param players The players of the session
     */
    void addBody(@NonNull Session session, Collection<Player> players) {
        int number = session.getGameAmount();
        Iterator<SingleGameResult> revIt = session.getLatestFirstIterator();
        while (revIt.hasNext()) {
            SingleGameResult result = revIt.next();
            addGame(number, result, players);
            number--;
        }
    }

    /**
     * Generates and adds the appropriate child views to the matching header/table layouts
     */
    void build() {
        builder.populate(activity, tableContainer);
    }

    private void addGame(int number, SingleGameResult result, Collection<Player> players) {
        builder.startRow();
        ITableCellBuilder nr = new FixedSizeTextViewBuilder(Integer.toString(number), CELL_WIDTH / 2, CELL_HEIGHT, FONT_SIZE_NORMAL_SP, CELL_PADDING);
        builder.putRowCell(nr);
        for (Player p : players) {
            PlayerRole role = result.findRole(p);
            if (role == null) {
                //player did not participate in this game
                //cell is empty
                builder.putRowCell(new FixedSizeTextViewBuilder(playerNotParticipating, CELL_WIDTH, CELL_HEIGHT, FONT_SIZE_NORMAL_SP, CELL_PADDING));
            } else {
                ITableCellBuilder playerGameResult = new ScoreBuilder(role.getPlayerBalance(), role.getMoney(), CELL_WIDTH, CELL_HEIGHT, CELL_PADDING);
                builder.putRowCell(playerGameResult);
            }
        }
    }

    /**
     * Builder class for a TextView with a fixed width and font size
     */
    private static class FixedSizeTextViewBuilder implements ITableCellBuilder, Comparable<FixedSizeTextViewBuilder> {
        private String text;
        private int width;
        private int height;
        private int fontsize;
        private int padding;

        FixedSizeTextViewBuilder(String s, int cellwidth, int cellheight, int fontsize, int padding) {
            text = s;
            width = cellwidth;
            height = cellheight;
            this.fontsize = fontsize;
            this.padding = padding;
        }

        @Override
        public View build(Context context) {
            TextView t = new TextView(context);
            t.setText(text);
            t.setSingleLine(false);
            t.setWidth(width);
            t.setHeight(height);
            t.setPadding(padding, padding, padding, padding);
            t.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontsize);
            return t;
        }

        @Override
        public int compareTo(@NonNull FixedSizeTextViewBuilder o) {
            //tries to compare the text as numbers, if that fails, the text is compared alphabetically
            try {
                int nr1 = Integer.parseInt(text);
                int nr2 = Integer.parseInt(o.text);
                return Integer.compare(nr1, nr2);
            } catch (NumberFormatException ignored) {

            }
            return text.compareTo(o.text);
        }
    }

    /**
     * Builder for a single table body cell.
     * It consists of a single TextView that contains the balance of the player (at the time the game
     * happened) and his earnings/losses. The values are color annotated, green for a non-negative value,
     * red for a negative value
     */
    private static class ScoreBuilder implements ITableCellBuilder, Comparable<ScoreBuilder> {
        @ColorRes
        private static final int COLOR_NEGATIVE = R.color.scoreboard_negative_text;
        @ColorRes
        private static final int COLOR_POSITIVE = R.color.scoreboard_positive_text;
        private int balance;
        private int delta;
        private int width;
        private int height;
        private int padding;

        ScoreBuilder(int balance, int delta, int cellwidth, int cellheight, int padding) {
            this.balance = balance;
            this.delta = delta;
            width = cellwidth;
            height = cellheight;
            this.padding = padding;
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
            textView.setWidth(width);
            textView.setHeight(height);
            textView.setPadding(padding, padding, padding, padding);
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

        @Override
        public int compareTo(@NonNull ScoreBuilder o) {
            // compares by delta
            return Integer.compare(delta, o.delta);
        }
    }

    /**
     * Comparator that can compare the different cell types ({@link ScoreBuilder}, {@link FixedSizeTextViewBuilder}).
     * If both {@link ITableCellBuilder} are of same type, then the comparison method of this type is called.
     * If not, the comparison order is as follows: {@linkplain ScoreBuilder} < {@linkplain FixedSizeTextViewBuilder} < any type
     */
    private static class ColumnOrder implements Comparator<ITableCellBuilder> {

        @Override
        public int compare(ITableCellBuilder o1, ITableCellBuilder o2) {
            boolean scoreBuilder1 = o1 instanceof ScoreBuilder;
            boolean scoreBuilder2 = o2 instanceof ScoreBuilder;
            boolean text1 = o1 instanceof FixedSizeTextViewBuilder;
            boolean text2 = o2 instanceof FixedSizeTextViewBuilder;
            if (scoreBuilder1 && scoreBuilder2) {
                //both are of type ScoreBuilder
                return ((ScoreBuilder) o1).compareTo((ScoreBuilder) o2);
            } else if (scoreBuilder1) {
                return -1;
            } else if (scoreBuilder2) {
                return 1;
            } else if (text1 && text2) {
                //both are of type FixedSizeTextViewBuilder
                return ((FixedSizeTextViewBuilder) o1).compareTo((FixedSizeTextViewBuilder) o2);
            } else if (text1) {
                return -1;
            } else if (text2) {
                return 1;
            }
            return 0;
        }
    }
}
