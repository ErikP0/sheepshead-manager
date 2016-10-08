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
import java.util.prefs.InvalidPreferencesFormatException;

import sheepshead.manager.singleGameRequirements.GameType;
import sheepshead.manager.singleGameRequirements.Player;
import sheepshead.manager.singleGameRequirements.SingleGameResult;
import sheepshead.manager.singleGameRequirements.StakeModifier;
import sheepshead.manager.singleGameRequirements.ValidGame;

/**
 * Created by Nicolas on 06.10.2016.
 */

public class Session {

    private ArrayList<Player> playerList; //Sollte am besten Fix sein. Wird in übergeordneter Klasse am besten einmalig initalisiert. Alle Player sind dann als Objekte immer vorhanden in einer Sitzung.

    private ArrayList<SingleGameResult> gameResults;

    private ArrayList<ArrayList<Integer>> scoreBoard; //Evtl. sogar redundant. Ergebnisse könnten auch über die gameResults und dann jeweils getSingleGameMoney() erhalten werden.

    private int firstPlayerToParticipate = 0; //wenn mehr als 4 Spieler mitspielen, ist das der erste von 4, der dabei ist.


    public Session(ArrayList<Player> playerList){
        scoreBoard = new ArrayList<ArrayList<Integer>>(0);

        gameResults = new ArrayList<SingleGameResult>(0);

        this.playerList = playerList;
    }


    public void calculateMoneyMatrix(){
        for (SingleGameResult singleGameResult : gameResults) {
            addToScoreBoard(singleGameResult);
        }
    }

    private void addToScoreBoard(SingleGameResult singleGameResult){
        scoreBoard.add(singleGameResult.getSingleGameMoney());
    }

    public ArrayList<Integer> getLineOfScoreBoard(int lineNumber){
        return scoreBoard.get(lineNumber);
    }

    /**
     * Erzeugt ein neues SingleGameResult, dessen Ergebnisse dann in die Scoreboard eingetragen werden.
     * @param gameType Es wird ein Spieltyp also (Sauspiel, Wenz, Solo) benötigt
     * @param stakeModifier Es müssen die Parameter für ein Spiel gesetzt werden (Schneider, Schwarz, Tout, Re ...)
     */
    public void addNewSingleGameResult(GameType gameType, StakeModifier stakeModifier) throws InvalidPreferencesFormatException {

        setParticipantPlayers();

        SingleGameResult singleGameResult = new SingleGameResult(playerList, gameType, stakeModifier);

        if (!ValidGame.isValidGame(playerList, gameType, stakeModifier)) {
            throw new InvalidPreferencesFormatException("Game cannot be added! Invalid Parameters are used.");
        }

        singleGameResult.setSingleGameResult();

        if (!ValidGame.isValidMoneySum(playerList)) {
            throw new InvalidPreferencesFormatException("The Sum of one line must be 0!");
        }

        gameResults.add(singleGameResult);

        addToScoreBoard(singleGameResult);

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

    public ArrayList<ArrayList<Integer>> getScoreBoard() {
        return scoreBoard;
    }

    public void setScoreBoard(ArrayList<ArrayList<Integer>> scoreBoard) {
        this.scoreBoard = scoreBoard;
    }

    public int getFirstPlayerToParticipate() {
        return firstPlayerToParticipate;
    }

    //wird nur für den Test benötigt bisher
    public void setFirstPlayerToParticipate(int firstPlayerToParticipate) {
        this.firstPlayerToParticipate = firstPlayerToParticipate;
    }


}
