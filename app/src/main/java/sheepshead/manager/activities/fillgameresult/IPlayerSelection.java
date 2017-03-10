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

package sheepshead.manager.activities.fillgameresult;


import android.os.Bundle;

import java.util.Collection;
import java.util.Set;

import sheepshead.manager.game.GameType;
import sheepshead.manager.game.Player;

/**
 * Controller for selecting participating players
 */
interface IPlayerSelection {

    /**
     * Adds the given player to the selection
     *
     * @param player to be added
     */
    void addPlayer(Player player);

    /**
     * Removes the given player from the selection
     *
     * @param player to be removed
     */
    void removePlayer(Player player);

    /**
     * Sets all available players for the base of selection
     *
     * @param availablePlayers Collection of all players participating in the session
     */
    void setAvailablePlayers(Collection<Player> availablePlayers);

    /**
     * @return All players currently selected
     */
    Set<Player> getSelectedPlayers();

    /**
     * Loads the stored selection
     *
     * @param savedInstanceState the bundle where the selection was stored to
     */
    void load(Bundle savedInstanceState);

    /**
     * Stores the current selection into the given bundle
     *
     * @param savedInstanceState the bundle where the selection is stored to
     */
    void store(Bundle savedInstanceState);

    /**
     * Notifies the controller of a changed game type
     *
     * @param newGameType The new game type
     */
    void onGameTypeSelectionChange(GameType newGameType);

    /**
     * Under certain circumstances the controller might have enough knowledge to automatically fill
     * his selection using all available players and the other controller.
     *
     * @param otherSelection Other controller
     */
    void tryAutoFill(IPlayerSelection otherSelection);
}
