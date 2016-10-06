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

public class Stake {

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

    private int stakeValue;


    public Stake(){
        this.isKontra = false;
        this.isRe = false;
        this.isTout = false;
        this.isSie = false;
        this.isSchneider = false;
        this.isSchwarz = false;
        this.numberOfLaufende = 0;
    }




    public boolean isRe() {
        return isRe;
    }

    public void setRe(boolean re) {
        isRe = re;
    }

    public boolean isKontra() {
        return isKontra;
    }

    public void setKontra(boolean kontra) {
        isKontra = kontra;
    }

    public boolean isTout() {
        return isTout;
    }

    public void setTout(boolean tout) {
        isTout = tout;
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


    public void setStakeValue(GameType gameType){
        stakeValue = gameType.getNormalPriceForOnePlayer();

        if(getNumberOfLaufende() >= 3 && (gameType.equals(GameType.SOLO) || gameType.equals(GameType.SAUSPIEL))
        || getNumberOfLaufende() >= 2 && gameType.equals(GameType.WENZ)){
            stakeValue = stakeValue + getNumberOfLaufende() * ConstantsForSheepshead.LAUFENDENTARIF;
        }

        //Laut Wiki wird bei Tout und Sie kein Schneider oder Schwarz gerechnet
        if (isTout()){
            stakeValue = stakeValue * 2;
        } else if(isSie()){
            stakeValue = stakeValue * 4;
        } else { //Weder Tout oder Sie werden gespielt,
            if(isSchneider()){
                stakeValue = stakeValue + ConstantsForSheepshead.GRUNDTARIF;
            }
            if (isSchwarz()){
                stakeValue = stakeValue + ConstantsForSheepshead.GRUNDTARIF;
            }
        }

        if (isKontra() && !isRe()){
            stakeValue = stakeValue * 2;
        }else if (isRe()){
            stakeValue = stakeValue * 4;
        }

    }

    public int getStakeValue(){
        return stakeValue;
    }

}
