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
import android.support.annotation.NonNull;
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

class ScoreBoardBuilder {

    private static final int CELL_WIDTH = 150;

    @NonNull
    private final TableLayout headerContainer;

    @NonNull
    private final TableLayout table;

    @NonNull
    private final Activity activity;

    private final DynamicSizeTableBuilder builder;

    ScoreBoardBuilder(@NonNull Activity a, @NonNull TableLayout header, @NonNull TableLayout existingTable) {
        activity = a;
        headerContainer = header;
        table = existingTable;
        builder = new DynamicSizeTableBuilder(new TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
    }

    void addHeader(Collection<Player> players) {
        builder.enableHeader();
        //insert "Nr." header cell
        String nr = activity.getString(R.string.DisplayScores_text_number_of_game);
        builder.putHeaderCell(new DynamicSizeTableBuilder.TextViewCellBuilder(nr));
        for (Player player : players) {
            String description = player.getName() + " (" + player.getSessionMoney() + ")";
            builder.putHeaderCell(new FixedSizeTextViewBuilder(description));
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
        DynamicSizeTableBuilder.ITableCellBuilder nr = new FixedSizeTextViewBuilder(Integer.toString(number));
        builder.putRowCell(nr);
        for (Player p : players) {
            PlayerRole role = result.findRole(p);
            if (role == null) {
                //player did not participate in this game
                //cell is empty
                builder.putRowCell(new DynamicSizeTableBuilder.EmptyCellBuilder(CELL_WIDTH));
            } else {
                String text = role.getPlayerBalance() + " (" + role.getMoney() + ")";
                DynamicSizeTableBuilder.ITableCellBuilder playerGameResult = new FixedSizeTextViewBuilder(text);
                builder.putRowCell(playerGameResult);
            }
        }
    }

    private static class FixedSizeTextViewBuilder implements DynamicSizeTableBuilder.ITableCellBuilder {
        private CharSequence text;

        public FixedSizeTextViewBuilder(CharSequence s) {
            text = s;
        }

        @Override
        public View build(Context context) {
            TextView t = new TextView(context);
            t.setText(text);
            t.setSingleLine(false);
            t.setWidth(CELL_WIDTH);
            return t;
        }
    }
}
