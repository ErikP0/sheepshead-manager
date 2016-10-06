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

import java.util.List;

/**
 * Created by Nicolas on 01.10.2016.
 */

public class SingleGameResult {
    /**
     * Wer spielt mit in aktuellem Spiel
     */
    private List<Player> participatoryPlayers;

    private boolean isKontra;

    private boolean isRe;

    /**
     * Es darf immer nur entweder isTout oder isSie gelten
     */
    private boolean isTout;

    /**
     * Es darf immer nur entweder isTout oder isSie gelten
     */
    private boolean isSie;

    private boolean isSchneider;

    private boolean isSchwarz;

    private int numberOfLaufende;

    private GameType gameType;

    private Stake stake;


    /**
     * Bei neuem Spiel: Wer spielt mit, welcher GameType, wer mit wem, wie ging das Spiel aus, welcher Spieler kriegt/verliert wieviel
     */

    public SingleGameResult(List<Player> participatoryPlayers, GameType gameType) {
        this.participatoryPlayers = participatoryPlayers;

        //Vorerst initialisiert, später müssen diese durch die Buttons in der GUI gesetzt werden (setter methoden benutzen)
        this.isSchneider = false;
        this.isSchwarz = false;
        this.isTout = false;
        this.isSie = false;
        this.isKontra = false;
        this.isRe = false;
        this.numberOfLaufende = 0;

        this.gameType = gameType;

    }


    public void setSingleGameResult(){
        int winLoseMultiplier = 1;

        stake = new Stake();
        stake.setKontra(isKontra());
        stake.setRe(isRe());
        stake.setSchneider(isSchneider());
        stake.setSchwarz(isSchwarz());
        stake.setNumberOfLaufende(getNumberOfLaufende());
        stake.setTout(isTout());
        stake.setSie(isSie());

        stake.setStakeValue(gameType);

        for (Player player : participatoryPlayers){
            if (player.isHasWon()){
                winLoseMultiplier = 1;
            }else{
                winLoseMultiplier = -1;
            }

            if (player.isPlayer()){
                player.setPriceToGet(gameType.getTeamMultiplier() * winLoseMultiplier * stake.getStakeValue());
            }else{
                player.setPriceToGet(winLoseMultiplier * stake.getStakeValue());
            }
        }
    }

    //Für Testzwecke
    private void showSingleGameResult(){

    }

    public boolean isSie() {
        return isSie;
    }

    public void setSie(boolean sie) {
        isSie = sie;
    }

    public boolean isSchneider() {
        return isSchneider;
    }

    public void setSchneider(boolean schneider) {
        isSchneider = schneider;
    }

    public boolean isSchwarz() {
        return isSchwarz;
    }

    public void setSchwarz(boolean schwarz) {
        isSchwarz = schwarz;
    }

    public int getNumberOfLaufende() {
        return numberOfLaufende;
    }

    public void setNumberOfLaufende(int numberOfLaufende) {
        this.numberOfLaufende = numberOfLaufende;
    }

    public boolean isKontra() {
        return isKontra;
    }

    public void setKontra(boolean kontra) {
        isKontra = kontra;
    }

    public boolean isRe() {
        return isRe;
    }

    public void setRe(boolean re) {
        isRe = re;
    }

    public boolean isTout() {
        return isTout;
    }

    public void setTout(boolean tout) {
        isTout = tout;
    }
}
