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

public class Player {

    private String playername;

    private int playernumber;

    private boolean isPlayer;

    private boolean hasWon;

    /**
     * if player lost game, this int is negative
     */
    private int priceToGet;


    public Player(String playername, int playernumber){
        this.playername = playername;
        this.playernumber = playernumber;
        this.isPlayer = false;
        this.hasWon = false;
    }


    public String getPlayername() {
        return playername;
    }

    public void setPlayername(String playername) {
        this.playername = playername;
    }

    public int getPlayernumber() {
        return playernumber;
    }

    public void setPlayernumber(int playernumber) {
        this.playernumber = playernumber;
    }

    public boolean isPlayer() {
        return isPlayer;
    }

    public void setPlayer(boolean player) {
        isPlayer = player;
    }

    public int getPriceToGet() {
        return priceToGet;
    }

    public void setPriceToGet(int priceToGet) {
        this.priceToGet = priceToGet;
    }

    public boolean isHasWon() {
        return hasWon;
    }

    public void setHasWon(boolean hasWon) {
        this.hasWon = hasWon;
    }

}
