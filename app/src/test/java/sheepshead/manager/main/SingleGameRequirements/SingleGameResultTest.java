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

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Created by Nicolas on 01.10.2016.
 */
public class SingleGameResultTest {


    Player player1 = new Player("A", 1);
    Player player2 = new Player("B", 2);
    Player player3 = new Player("C", 3);
    Player player4 = new Player("D", 4);

    SingleGameResult singleGameResult;

    GameType gameType;

    @Test
    public void testNormalSauspielPlayer1and2Won() throws Exception {
        gameType = GameType.SAUSPIEL;

        player1.setHasWon(true);
        player2.setHasWon(true);
        player3.setPlayer(true);
        player4.setPlayer(true);

        List<Player> playerList = Arrays.asList(player1, player2, player3, player4);

        singleGameResult = new SingleGameResult(playerList, gameType);

        whenSetSingleGameResultIsExecuted();

        assertEquals(10, player1.getPriceToGet());
        assertEquals(10, player2.getPriceToGet());
        assertEquals(-10, player3.getPriceToGet());
        assertEquals(-10, player4.getPriceToGet());
    }

    @Test
    public void testSauspielSchneiderSchwarz2LaufendeKontra() throws Exception {
        gameType = GameType.SAUSPIEL;

        player1.setHasWon(true);
        player2.setHasWon(true);
        player3.setPlayer(true);
        player4.setPlayer(true);

        List<Player> playerList = Arrays.asList(player1, player2, player3, player4);

        singleGameResult = new SingleGameResult(playerList, gameType);

        singleGameResult.setSchneider(true);
        singleGameResult.setSchwarz(true);
        singleGameResult.setKontra(true);
        singleGameResult.setNumberOfLaufende(2);

        whenSetSingleGameResultIsExecuted();

        assertEquals(60, player1.getPriceToGet());
        assertEquals(60, player2.getPriceToGet());
        assertEquals(-60, player3.getPriceToGet());
        assertEquals(-60, player4.getPriceToGet());

    }

    @Test
    public void testSauspielSchneiderSchwarz4Laufende() throws Exception {
        gameType = GameType.SAUSPIEL;

        player1.setHasWon(true);
        player3.setHasWon(true);
        player1.setPlayer(true);
        player3.setPlayer(true);

        List<Player> playerList = Arrays.asList(player1, player2, player3, player4);

        singleGameResult = new SingleGameResult(playerList, gameType);

        singleGameResult.setSchneider(true);
        singleGameResult.setSchwarz(true);
        singleGameResult.setNumberOfLaufende(4);

        whenSetSingleGameResultIsExecuted();

        assertEquals(70, player1.getPriceToGet());
        assertEquals(-70, player2.getPriceToGet());
        assertEquals(70, player3.getPriceToGet());
        assertEquals(-70, player4.getPriceToGet());
    }

    @Test
    public void testSauspielSchneiderSchwarz5LaufendeRe() throws Exception {
        gameType = GameType.SAUSPIEL;

        player1.setHasWon(true);
        player3.setHasWon(true);
        player1.setPlayer(true);
        player3.setPlayer(true);

        List<Player> playerList = Arrays.asList(player1, player2, player3, player4);

        singleGameResult = new SingleGameResult(playerList, gameType);

        singleGameResult.setSchneider(true);
        singleGameResult.setSchwarz(true);
        singleGameResult.setKontra(true);
        singleGameResult.setRe(true);
        singleGameResult.setNumberOfLaufende(5);

        whenSetSingleGameResultIsExecuted();

        assertEquals(320, player1.getPriceToGet());
        assertEquals(-320, player2.getPriceToGet());
        assertEquals(320, player3.getPriceToGet());
        assertEquals(-320, player4.getPriceToGet());
    }

    @Test
    public void testWenzTout2LaufendeSchwarz() throws Exception {
        gameType = GameType.WENZ;

        player1.setHasWon(true);
        player1.setPlayer(true);


        List<Player> playerList = Arrays.asList(player1, player2, player3, player4);

        singleGameResult = new SingleGameResult(playerList, gameType);

        singleGameResult.setTout(true);
        singleGameResult.setSchneider(true);
        singleGameResult.setSchwarz(true);
        singleGameResult.setNumberOfLaufende(2);

        whenSetSingleGameResultIsExecuted();

        assertEquals(420, player1.getPriceToGet());
        assertEquals(-140, player2.getPriceToGet());
        assertEquals(-140, player3.getPriceToGet());
        assertEquals(-140, player4.getPriceToGet());
    }

    @Test
    public void testSoloSchneider1Laufender() throws Exception {

        gameType = GameType.SOLO;

        player1.setPlayer(true);
        player2.setHasWon(true);
        player3.setHasWon(true);
        player4.setHasWon(true);

        List<Player> playerList = Arrays.asList(player1, player2, player3, player4);

        singleGameResult = new SingleGameResult(playerList, gameType);

        singleGameResult.setSchneider(true);
        singleGameResult.setNumberOfLaufende(1);

        whenSetSingleGameResultIsExecuted();

        assertEquals(-180, player1.getPriceToGet());
        assertEquals(60, player2.getPriceToGet());
        assertEquals(60, player3.getPriceToGet());
        assertEquals(60, player4.getPriceToGet());
    }

    @Test
    public void testSoloSie() throws Exception {

        gameType = GameType.SOLO;

        player1.setPlayer(true);
        player1.setHasWon(true);

        List<Player> playerList = Arrays.asList(player1, player2, player3, player4);

        singleGameResult = new SingleGameResult(playerList, gameType);

        singleGameResult.setSchneider(true);
        singleGameResult.setSchwarz(true);
        singleGameResult.setSie(true);
        singleGameResult.setNumberOfLaufende(8);

        whenSetSingleGameResultIsExecuted();

        assertEquals(1560, player1.getPriceToGet());
        assertEquals(-520, player2.getPriceToGet());
        assertEquals(-520, player3.getPriceToGet());
        assertEquals(-520, player4.getPriceToGet());
    }

    @Test
    public void testLeeresSpiel() throws Exception {
        gameType = GameType.LEER;

        List<Player> playerList = Arrays.asList(player1, player2, player3, player4);

        singleGameResult = new SingleGameResult(playerList, gameType);

        whenSetSingleGameResultIsExecuted();

        assertEquals(0, player1.getPriceToGet());
        assertEquals(0, player2.getPriceToGet());
        assertEquals(0, player3.getPriceToGet());
        assertEquals(0, player4.getPriceToGet());
    }


    public void whenSetSingleGameResultIsExecuted(){
        singleGameResult.setSingleGameResult();
    }

    public void thenNoExceptionShouldBeThrown(){

    }

    public void thenExceptionShouldBeThrown(){

    }

}