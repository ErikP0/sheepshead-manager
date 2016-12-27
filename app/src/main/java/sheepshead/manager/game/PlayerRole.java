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

package sheepshead.manager.game;


import android.support.annotation.NonNull;

public class PlayerRole {

    private Player player;

    private boolean isCaller;

    private boolean isWinner;

    /**
     * if player lost game, this int is negative
     */
    private int moneyWon = 0;

    public PlayerRole(@NonNull Player associatedPlayer, boolean hasCalled, boolean hasWon) {
        player = associatedPlayer;
        isCaller = hasCalled;
        isWinner = hasWon;
    }

    public
    @NonNull
    Player getPlayer() {
        return player;
    }

    public boolean isCaller() {
        return isCaller;
    }

    public boolean isWinner() {
        return isWinner;
    }

    public int getMoney() {
        return moneyWon;
    }

    public void setMoney(int money) {
        moneyWon = money;
    }

    @Override
    public String toString() {
        return player + " isCaller=" + isCaller + ", isWinner=" + isWinner;
    }
}
