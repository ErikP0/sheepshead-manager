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

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Created by Nicolas on 01.10.2016.
 */
public class SingleGameResultTest {

    private Player player1 = new Player("A", 0);
    private Player player2 = new Player("B", 1);
    private Player player3 = new Player("C", 2);
    private Player player4 = new Player("D", 3);

    private ArrayList<Player> playerList;

    private SingleGameResult singleGameResult;

    private GameType gameType;

    private StakeModifier stakeModifier;


    @Test
    public void testNormalSauspielPlayer1and2Won() throws Exception {
        gameType = GameType.SAUSPIEL;

        player1.setHasWon(true);
        player2.setHasWon(true);
        player3.setCaller(true);
        player4.setCaller(true);

        givenSetPlayerList();

        stakeModifier = new StakeModifier();

        singleGameResult = new SingleGameResult(playerList, gameType, stakeModifier);

        whenSetSingleGameResultIsExecuted();

        assertEquals(10, player1.getPriceToGetInSingleGame());
        assertEquals(10, player2.getPriceToGetInSingleGame());
        assertEquals(-10, player3.getPriceToGetInSingleGame());
        assertEquals(-10, player4.getPriceToGetInSingleGame());
    }

    @Test
    public void testSauspielSchneiderSchwarz2LaufendeKontra() throws Exception {
        gameType = GameType.SAUSPIEL;

        player1.setHasWon(true);
        player2.setHasWon(true);
        player3.setCaller(true);
        player4.setCaller(true);

        givenSetPlayerList();

        stakeModifier = new StakeModifier();

        stakeModifier.setSchneider(true);
        stakeModifier.setSchwarz(true);
        stakeModifier.setKontra(true);
        stakeModifier.setNumberOfLaufende(2);

        singleGameResult = new SingleGameResult(playerList, gameType, stakeModifier);

        whenSetSingleGameResultIsExecuted();

        assertEquals(60, player1.getPriceToGetInSingleGame());
        assertEquals(60, player2.getPriceToGetInSingleGame());
        assertEquals(-60, player3.getPriceToGetInSingleGame());
        assertEquals(-60, player4.getPriceToGetInSingleGame());

    }

    @Test
    public void testSauspielSchneiderSchwarz4Laufende() throws Exception {
        gameType = GameType.SAUSPIEL;

        player1.setHasWon(true);
        player3.setHasWon(true);
        player1.setCaller(true);
        player3.setCaller(true);

        givenSetPlayerList();

        stakeModifier = new StakeModifier();

        stakeModifier.setSchneider(true);
        stakeModifier.setSchwarz(true);
        stakeModifier.setNumberOfLaufende(4);

        singleGameResult = new SingleGameResult(playerList, gameType, stakeModifier);

        whenSetSingleGameResultIsExecuted();

        assertEquals(70, player1.getPriceToGetInSingleGame());
        assertEquals(-70, player2.getPriceToGetInSingleGame());
        assertEquals(70, player3.getPriceToGetInSingleGame());
        assertEquals(-70, player4.getPriceToGetInSingleGame());
    }

    @Test
    public void testSauspielSchneiderSchwarz5LaufendeRe() throws Exception {
        gameType = GameType.SAUSPIEL;

        player1.setHasWon(true);
        player3.setHasWon(true);
        player1.setCaller(true);
        player3.setCaller(true);

        givenSetPlayerList();

        stakeModifier = new StakeModifier();

        stakeModifier.setSchneider(true);
        stakeModifier.setSchwarz(true);
        stakeModifier.setKontra(true);
        stakeModifier.setRe(true);
        stakeModifier.setNumberOfLaufende(5);

        singleGameResult = new SingleGameResult(playerList, gameType, stakeModifier);

        whenSetSingleGameResultIsExecuted();

        assertEquals(320, player1.getPriceToGetInSingleGame());
        assertEquals(-320, player2.getPriceToGetInSingleGame());
        assertEquals(320, player3.getPriceToGetInSingleGame());
        assertEquals(-320, player4.getPriceToGetInSingleGame());
    }

    @Test
    public void testWenzTout2LaufendeSchwarz() throws Exception {
        gameType = GameType.WENZ;

        player1.setHasWon(true);
        player1.setCaller(true);

        givenSetPlayerList();

        stakeModifier = new StakeModifier();

        stakeModifier.setTout(true);
        stakeModifier.setSchneider(true);
        stakeModifier.setSchwarz(true);
        stakeModifier.setNumberOfLaufende(2);

        singleGameResult = new SingleGameResult(playerList, gameType, stakeModifier);

        whenSetSingleGameResultIsExecuted();

        assertEquals(420, player1.getPriceToGetInSingleGame());
        assertEquals(-140, player2.getPriceToGetInSingleGame());
        assertEquals(-140, player3.getPriceToGetInSingleGame());
        assertEquals(-140, player4.getPriceToGetInSingleGame());
    }

    @Test
    public void testSoloSchneider1Laufender() throws Exception {

        gameType = GameType.SOLO;

        player1.setCaller(true);
        player2.setHasWon(true);
        player3.setHasWon(true);
        player4.setHasWon(true);

        givenSetPlayerList();

        stakeModifier = new StakeModifier();

        stakeModifier.setSchneider(true);
        stakeModifier.setNumberOfLaufende(1);

        singleGameResult = new SingleGameResult(playerList, gameType, stakeModifier);

        whenSetSingleGameResultIsExecuted();

        assertEquals(-180, player1.getPriceToGetInSingleGame());
        assertEquals(60, player2.getPriceToGetInSingleGame());
        assertEquals(60, player3.getPriceToGetInSingleGame());
        assertEquals(60, player4.getPriceToGetInSingleGame());
    }

