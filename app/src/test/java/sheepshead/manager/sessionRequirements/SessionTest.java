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

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import sheepshead.manager.singleGameRequirements.GameType;
import sheepshead.manager.singleGameRequirements.Player;
import sheepshead.manager.singleGameRequirements.StakeModifier;

import static org.junit.Assert.*;

/**
 * Created by Nicolas on 07.10.2016.
 */
public class SessionTest {
    Player player0 = new Player("A",0);
    Player player1 = new Player("B",1);
    Player player2 = new Player("C",2);
    Player player3 = new Player("D",3);
    Player player4 = new Player("E",4);
    ArrayList<Player> players = new ArrayList<Player>(5);

    Session session;

    GameType gameType;

    StakeModifier stakeModifier;

    @Before public void setUp(){
        players.add(player0);
        players.add(player1);
        players.add(player2);
        players.add(player3);
        players.add(player4);

        session = new Session(players);

        gameType = GameType.LEER;

        stakeModifier = new StakeModifier();
    }


    @Test
    public void addNewSingleGameResultParticipantFirstPlayer0() throws Exception {
        session.addNewSingleGameResult(gameType, stakeModifier);
        assertTrue(player0.isParticipant());
        assertTrue(player1.isParticipant());
        assertTrue(player2.isParticipant());
        assertTrue(player3.isParticipant());
        assertFalse(player4.isParticipant());
    }

    @Test
    public void addNewSingleGameResultParticipantFirstPlayer3() throws Exception {
        session.addNewSingleGameResult(gameType, stakeModifier);
        session.addNewSingleGameResult(gameType, stakeModifier);
        session.addNewSingleGameResult(gameType, stakeModifier);
        session.addNewSingleGameResult(gameType, stakeModifier);
        assertTrue(player0.isParticipant());
        assertTrue(player1.isParticipant());
        assertFalse(player2.isParticipant());
        assertTrue(player3.isParticipant());
        assertTrue(player4.isParticipant());
    }

    @Test
    public void addNewSingleGameResultScoreBoardWorks() throws Exception {
        session.addNewSingleGameResult(gameType, stakeModifier); //Spieler sind 0, 1, 2, 3 //jeder Spieler 0+
        gameType = GameType.SOLO; //
        player1.setHasWon(true);
        player1.setCaller(true);
        session.addNewSingleGameResult(gameType, stakeModifier); //Spieler sind 1, 2, 3, 4 //0, 150, -50, -50, -50
        gameType = GameType.SAUSPIEL;
        player1.setHasWon(false);
        player1.setCaller(false);
        player2.setHasWon(true);
        player2.setCaller(true);
        player3.setHasWon(true);
        player3.setCaller(true);
        session.addNewSingleGameResult(gameType, stakeModifier); //Spieler sind 0, 2, 3, 4 //-10, 150, -40, -40, -60

        ArrayList<Integer> line2OfScoreBoard = session.getLineOfScoreBoard(2);
        for (Player player : players){
            System.out.print(player.getPriceToGetInSession()+ " ");
        }
        assertEquals((int)line2OfScoreBoard.get(0), player0.getPriceToGetInSession());
        assertEquals((int)line2OfScoreBoard.get(2), player1.getPriceToGetInSession());
        assertEquals((int)line2OfScoreBoard.get(4), player2.getPriceToGetInSession());
        assertEquals((int)line2OfScoreBoard.get(6), player3.getPriceToGetInSession());
        assertEquals((int)line2OfScoreBoard.get(8), player4.getPriceToGetInSession());
    }

}