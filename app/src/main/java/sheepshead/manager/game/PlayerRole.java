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

package sheepshead.manager.game;


import android.support.annotation.NonNull;

import sheepshead.manager.serialization.CSVCellContent;
import sheepshead.manager.session.ICSVSerializable;

/**
 * A PlayerRole represents a player in a specific single game.
 * So a player role is connected to the session persistent player representation (class Player) but
 * it contains game specific members like being on the calling side or having won the game.
 */
public class PlayerRole implements ICSVSerializable {

    /**
     * The connected session persistent player
     */
    private
    @NonNull
    Player player;

    /**
     * True if this role is on the caller side, false if on the non-caller side
     */
    private boolean isCaller;

    /**
     * True if this role won the game; false if it lost
     */
    private boolean isWinner;

    /**
     * The won/lost stake of the game for this role
     * if player lost game, this int is negative
     */
    private int moneyWon = 0;

    private int balance = 0;

    /**
     * Creates a player role
     *
     * @param associatedPlayer the connected session persistent player
     * @param hasCalled        true if this role is on the calling side; otherwise false
     * @param hasWon           true if this role won the game; otherwise false
     */
    public PlayerRole(@NonNull Player associatedPlayer, boolean hasCalled, boolean hasWon) {
        player = associatedPlayer;
        isCaller = hasCalled;
        isWinner = hasWon;
    }

    /**
     * Creates a player role
     *
     * @param associatedPlayer the connected session persistent player
     * @param data             A readonly csv data cell encoding a player role
     */
    public PlayerRole(@NonNull Player associatedPlayer, CSVCellContent.Reader data) {
        this(associatedPlayer, data.getBoolean(), data.getBoolean());
        setMoney(data.getInteger());
        setPlayerBalance(data.getInteger());
    }

    @Override
    public void toCSVSerializableString(CSVCellContent content) {
        //watch out! the order is important!
        content.put(isCaller);
        content.put(isWinner);
        content.put(moneyWon);
        content.put(balance);
    }

    /**
     * @return The connected session persistent player
     */
    public
    @NonNull
    Player getPlayer() {
        return player;
    }

    /**
     * @return True if this role is on the calling side; false if on the non-calling side
     */
    public boolean isCaller() {
        return isCaller;
    }

    /**
     * @return True if the role won the game; false if it lost
     */
    public boolean isWinner() {
        return isWinner;
    }

    /**
     * A negative amount denotes a loss of the game
     *
     * @return The stake for this game and role
     */
    public int getMoney() {
        return moneyWon;
    }

    /**
     * Sets the earned or lost stake for this player role
     *
     * @param money Earned stake is positive, lost stake is negative
     */
    public void setMoney(int money) {
        moneyWon = money;
    }

    public int getPlayerBalance() {
        return balance;
    }

    public void setPlayerBalance(int newBalance) {
        balance = newBalance;
    }

    @Override
    public String toString() {
        return player + " isCaller=" + isCaller + ", isWinner=" + isWinner;
    }
}
