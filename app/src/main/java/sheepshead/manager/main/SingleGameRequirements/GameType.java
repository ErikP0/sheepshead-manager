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

package sheepshead.manager.main.SingleGameRequirements;

/**
 * Created by Nicolas on 01.10.2016.
 */

public enum GameType {

    SAUSPIEL(ConstantsForSheepshead.GRUNDTARIF, 1),
    WENZ(ConstantsForSheepshead.SOLOTARIF, 3),
    SOLO(ConstantsForSheepshead.SOLOTARIF, 3),
    LEER(0, 1); //Wird aktuell nicht gebraucht, könnte aber evtl. mal nützlich sein

    private int normalPriceForOnePlayer;

    private int teamMultiplier;

    GameType(){

    }

    GameType(int normalPriceForOnePlayer, int teamMultiplier){
        this.normalPriceForOnePlayer = normalPriceForOnePlayer;
        this.teamMultiplier = teamMultiplier;
    }


    public int getNormalPriceForOnePlayer() {
        return normalPriceForOnePlayer;
    }

    public int getTeamMultiplier() {
        return teamMultiplier;
    }

}
