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

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import sheepshead.manager.game.Player;
import sheepshead.manager.game.PlayerRole;
import sheepshead.manager.game.SingleGameResult;

/**
 * A session holds all participating players, and all single games that have been entered by the user
 */
public class Session {

    /**
     * All players in the session
     */
    private final Collection<Player> players;
    /**
     * The stake of the session
     */
    private final Stake sessionStake;
    /**
     * All saved games in the session
     */
    private LinkedList<SingleGameResult> savedGames;

    /**
     * Creates a new session with the given players and the given stake
     *
     * @param playerNames A list of desired player names
     * @param stake       The stake of the session
     * @throws IllegalArgumentException If less than 4 names are given
     * @throws IllegalArgumentException If the name list contains duplicate names
     */
    public Session(List<String> playerNames, Stake stake) {
        if (playerNames.size() < SingleGameResult.PLAYERS_PER_GAME) {
            throw new IllegalArgumentException("The name list must contain at least " + SingleGameResult.PLAYERS_PER_GAME + " names");
        }
        if (containsDuplicates(playerNames)) {
            throw new IllegalArgumentException("Duplicate player names are not allowed");
        }
        players = Collections.unmodifiableList(createPlayers(playerNames));
        sessionStake = stake;
        savedGames = new LinkedList<>();
    }

    private static List<Player> createPlayers(List<String> playerNames) {
        List<Player> players = new ArrayList<>(playerNames.size());
        for (String name : playerNames) {
            players.add(new Player(name));
        }
        return players;
    }

    private static boolean containsDuplicates(List<String> names) {
        Set<String> set = new HashSet<>();
        for (String name : names) {
            if (!set.add(name.toLowerCase())) {
                //Set#add returns false when the element is already in the set -> a duplicate
                return true;
            }
        }
        return false;
    }

    /**
     * Add the given game result to the session.
     * When the result is added, the earned and lost stakes will be accounted with the
     * current balance of each player
     *
     * @param game The game result to add
     */
    public void addGame(@NonNull SingleGameResult game) {
        savedGames.add(game);
        game.calculate(sessionStake);
        //update players money
        for (PlayerRole role : game.getParticipants()) {
            int delta = role.getMoney();
            Player player = role.getPlayer();
            player.setSessionMoney(player.getSessionMoney() + delta);
            role.setPlayerBalance(player.getSessionMoney());
        }
    }

    public
    @Nullable
    SingleGameResult getLatestResult() {
        if (!savedGames.isEmpty()) {
            return savedGames.getLast();
        }
        return null;
    }

    public Stake getSessionStake() {
        return sessionStake;
    }

    /**
     * @return The amount of game results stored in the session
     */
    public int getGameAmount() {
        return savedGames.size();
    }

    /**
     * @return all players in the session
     */
    public Collection<Player> getPlayers() {
        return players;
    }

    public Iterator<SingleGameResult> getLatestFirstIterator() {
        return savedGames.descendingIterator();
    }

    /**
     * @return An iterator ranging from the first (oldest) game result to the last (latest) game result
     */
    public Iterator<SingleGameResult> iterator() {
        return savedGames.iterator();
    }

    public String printInfo() {
        StringBuilder builder = new StringBuilder();
        builder.append("Aktueller Kontostand:\n");
        for (Player p : players) {
            builder.append(p).append('\n');
        }
        builder.append("Letztes Spiel:\n");
        SingleGameResult lastGame = savedGames.get(savedGames.size() - 1);
        for (PlayerRole pr : lastGame.getParticipants()) {
            builder.append(pr.getPlayer().getName() + " (" + pr.getMoney() + ")\n");
        }
        return builder.toString();
    }

    public void removeLatestGameResult() {
        if (!savedGames.isEmpty()) {
            SingleGameResult remove = savedGames.removeLast();
            for (Player player : players) {
                PlayerRole role = remove.findRole(player);
                if (role != null) {
                    player.setSessionMoney(player.getSessionMoney() - role.getMoney());
                }
            }
        }
    }

}
