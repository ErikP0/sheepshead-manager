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

package sheepshead.manager.sessionRequirements;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import sheepshead.manager.singleGameRequirements.GameType;
import sheepshead.manager.singleGameRequirements.Player;
import sheepshead.manager.singleGameRequirements.SingleGameResult;
import sheepshead.manager.singleGameRequirements.StakeModifier;

/**
 * Created by Nicolas on 06.10.2016.
 */

public class Session {

    private ArrayList<Player> playerList; //Sollte am besten Fix sein. Wird in übergeordneter Klasse am besten einmalig initalisiert. Alle Player sind dann als Objekte immer vorhanden in einer Sitzung.

    private ArrayList<SingleGameResult> gameResults;

    private ArrayList<ArrayList<Integer>> moneyMatrix; //Evtl. sogar redundant. Ergebnisse könnten auch über die gameResults und dann jeweils getSingleGameMoney() erhalten werden.

    private int firstPlayerToParticipate = 0; //wenn mehr als 4 Spieler mitspielen, ist das der erste von 4, der dabei ist.


    public Session(ArrayList<Player> playerList){
        moneyMatrix = new ArrayList<ArrayList<Integer>>(0);

        gameResults = new ArrayList<SingleGameResult>(0);

        this.playerList = playerList;
    }


    public void calculateMoneyMatrix(){
        for (SingleGameResult singleGameResult : gameResults) {
            addToMoneyMatrix(singleGameResult);
        }
    }

    private void addToMoneyMatrix(SingleGameResult singleGameResult){
        moneyMatrix.add(singleGameResult.getSingleGameMoney());
    }

    public void addNewSingleGameResult(GameType gameType, StakeModifier stakeModifier){

        setParticipantPlayers();

        SingleGameResult singleGameResult = new SingleGameResult(playerList, gameType, stakeModifier);

        singleGameResult.setSingleGameResult();

        gameResults.add(singleGameResult);

        addToMoneyMatrix(singleGameResult);

        firstPlayerToParticipate = (gameResults.size() % playerList.size());
    }

    private void setParticipantPlayers(){

        for (Player player : playerList){
            player.setParticipant(false);
        }

        playerList.get(firstPlayerToParticipate).setParticipant(true);

        playerList.get((firstPlayerToParticipate + 1) % playerList.size()).setParticipant(true);

        playerList.get((firstPlayerToParticipate + 2) % playerList.size()).setParticipant(true);

        playerList.get((firstPlayerToParticipate + 3) % playerList.size()).setParticipant(true);
    }


    public ArrayList<Player> getPlayerList() {
        return playerList;
    }

    public void setPlayerList(ArrayList<Player> playerList) {
        this.playerList = playerList;
    }

    public ArrayList<SingleGameResult> getGameResults() {
        return gameResults;
    }

    public void setGameResults(ArrayList<SingleGameResult> gameResults) {
        this.gameResults = gameResults;
    }

    public ArrayList<ArrayList<Integer>> getMoneyMatrix() {
        return moneyMatrix;
    }

    public void setMoneyMatrix(ArrayList<ArrayList<Integer>> moneyMatrix) {
        this.moneyMatrix = moneyMatrix;
    }

}
