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

import android.content.Context;
import android.support.annotation.StringRes;

import java.io.Serializable;

import sheepshead.manager.R;
import sheepshead.manager.serialization.CSVCellContent;
import sheepshead.manager.session.ICSVSerializable;

/**
 * A game type describes a game mode in Schafkopf.
 * Currently available gamemodes are Sauspiel (2vs2), Wenz und Solo (1vs3).
 * A dummy game type LEER is provided for easy representation of a game type not yet selected
 */
public enum GameType implements Serializable, ICSVSerializable {

    SAUSPIEL(1, 3, 8, 2, R.string.GameType_SAUSPIEL),
    WENZ(3, 2, 4, 1, R.string.GameType_WENZ),
    SOLO(3, 3, 8, 1, R.string.GameType_SOLO),
    LEER(1, -1, -1, -1, R.string.GameType_LEER);

    private int teamMultiplier;

    /**
     * The first number where "Laufende" are accounted for(e.g. SAUSPIEL/SOLO 3, WENZ 2)
     */
    private int minLaufende;

    /**
     * The highest number where "Laufende" still count (e.g. SAUSPIEL/SOLO 8, WENZ 4)
     * This is restricted by the amount of high trumps in the game type
     */
    private int maxLaufendeIncl;

    /**
     * The amount of callers that this game type can have
     */
    private int numberOfCallers;

    /**
     * the string resource of the name of this game type
     */
    private @StringRes
    int displayString;

    GameType(int teamMultiplier, int laufendeBegin, int laufendeEndIncl, int numCallers, @StringRes int displaystring) {
        this.teamMultiplier = teamMultiplier;
        minLaufende = laufendeBegin;
        maxLaufendeIncl = laufendeEndIncl;
        numberOfCallers = numCallers;
        displayString = displaystring;
    }

    /**
     * @param data A readonly csv data cell
     * @return the game type encoded in the given csv data cell
     */
    public static GameType getGameType(CSVCellContent.Reader data) {
        int index = data.getInteger();
        return GameType.values()[index];
    }

    /**
     * @return The amount of players on the calling side for this game type
     */
    public int getNumberOfCallers() {
        return numberOfCallers;
    }

    public int getTeamMultiplier() {
        return teamMultiplier;
    }

    /**
     * @return the first number where high trumps count as "Laufende"
     */
    public int getLaufendeBegin() {
        return minLaufende;
    }

    /**
     * @return the last number where high trumps still count as "Laufende"
     */
    public int getLaufendeEnd() {
        return maxLaufendeIncl;
    }

    @Override
    public void toCSVSerializableString(CSVCellContent content) {
        content.put(ordinal());
    }

    /**
     * Returns a translated, human-readable name of this game type
     *
     * @param context the context for translation
     * @return a human-readable string to display
     */
    public String toDisplayString(Context context) {
        return context.getResources().getString(displayString);
    }
}
