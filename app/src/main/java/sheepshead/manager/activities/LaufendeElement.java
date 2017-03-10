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

package sheepshead.manager.activities;


import sheepshead.manager.activities.fillgameresult.FillGameResult;
import sheepshead.manager.game.GameType;

/**
 * Represents a selectable element for the "Laufende"-Dropdown in {@link FillGameResult}
 */
public abstract class LaufendeElement {

    //toString() is explicitly overloaded because the Spinner will use toString to turn the element
    //into a displayable string
    public abstract String toString();

    /**
     * @return Returns the amount of "Laufende" represented by this element
     */
    public abstract int getAnzLaufende();

    /**
     * A builder convenience class for constructing matching element arrays depending on a game type
     * Usage: <code>new LaufendeElement.Builder(gametype).build()</code>
     */
    public static class Builder {
        /**
         * The game type to build the elements for
         */
        private GameType type;

        /**
         * Creates a new builder with the given game type
         *
         * @param gameType game type that determines what elements are created
         */
        public Builder(GameType gameType) {
            type = gameType;
        }

        /**
         * Returns a array containing matching elements determined by the game type.
         * <li>{@link GameType#LEER} returns one element stating "No game mode selected"</li>
         * <li>{@link GameType#WENZ} returns 4 elements (0, 2, 3, 4 laufende) </li>
         * <lie>{@link GameType#SAUSPIEL}, {@link GameType#SOLO} return 7 elements (0, 3-8 laufende)</lie>
         *
         * @return an array containing matching elements depending on the game type
         */
        public LaufendeElement[] build() {
            if (type.equals(GameType.LEER)) {
                return createEmptySpinner();
            }
            int lowerBound = type.getLaufendeBegin();
            int upperBound = type.getLaufendeEnd();
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

        /**
         * @return Creates and returns an element for {@link GameType#LEER}
         */
        private LaufendeElement[] createEmptySpinner() {
            LaufendeElement[] result = {new LaufendeElementNoGameTypeSelected()};
            return result;
        }
    }

    /**
     * Subclass that represents a LaufendeElement that really is a number
     */
    private static class LaufendeElementNumber extends LaufendeElement {
        private int number;

        LaufendeElementNumber(int n) {
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

    /**
     * Subclass that represents a LaufendeElement that is not a number but more a info message for the
     * user
     */
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
