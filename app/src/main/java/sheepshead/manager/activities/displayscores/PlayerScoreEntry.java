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


import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import sheepshead.manager.game.Player;
import sheepshead.manager.game.PlayerRole;

/**
 * Data class for the session overview.
 * Associates the player with his balance (at that time) and his role in the latest game
 */
class PlayerScoreEntry {

    private
    @NonNull
    Player player;

    private int balance;
    private
    @Nullable
    PlayerRole role;

    PlayerScoreEntry(@NonNull Player player, @Nullable PlayerRole role) {
        this.player = player;
        balance = player.getSessionMoney();
        this.role = role;
    }

    String getName() {
        return player.getName();
    }

    int getBalance() {
        return balance;
    }

    @Nullable
    PlayerRole getRole() {
        return role;
    }
}
