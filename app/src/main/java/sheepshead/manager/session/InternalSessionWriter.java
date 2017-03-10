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

package sheepshead.manager.session;


import java.util.ArrayList;
import java.util.List;

import sheepshead.manager.game.Player;
import sheepshead.manager.game.PlayerRole;
import sheepshead.manager.game.SingleGameResult;
import sheepshead.manager.serialization.CSVCellContent;
import sheepshead.manager.serialization.SessionCSVWriter;
import sheepshead.manager.serialization.SessionDataCorruptedException;


/**
 * Implementation for the internal load and save format
 */
public class InternalSessionWriter implements SessionCSVWriter.Writer {
    public static final char contentSeparator = '|';


    @Override
    public List<String> writeHeader(Session session) throws SessionDataCorruptedException {
        List<String> cells = new ArrayList<>();
        for (Player player : session.getPlayers()) {
            cells.add(player.getName());
        }
        //next append the stake of the session
        CSVCellContent cellContent = new CSVCellContent(contentSeparator);
        session.getSessionStake().toCSVSerializableString(cellContent);
        cells.add(cellContent.consume());
        return cells;
    }

    @Override
    public List<String> writeGame(Session session, SingleGameResult result) throws SessionDataCorruptedException {
        List<String> list = new ArrayList<>();
        for (Player player : session.getPlayers()) {
            PlayerRole role = result.findRole(player);
            String cellContent = "";
            if (role != null) {
                CSVCellContent contentCollector = new CSVCellContent(contentSeparator);
                role.toCSVSerializableString(contentCollector);
                cellContent = contentCollector.consume();
            }
            list.add(cellContent);
        }
        //now append the game type
        CSVCellContent gameType = new CSVCellContent(contentSeparator);
        result.getGameType().toCSVSerializableString(gameType);
        list.add(gameType.consume());

        //and the modifier
        CSVCellContent modifier = new CSVCellContent(contentSeparator);
        result.getStakeModifier().toCSVSerializableString(modifier);
        list.add(modifier.consume());
        return list;
    }
}
