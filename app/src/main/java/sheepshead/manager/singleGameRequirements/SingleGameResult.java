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
import java.util.List;

/**
 * Created by Nicolas on 01.10.2016.
 */

public class SingleGameResult {
    /**
     * Wer spielt mit in aktuellem Spiel
     */
    private ArrayList<Player> playerList;

    private GameType gameType;

    private StakeModifier stakeModifier;

    private ArrayList<Integer> singleGameMoney;


    /**
     * Bei neuem Spiel: Wer spielt mit, welcher GameType, wer mit wem, wie ging das Spiel aus, welcher Spieler kriegt/verliert wieviel
     */

    public SingleGameResult(ArrayList<Player> playerList, GameType gameType, StakeModifier stakeModifier) {

        singleGameMoney = new ArrayList<Integer>(playerList.size() * 2); //Hier ist das Geld der Spieler, das sie in der Runde zahlen müssen und insgesamt besitzen/zahlen müssen aufgeführt. 1. Wert gesamt Geld 2. Geld in dem Spiel

        this.playerList = playerList;

        this.gameType = gameType;

        this.stakeModifier = stakeModifier;

    }

    public void setSingleGameResult(){
        int winLoseMultiplier = 1;

        final int stakeValue = calculateStakeValue();

        for (Player player : playerList){
            if (player.isParticipant()) {
                if (player.hasWon()){
                    winLoseMultiplier = 1;
                } else {
                    winLoseMultiplier = -1;
                }

                if (player.isCaller()) {
                    player.setPriceToGetInSingleGame(gameType.getTeamMultiplier() * winLoseMultiplier * stakeValue);
                } else {
                    player.setPriceToGetInSingleGame(winLoseMultiplier * stakeValue);
                }

                player.setPriceToGetInSession(player.getPriceToGetInSession() + player.getPriceToGetInSingleGame());

            } else {
                player.setPriceToGetInSingleGame(0);
            }

            singleGameMoney.add(player.getPriceToGetInSession());
            singleGameMoney.add(player.getPriceToGetInSingleGame());

        }
    }

    private int calculateStakeValue() {

        int aktStakeValue = gameType.getNormalPriceForOnePlayer();

        if(stakeModifier.getNumberOfLaufende() >= 3 && (gameType.equals(GameType.SOLO) || gameType.equals(GameType.SAUSPIEL))
                || stakeModifier.getNumberOfLaufende() >= 2 && gameType.equals(GameType.WENZ)){
            aktStakeValue = aktStakeValue + stakeModifier.getNumberOfLaufende() * SheapsheadConstants.LAUFENDENTARIF;
        }

        //Laut Wiki wird bei Tout und Sie kein Schneider oder Schwarz gerechnet
        if (stakeModifier.isTout()){
            aktStakeValue = aktStakeValue * 2;
        } else if(stakeModifier.isSie()){
            aktStakeValue = aktStakeValue * 4;
        } else { //Weder Tout oder Sie werden gespielt,
            if(stakeModifier.isSchneider()){
                aktStakeValue = aktStakeValue + SheapsheadConstants.GRUNDTARIF;
            }
            if (stakeModifier.isSchwarz()){
                aktStakeValue = aktStakeValue + SheapsheadConstants.GRUNDTARIF;
            }
        }

        if (stakeModifier.isKontra() && !stakeModifier.isRe()){
            aktStakeValue = aktStakeValue * 2;
        }else if (stakeModifier.isRe()){
            aktStakeValue = aktStakeValue * 4;
        }
        return aktStakeValue;

    }

    public ArrayList<Integer> getSingleGameMoney(){
        return singleGameMoney;
    }

    public void showSingleGameLine(){
        String string;
        String leerzeichen = "";
        for (int i = 0; i < singleGameMoney.size(); i++){
            string = singleGameMoney.get(i) + "";
            for (int j = 0; j < (6 - string.length()); j++){
                leerzeichen += " ";
            }
            System.out.print(leerzeichen + string + " ");
            leerzeichen = "";
        }
    }

}
