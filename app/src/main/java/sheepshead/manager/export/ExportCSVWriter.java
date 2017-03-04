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

package sheepshead.manager.export;


import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

import sheepshead.manager.R;
import sheepshead.manager.game.Player;
import sheepshead.manager.game.PlayerRole;
import sheepshead.manager.game.SingleGameResult;
import sheepshead.manager.serialization.CSVWrite;
import sheepshead.manager.serialization.SessionDataCorruptedException;
import sheepshead.manager.session.Session;

public class ExportCSVWriter implements CSVWrite {

    private Activity activity;
    private int counter;

    public ExportCSVWriter(Activity activity) {
        this.activity = activity;
        counter = 1;
    }

    @Override
    public List<String> writeHeader(Session session) throws SessionDataCorruptedException {
        String deltaDescription = activity.getString(R.string.DialogSelectCSVRule_writer_delta_header);
        List<String> header = new ArrayList<>();
        header.add(activity.getString(R.string.DialogSelectCSVRule_writer_nr_header));
        for (Player player : session.getPlayers()) {
            header.add(player.getName());
            header.add(deltaDescription);
        }
        return header;
    }

    @Override
    public List<String> writeGame(Session session, SingleGameResult result) throws SessionDataCorruptedException {
        List<String> line = new ArrayList<>();
        line.add(Integer.toString(counter));
        for (Player player : session.getPlayers()) {
            PlayerRole role = result.findRole(player);
            if (role != null) {
                line.add(Integer.toString(role.getPlayerBalance()));
                line.add(Integer.toString(role.getMoney()));
            } else {
                line.add("");
                line.add("");
            }
        }
        counter++;
        return line;
    }
}
