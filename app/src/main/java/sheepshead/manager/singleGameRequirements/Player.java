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

/**
 * Created by Nicolas on 01.10.2016.
 */

public class Player {

    private String name;

    private int number;

    private boolean isCaller;

    private boolean hasWon;

    /**
     * if player lost game, this int is negative
     */
    private int priceToGet;


    public Player(String name, int number){
        this.name = name;
        this.number = number;
        this.isCaller = false;
        this.hasWon = false;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public boolean isCaller() {
        return isCaller;
    }

    public void setCaller(boolean caller) {
        isCaller = caller;
    }

    public int getPriceToGet() {
        return priceToGet;
    }

    public void setPriceToGet(int priceToGet) {
        this.priceToGet = priceToGet;
    }

    public boolean hasWon() {
        return hasWon;
    }

    public void setHasWon(boolean hasWon) {
        this.hasWon = hasWon;
    }

}
