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


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sheepshead.manager.game.GameType;
import sheepshead.manager.game.Player;
import sheepshead.manager.game.PlayerRole;
import sheepshead.manager.game.SingleGameResult;
import sheepshead.manager.game.StakeModifier;

public class DummySession extends Session {

    private static final String[] names = {"Arian", "Simon", "Clemens", "Nico", "Erik"};

    private Map<String, Player> players;

    public DummySession() {
        super(Arrays.asList(names), new Stake(10, 50, 10));
        players = new HashMap<>();
        for (Player p : getPlayers()) {
            players.put(p.getName(), p);
        }
        addGames();
    }

    private void addGames() {
        addGame(createGame(createMod(0, false), GameType.SAUSPIEL, "Arian"));
        addGame(createGame(createMod(0, false), GameType.SAUSPIEL, "Simon"));
        addGame(createGame(createMod(0, false), GameType.SAUSPIEL, "Clemens"));
        addGame(createGame(createMod(0, false), GameType.SAUSPIEL, "Nico"));
        addGame(createGame(createMod(0, false), GameType.SAUSPIEL, "Erik"));
        addGame(createGame(createMod(3, true), GameType.SOLO, "Arian"));
        addGame(createGame(createMod(3, true), GameType.SOLO, "Simon"));
        addGame(createGame(createMod(3, true), GameType.SOLO, "Clemens"));
        addGame(createGame(createMod(3, true), GameType.SOLO, "Nico"));
        addGame(createGame(createMod(3, true), GameType.SOLO, "Erik"));
        addGame(createGame(createMod(0, false), GameType.SAUSPIEL, "Arian"));
        addGame(createGame(createMod(0, false), GameType.SAUSPIEL, "Simon"));
        addGame(createGame(createMod(0, false), GameType.SAUSPIEL, "Clemens"));
        addGame(createGame(createMod(0, false), GameType.SAUSPIEL, "Nico"));
        addGame(createGame(createMod(0, false), GameType.SAUSPIEL, "Erik"));
        addGame(createGame(createMod(3, true), GameType.SOLO, "Arian"));
        addGame(createGame(createMod(3, true), GameType.SOLO, "Simon"));
        addGame(createGame(createMod(3, true), GameType.SOLO, "Clemens"));
        addGame(createGame(createMod(3, true), GameType.SOLO, "Nico"));
        addGame(createGame(createMod(3, true), GameType.SOLO, "Erik"));
        addGame(createGame(createMod(0, false), GameType.SAUSPIEL, "Arian"));
        addGame(createGame(createMod(0, false), GameType.SAUSPIEL, "Simon"));
        addGame(createGame(createMod(0, false), GameType.SAUSPIEL, "Clemens"));
        addGame(createGame(createMod(0, false), GameType.SAUSPIEL, "Nico"));
        addGame(createGame(createMod(0, false), GameType.SAUSPIEL, "Erik"));
        addGame(createGame(createMod(3, true), GameType.SOLO, "Arian"));
        addGame(createGame(createMod(3, true), GameType.SOLO, "Simon"));
        addGame(createGame(createMod(3, true), GameType.SOLO, "Clemens"));
        addGame(createGame(createMod(3, true), GameType.SOLO, "Nico"));
        addGame(createGame(createMod(3, true), GameType.SOLO, "Erik"));
        addGame(createGame(createMod(0, false), GameType.SAUSPIEL, "Arian"));
        addGame(createGame(createMod(0, false), GameType.SAUSPIEL, "Simon"));
        addGame(createGame(createMod(0, false), GameType.SAUSPIEL, "Clemens"));
        addGame(createGame(createMod(0, false), GameType.SAUSPIEL, "Nico"));
        addGame(createGame(createMod(0, false), GameType.SAUSPIEL, "Erik"));
        addGame(createGame(createMod(3, true), GameType.SOLO, "Arian"));
        addGame(createGame(createMod(3, true), GameType.SOLO, "Simon"));
        addGame(createGame(createMod(3, true), GameType.SOLO, "Clemens"));
        addGame(createGame(createMod(3, true), GameType.SOLO, "Nico"));
        addGame(createGame(createMod(3, true), GameType.SOLO, "Erik"));
    }

    private StakeModifier createMod(int laufende, boolean schneider) {
        StakeModifier mod = new StakeModifier();
        mod.setSchneider(schneider);
        mod.setNumberOfLaufende(laufende);
        return mod;
    }

    private SingleGameResult createGame(StakeModifier mod, GameType type, String firstPlayer) {
        int index = find(firstPlayer);
        List<PlayerRole> roles = new ArrayList<>();
        for (int i = 0; i < SingleGameResult.PLAYERS_PER_GAME; i++) {
            Player player = players.get(names[index]);
            index = (index + 1) % names.length;
            boolean callerWon = i < type.getNumberOfCallers();
            roles.add(new PlayerRole(player, callerWon, callerWon));
        }
        return new SingleGameResult(roles, type, mod);
    }

    private int find(String name) {
        for (int i = 0; i < names.length; i++) {
            if (name.equals(names[i])) {
                return i;
            }
        }
        return 0;
    }
}
