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


import sheepshead.manager.singleGameRequirements.GameType;

public class LaufendeSpinnerBuilder {

    private GameType type;

    public LaufendeSpinnerBuilder(GameType gameType) {
        type = gameType;
    }

    public LaufendeElement[] build() {
        int lowerBound = 0;
        int upperBound = 0;
        switch (type) {
            case LEER:
                return createEmptySpinner();
            case WENZ:
                lowerBound = 2;
                upperBound = 4;
                break;
            case SAUSPIEL://fall-through
            case SOLO:
                lowerBound = 3;
                upperBound = 8;
                break;
            default:
                throw new IllegalStateException("Unknown game type " + type);
        }
        //create spinner content: 0 is always there + [lowerBound, upperBound]
        LaufendeElement[] result = new LaufendeElement[1 + (upperBound - lowerBound + 1)];
        int index = 0;
        //insert 0
        result[index] = new LaufendeElementNumber(0);
        index++;
        for (int currentNumber = lowerBound; currentNumber <= upperBound; currentNumber++) {
            result[index] = new LaufendeElementNumber(currentNumber);
            index++;
        }
        return result;
    }

    private LaufendeElement[] createEmptySpinner() {
        LaufendeElement[] result = {new LaufendeElementNoGameTypeSelected()};
        return result;
    }

    public static abstract class LaufendeElement {

        public abstract String toString();

        public abstract int getAnzLaufende();
    }

    private static class LaufendeElementNumber extends LaufendeElement {
        private int number;

        public LaufendeElementNumber(int n) {
            number = n;
        }

        @Override
        public String toString() {
            return Integer.toString(number);
        }

        @Override
        public int getAnzLaufende() {
            return number;
        }
    }

    private static class LaufendeElementNoGameTypeSelected extends LaufendeElement {
        @Override
        public int getAnzLaufende() {
            return 0;
        }

        @Override
        public String toString() {
            return "Kein Spiel ausgewählt";
        }
    }
}
