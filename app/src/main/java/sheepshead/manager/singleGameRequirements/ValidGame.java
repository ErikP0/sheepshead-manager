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

import java.util.ArrayList;

/**
 * Created by Nicolas on 08.10.2016.
 */

public class ValidGame {


    public ValidGame(ArrayList<Player> players, GameType gameType, StakeModifier stakeModifier){

    }

    /**
     * Hier soll überprüft werden, ob die Daten für ein Spiel für sich konsistent sind. D.h. dass es z.B. nur 1Caller geben darf bei einem Solo etc.
     * @param players Die Spieler, die in einem einzelnen Spiel mitspielen
     * @param gameType Welcher Gametype ist ausgewählt
     * @param stakeModifier Die Spielparameter
     * @return ist das Spiel valide
     */
    public static boolean isValidGame(ArrayList<Player> players, GameType gameType, StakeModifier stakeModifier){
        int numberOfParticipants = 0;
        ArrayList<Player> callers = new ArrayList<Player>();
        ArrayList<Player> winners = new ArrayList<Player>();

        if(gameType == GameType.LEER){ //Es wird nichts geändert. Dieser Gametype existiert für Testzwecke
            return true;
        }

        for (Player player : players){
            if(player.isParticipant()){

                numberOfParticipants++;

                if (player.isCaller()){
                    callers.add(player);
                }
                if(player.hasWon()){
                    winners.add(player);
                }
            }
        }

        if (numberOfParticipants != 4){
            return false;
        }

        if (gameType == GameType.SAUSPIEL){
            if (!(callers.size() == 2 && winners.size() == 2)){
                return false;
            }
            if(stakeModifier.isTout() || stakeModifier.isSie()){
                return false;
            }
        }else if (gameType == GameType.SOLO || gameType == GameType.WENZ){
            if (!(callers.size() == 1 && (winners.size() == 1 || winners.size() == 3))){
                return false;
            }
            if (stakeModifier.isSie() && gameType == GameType.WENZ){
                return false;
            }
        }

        if (stakeModifier.isRe() && !stakeModifier.isKontra()){
            return false;
        }

        for (Player caller : callers){
            for (Player winner : winners){
                if (winners.size() == 1 && caller != winner){ //falscher Zustand, wenn es einen Gewinner gibt und dieser nicht auch zugleich caller ist (nur bei Solo oder Wenz)
                    return false;
                }else if(winners.size() == 3 && caller == winner){ //falscher Zustand, wenn es 3 Gewinner gibt und der caller sich unter diesen Befindet (nur bei Solo oder Wenz)
                    return false;
                }else if(winners.size() == 2 && !(winners.equals(callers) || (!winners.contains(caller) && !callers.contains(winner)))){ //im Sauspiel //Kompliziert aber sollte stimmen ;) zeigt sich im Test
                    return false;
                }
            }
        }

        return true;
    }

    //wird das benötigt, da ja obige Methode eigentlich die Richtigkeit validiert???
    public static boolean isValidMoneySum(ArrayList<Player> players){
        int singleGameMoneySum = 0;
        int sessionMoneySum = 0;

        for (Player player : players){
            singleGameMoneySum += player.getPriceToGetInSingleGame();
            sessionMoneySum += player.getPriceToGetInSession();
        }

        if (!(singleGameMoneySum == 0 && sessionMoneySum == 0)){
            return false;
        }
        return true;
    }
}
