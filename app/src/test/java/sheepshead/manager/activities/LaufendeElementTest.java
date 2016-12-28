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

package sheepshead.manager.activities;


import org.junit.Test;

import sheepshead.manager.game.GameType;

import static junit.framework.Assert.assertEquals;

public class LaufendeElementTest {

    @Test
    public void testOutputSauspiel() {
        int[] expected = {0, 3, 4, 5, 6, 7, 8};
        testOutputForGametype(GameType.SAUSPIEL, expected);

    }

    @Test
    public void testOutputWenz() {
        int[] expected = {0, 2, 3, 4};
        testOutputForGametype(GameType.WENZ, expected);

    }

    @Test
    public void testOutputSolo() {
        int[] expected = {0, 3, 4, 5, 6, 7, 8};
        testOutputForGametype(GameType.SOLO, expected);
    }

    @Test
    public void testOutputNoGameTypeSelected() {
        int[] expected = {0};
        testOutputForGametype(GameType.LEER, expected);
    }

    private void testOutputForGametype(GameType type, int[] expected) {
        LaufendeElement[] output = new LaufendeElement.Builder(type).build();
        assertEquals(expected.length, output.length);
        for (int i = 0; i < expected.length; i++) {
            assertEquals(expected[i], output[i].getAnzLaufende());
        }
    }
}
