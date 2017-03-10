/*
 * Copyright 2017  Erik Pohle
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

package sheepshead.manager.session;

import org.junit.Test;

import sheepshead.manager.game.GameType;

import static org.junit.Assert.assertEquals;

public class StakeTest {

    @Test
    public void testStake() {
        Stake stake = new Stake(10, 50, 20);
        assertEquals(10, stake.getGrundTarif());
        assertEquals(50, stake.getSoloTarif());
        assertEquals(20, stake.getLaufendeTarif());
        assertEquals(10, stake.getTarifFor(GameType.SAUSPIEL));
        assertEquals(50, stake.getTarifFor(GameType.SOLO));
        assertEquals(50, stake.getTarifFor(GameType.WENZ));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidTarif1() {
        new Stake(-10, 50, 20);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidTarif2() {
        new Stake(10, -50, 20);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidTarif3() {
        new Stake(10, 50, -20);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidTarif4() {
        new Stake(0, 50, 20);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidTarif5() {
        new Stake(10, 0, 20);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidTarif6() {
        new Stake(10, 50, 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testTarifLeer() {
        Stake stake = new Stake(1, 2, 3);
        stake.getTarifFor(GameType.LEER);
    }
}