    @Test
    public void testSoloSie() throws Exception {

        gameType = GameType.SOLO;

        player1.setCaller(true);
        player1.setHasWon(true);

        givenSetPlayerList();

        stakeModifier = new StakeModifier();

        stakeModifier.setSchneider(true);
        stakeModifier.setSchwarz(true);
        stakeModifier.setSie(true);
        stakeModifier.setNumberOfLaufende(8);

        singleGameResult = new SingleGameResult(playerList, gameType, stakeModifier);

        whenSetSingleGameResultIsExecuted();

        assertEquals(1560, player1.getPriceToGetInSingleGame());
        assertEquals(-520, player2.getPriceToGetInSingleGame());
        assertEquals(-520, player3.getPriceToGetInSingleGame());
        assertEquals(-520, player4.getPriceToGetInSingleGame());
    }

    @Test
    public void testLeeresSpiel() throws Exception {
        gameType = GameType.LEER;

        givenSetPlayerList();

        stakeModifier = new StakeModifier();

        singleGameResult = new SingleGameResult(playerList, gameType, stakeModifier);

        whenSetSingleGameResultIsExecuted();

        assertEquals(0, player1.getPriceToGetInSingleGame());
        assertEquals(0, player2.getPriceToGetInSingleGame());
        assertEquals(0, player3.getPriceToGetInSingleGame());
        assertEquals(0, player4.getPriceToGetInSingleGame());
    }

/*
    @Test
    public void testNormalSauspiel() throws Exception {

        assertEquals(SheapsheadConstants.GRUNDTARIF, whenSetStakeIsExecuted(GameType.SAUSPIEL));
        assertEquals(10, whenSetStakeIsExecuted(GameType.SAUSPIEL));
    }

    @Test
    public void testSauspielSchneiderSchwarz2LaufendeKontra() throws Exception {

        stake.setSchneider(true);
        stake.setSchwarz(true);
        stake.setKontra(true);
        stake.setNumberOfLaufende(2);
        assertEquals(2 * (SheapsheadConstants.GRUNDTARIF * 3), whenSetStakeIsExecuted(GameType.SAUSPIEL));
        assertEquals(60, whenSetStakeIsExecuted(GameType.SAUSPIEL));
    }

    @Test
    public void testSauspielSchneiderSchwarz4Laufende() throws Exception {

        stake.setSchneider(true);
        stake.setSchwarz(true);
        stake.setNumberOfLaufende(4);
        assertEquals(SheapsheadConstants.GRUNDTARIF * 3 + SheapsheadConstants.LAUFENDENTARIF * 4, whenSetStakeIsExecuted(GameType.SAUSPIEL));
        assertEquals(70, whenSetStakeIsExecuted(GameType.SAUSPIEL));
    }

    @Test
    public void testSauspielSchneiderSchwarz5LaufendeRe() throws Exception {

        stake.setSchneider(true);
        stake.setSchwarz(true);
        stake.setKontra(true);
        stake.setRe(true);
        stake.setNumberOfLaufende(5);
        assertEquals(4 * (SheapsheadConstants.GRUNDTARIF * 3 + SheapsheadConstants.LAUFENDENTARIF * 5), whenSetStakeIsExecuted(GameType.SAUSPIEL));
        assertEquals(320, whenSetStakeIsExecuted(GameType.SAUSPIEL));
    }

    @Test
    public void testWenzTout2LaufendeSchwarz() throws Exception {

        stake.setTout(true);
        stake.setNumberOfLaufende(2);
        stake.setSchneider(true);
        stake.setSchwarz(true);
        assertEquals(2 * (SheapsheadConstants.SOLOTARIF + SheapsheadConstants.LAUFENDENTARIF * 2), whenSetStakeIsExecuted(GameType.WENZ));
        assertEquals(140, whenSetStakeIsExecuted(GameType.WENZ));
    }

    @Test
    public void testSoloSchneider1Laufender() throws Exception {

        stake.setNumberOfLaufende(1);
        stake.setSchwarz(true);
        assertEquals(SheapsheadConstants.SOLOTARIF + SheapsheadConstants.GRUNDTARIF, whenSetStakeIsExecuted(GameType.SOLO));
        assertEquals(60, whenSetStakeIsExecuted(GameType.SOLO));
    }

    @Test
    public void testSoloSie() throws Exception {

        stake.setSchneider(true);
        stake.setSchwarz(true);
        stake.setSie(true);
        stake.setNumberOfLaufende(8);
        assertEquals(4 * (SheapsheadConstants.SOLOTARIF + SheapsheadConstants.LAUFENDENTARIF * 8), whenSetStakeIsExecuted(GameType.SOLO));
        assertEquals(520, whenSetStakeIsExecuted(GameType.SOLO));
    }

    @Test
    public void testLeeresSpiel() throws Exception {

        assertEquals(0, whenSetStakeIsExecuted(GameType.LEER));
    }

    public int whenSetStakeIsExecuted(GameType gameType){
        stake.setStakeValue(gameType);
        return stake.getStakeValue();
    }


*/
    public void givenSetPlayerList() throws Exception {
        playerList = new ArrayList<Player>(4);
        playerList.add(player1);
        playerList.add(player2);
        playerList.add(player3);
        playerList.add(player4);
    }


    private void whenSetSingleGameResultIsExecuted(){
        singleGameResult.setSingleGameResult();
    }

   // private void whenCalculateStakeValueIsExecuted() { singleGameResult.calcu}

    private void thenNoExceptionShouldBeThrown(){

    }

    private void thenExceptionShouldBeThrown(){

    }

}