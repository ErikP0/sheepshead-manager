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

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by Nicolas on 01.10.2016.
 */
public class StakeTest {

    private Stake stake = new Stake();

    @Test
    public void testNormalSauspiel() throws Exception {

        assertEquals(ConstantsForSheepshead.GRUNDTARIF, whenSetStakeIsExecuted(GameType.SAUSPIEL));
        assertEquals(10, whenSetStakeIsExecuted(GameType.SAUSPIEL));
    }

    @Test
    public void testSauspielSchneiderSchwarz2LaufendeKontra() throws Exception {

        stake.setSchneider(true);
        stake.setSchwarz(true);
        stake.setKontra(true);
        stake.setNumberOfLaufende(2);
        assertEquals(2 * (ConstantsForSheepshead.GRUNDTARIF * 3), whenSetStakeIsExecuted(GameType.SAUSPIEL));
        assertEquals(60, whenSetStakeIsExecuted(GameType.SAUSPIEL));
    }

    @Test
    public void testSauspielSchneiderSchwarz4Laufende() throws Exception {

        stake.setSchneider(true);
        stake.setSchwarz(true);
        stake.setNumberOfLaufende(4);
        assertEquals(ConstantsForSheepshead.GRUNDTARIF * 3 + ConstantsForSheepshead.LAUFENDENTARIF * 4, whenSetStakeIsExecuted(GameType.SAUSPIEL));
        assertEquals(70, whenSetStakeIsExecuted(GameType.SAUSPIEL));
    }

    @Test
    public void testSauspielSchneiderSchwarz5LaufendeRe() throws Exception {

        stake.setSchneider(true);
        stake.setSchwarz(true);
        stake.setKontra(true);
        stake.setRe(true);
        stake.setNumberOfLaufende(5);
        assertEquals(4 * (ConstantsForSheepshead.GRUNDTARIF * 3 + ConstantsForSheepshead.LAUFENDENTARIF * 5), whenSetStakeIsExecuted(GameType.SAUSPIEL));
        assertEquals(320, whenSetStakeIsExecuted(GameType.SAUSPIEL));
    }

    @Test
    public void testWenzTout2LaufendeSchwarz() throws Exception {

        stake.setTout(true);
        stake.setNumberOfLaufende(2);
        stake.setSchneider(true);
        stake.setSchwarz(true);
        assertEquals(2 * (ConstantsForSheepshead.SOLOTARIF + ConstantsForSheepshead.LAUFENDENTARIF * 2), whenSetStakeIsExecuted(GameType.WENZ));
        assertEquals(140, whenSetStakeIsExecuted(GameType.WENZ));
    }

    @Test
    public void testSoloSchneider1Laufender() throws Exception {

        stake.setNumberOfLaufende(1);
        stake.setSchwarz(true);
        assertEquals(ConstantsForSheepshead.SOLOTARIF + ConstantsForSheepshead.GRUNDTARIF, whenSetStakeIsExecuted(GameType.SOLO));
        assertEquals(60, whenSetStakeIsExecuted(GameType.SOLO));
    }

    @Test
    public void testSoloSie() throws Exception {

        stake.setSchneider(true);
        stake.setSchwarz(true);
        stake.setSie(true);
        stake.setNumberOfLaufende(8);
        assertEquals(4 * (ConstantsForSheepshead.SOLOTARIF + ConstantsForSheepshead.LAUFENDENTARIF * 8), whenSetStakeIsExecuted(GameType.SOLO));
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

    public void thenNoExceptionShouldBeThrown(){

    }

    public void thenExceptionShouldBeThrown(){

    }

}