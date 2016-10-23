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

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import sheepshead.manager.sessionRequirements.Stake;

import static junit.framework.Assert.assertEquals;

/**
 * Created by Nicolas on 01.10.2016.
 */
public class SingleGameResultTest {

    private Player player1 = new Player("A", 0);
    private Player player2 = new Player("B", 1);
    private Player player3 = new Player("C", 2);
    private Player player4 = new Player("D", 3);

    private Stake defaultStake = new Stake(10, 50, 10);

    private SingleGameResult singleGameResult;

    private GameType gameType;

    private StakeModifier stakeModifier;


    @Test
    public void testNormalSauspielPlayer1and2Won() throws Exception {
        gameType = GameType.SAUSPIEL;

        List<PlayerRole> playerList = new ArrayList<>(4);
        playerList.add(new PlayerRole(player1, false, true));
        playerList.add(new PlayerRole(player2, false, true));
        playerList.add(new PlayerRole(player3, true, false));
        playerList.add(new PlayerRole(player4, true, false));

        stakeModifier = new StakeModifier();

        singleGameResult = new SingleGameResult(playerList, gameType, stakeModifier);

        whenSetSingleGameResultIsExecuted();

        int[] expected = {10, 10, -10, -10};
        int index = 0;
        for (PlayerRole role : singleGameResult.getParticipants()) {
            assertEquals(expected[index], role.getMoney());
            index++;
        }
    }

    @Test
    public void testSauspielSchneiderSchwarz2LaufendeKontra() throws Exception {
        gameType = GameType.SAUSPIEL;

        List<PlayerRole> playerList = new ArrayList<>(4);
        playerList.add(new PlayerRole(player1, false, true));
        playerList.add(new PlayerRole(player2, false, true));
        playerList.add(new PlayerRole(player3, true, false));
        playerList.add(new PlayerRole(player4, true, false));


        stakeModifier = new StakeModifier();

        stakeModifier.setSchneider(true);
        stakeModifier.setSchwarz(true);
        stakeModifier.setKontra(true);
        stakeModifier.setNumberOfLaufende(2);

        singleGameResult = new SingleGameResult(playerList, gameType, stakeModifier);

        whenSetSingleGameResultIsExecuted();

        int[] expected = {60, 60, -60, -60};
        int index = 0;
        for (PlayerRole role : singleGameResult.getParticipants()) {
            assertEquals(expected[index], role.getMoney());
            index++;
        }

    }

    @Test
    public void testSauspielSchneiderSchwarz4Laufende() throws Exception {
        gameType = GameType.SAUSPIEL;

        List<PlayerRole> playerList = new ArrayList<>(4);
        playerList.add(new PlayerRole(player1, true, true));
        playerList.add(new PlayerRole(player2, false, false));
        playerList.add(new PlayerRole(player3, true, true));
        playerList.add(new PlayerRole(player4, false, false));

        stakeModifier = new StakeModifier();

        stakeModifier.setSchneider(true);
        stakeModifier.setSchwarz(true);
        stakeModifier.setNumberOfLaufende(4);

        singleGameResult = new SingleGameResult(playerList, gameType, stakeModifier);

        whenSetSingleGameResultIsExecuted();

        int[] expected = {70, -70, 70, -70};
        int index = 0;
        for (PlayerRole role : singleGameResult.getParticipants()) {
            assertEquals(expected[index], role.getMoney());
            index++;
        }
    }

    @Test
    public void testSauspielSchneiderSchwarz5LaufendeRe() throws Exception {
        gameType = GameType.SAUSPIEL;

        List<PlayerRole> playerList = new ArrayList<>(4);
        playerList.add(new PlayerRole(player1, true, true));
        playerList.add(new PlayerRole(player2, false, false));
        playerList.add(new PlayerRole(player3, true, true));
        playerList.add(new PlayerRole(player4, false, false));

        stakeModifier = new StakeModifier();

        stakeModifier.setSchneider(true);
        stakeModifier.setSchwarz(true);
        stakeModifier.setKontra(true);
        stakeModifier.setRe(true);
        stakeModifier.setNumberOfLaufende(5);

        singleGameResult = new SingleGameResult(playerList, gameType, stakeModifier);

        whenSetSingleGameResultIsExecuted();

        int[] expected = {320, -320, 320, -320};
        int index = 0;
        for (PlayerRole role : singleGameResult.getParticipants()) {
            assertEquals(expected[index], role.getMoney());
            index++;
        }
    }

    @Test
    public void testWenzTout2LaufendeSchwarz() throws Exception {
        gameType = GameType.WENZ;

        List<PlayerRole> playerList = new ArrayList<>(4);
        playerList.add(new PlayerRole(player1, true, true));
        playerList.add(new PlayerRole(player2, false, false));
        playerList.add(new PlayerRole(player3, false, false));
        playerList.add(new PlayerRole(player4, false, false));

        stakeModifier = new StakeModifier();

        stakeModifier.setTout(true);
        stakeModifier.setSchneider(true);
        stakeModifier.setSchwarz(true);
        stakeModifier.setNumberOfLaufende(2);

        singleGameResult = new SingleGameResult(playerList, gameType, stakeModifier);

        whenSetSingleGameResultIsExecuted();

        int[] expected = {420, -140, -140, -140};
        int index = 0;
        for (PlayerRole role : singleGameResult.getParticipants()) {
            assertEquals(expected[index], role.getMoney());
            index++;
        }
    }

    @Test
    public void testSoloSchneider1Laufender() throws Exception {

        gameType = GameType.SOLO;

        List<PlayerRole> playerList = new ArrayList<>(4);
        playerList.add(new PlayerRole(player1, true, false));
        playerList.add(new PlayerRole(player2, false, true));
        playerList.add(new PlayerRole(player3, false, true));
        playerList.add(new PlayerRole(player4, false, true));

        stakeModifier = new StakeModifier();

        stakeModifier.setSchneider(true);
        stakeModifier.setNumberOfLaufende(1);

        singleGameResult = new SingleGameResult(playerList, gameType, stakeModifier);

        whenSetSingleGameResultIsExecuted();

        int[] expected = {-180, 60, 60, 60};
        int index = 0;
        for (PlayerRole role : singleGameResult.getParticipants()) {
            assertEquals(expected[index], role.getMoney());
            index++;
        }
    }

    @Test
    public void testSoloSie() throws Exception {

        gameType = GameType.SOLO;

        List<PlayerRole> playerList = new ArrayList<>(4);
        playerList.add(new PlayerRole(player1, true, true));
        playerList.add(new PlayerRole(player2, false, false));
        playerList.add(new PlayerRole(player3, false, false));
        playerList.add(new PlayerRole(player4, false, false));

        stakeModifier = new StakeModifier();

        stakeModifier.setSchneider(true);
        stakeModifier.setSchwarz(true);
        stakeModifier.setSie(true);
        stakeModifier.setNumberOfLaufende(8);

        singleGameResult = new SingleGameResult(playerList, gameType, stakeModifier);

        whenSetSingleGameResultIsExecuted();

        int[] expected = {1560, -520, -520, -520};
        int index = 0;
        for (PlayerRole role : singleGameResult.getParticipants()) {
            assertEquals(expected[index], role.getMoney());
            index++;
        }
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


    private void whenSetSingleGameResultIsExecuted() {
        singleGameResult.calculate(defaultStake);
    }

    // private void whenCalculateStakeValueIsExecuted() { singleGameResult.calcu}

    private void thenNoExceptionShouldBeThrown() {

    }

    private void thenExceptionShouldBeThrown() {

    }

}