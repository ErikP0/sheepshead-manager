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

package sheepshead.manager.singleGameRequirements;

import java.io.Serializable;

/**
 * Created by Nicolas on 01.10.2016.
 */

//Serializable is used in FillGameResult
public enum GameType implements Serializable {

    SAUSPIEL(1, 3, 8, 2),
    WENZ(3, 2, 4, 1),
    SOLO(3, 3, 8, 1),
    LEER(1, -1, -1, -1);

    private int teamMultiplier;

    /**
     * The first number where "Laufende" count (e.g. SAUSPIEL/SOLO 3, WENZ 2)
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

    GameType(int teamMultiplier, int laufendeBegin, int laufendeEndIncl, int numCallers) {
        this.teamMultiplier = teamMultiplier;
        minLaufende = laufendeBegin;
        maxLaufendeIncl = laufendeEndIncl;
        numberOfCallers = numCallers;
    }

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

}
