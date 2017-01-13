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

package sheepshead.manager.session;


import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import sheepshead.manager.game.GameType;
import sheepshead.manager.game.Player;
import sheepshead.manager.game.PlayerRole;
import sheepshead.manager.game.SingleGameResult;
import sheepshead.manager.game.StakeModifier;
import sheepshead.manager.serialization.CSVCellContent;
import sheepshead.manager.serialization.CSVRead;
import sheepshead.manager.serialization.SessionDataCorruptedException;

public class InternalSessionReader implements CSVRead {

    private Session session;

    @Override
    public void readHeader(List<String> headerCellContent) throws SessionDataCorruptedException {
        try {
            //all the header contains are the names of the players + the serialized stake
            CSVCellContent.Reader stakeData = new CSVCellContent(headerCellContent.get(headerCellContent.size() - 1), InternalSessionWriter.contentSeparator).getReader();
            session = new Session(headerCellContent.subList(0, headerCellContent.size() - 1), new Stake(stakeData));
        } catch (Exception e) {
            throw new SessionDataCorruptedException("When reading the header (" + headerCellContent + "): ", e);
        }
    }

    @Override
    public void readGame(List<String> rowContent) throws SessionDataCorruptedException {
        Collection<Player> players = session.getPlayers();
        if (rowContent.size() != players.size() + 2) {
            throw new SessionDataCorruptedException("Row size does not match player size + game type + stake modifier (here: " + TextUtils.join(",", rowContent));
        }
        List<PlayerRole> playerRoles = new ArrayList<>(SingleGameResult.PLAYERS_PER_GAME);
        try {
            Iterator<String> it = rowContent.iterator();
            for (Player player : session.getPlayers()) {
                String playerRoleRaw = it.next();
                if (!playerRoleRaw.isEmpty()) {
                    CSVCellContent.Reader roleData = new CSVCellContent(playerRoleRaw, InternalSessionWriter.contentSeparator).getReader();
                    playerRoles.add(new PlayerRole(player, roleData));
                }
            }

            //next is the game type
            GameType gameType = GameType.getGameType(new CSVCellContent(it.next(), InternalSessionWriter.contentSeparator).getReader());
            //and the stake modifier
            StakeModifier modifier = new StakeModifier(new CSVCellContent(it.next(), InternalSessionWriter.contentSeparator).getReader());
            session.addGame(new SingleGameResult(playerRoles, gameType, modifier));
        } catch (Exception e) {
            throw new SessionDataCorruptedException("When reading row (" + rowContent + "): ", e);
        }
    }

    @Override
    public Session buildSession() {
        return session;
    }
}
