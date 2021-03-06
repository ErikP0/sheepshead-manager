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

package sheepshead.manager.game;

import sheepshead.manager.serialization.CSVCellContent;
import sheepshead.manager.session.ICSVSerializable;

/**
 * The class StakeModifier is a collection of information that modify the stake of a single game.
 * E.g. the "Schneider" modifier adds the base stake, the "Kontra" modifier doubles the stake
 */
public class StakeModifier implements ICSVSerializable {

    private boolean isKontra;

    private boolean isRe;

    /**
     * Es darf immer nur entweder isTout oder isSie gelten
     */
    private boolean isTout;

    /**
     * Es darf immer nur entweder isTout oder isSie gelten
     */
    private boolean isSie;

    private boolean isSchneider;

    private boolean isSchwarz;

    private int numberOfLaufende;

    /**
     * Creates an empty stake modifier (no modifiers are set)
     */
    public StakeModifier() {
        this.isKontra = false;
        this.isRe = false;
        this.isTout = false;
        this.isSie = false;
        this.isSchneider = false;
        this.isSchwarz = false;
        this.numberOfLaufende = 0;
    }

    public StakeModifier(CSVCellContent.Reader data) {
        this();
        setKontra(data.getBoolean());
        setRe(data.getBoolean());
        setTout(data.getBoolean());
        setSie(data.getBoolean());
        setSchneider(data.getBoolean());
        setSchwarz(data.getBoolean());
        setNumberOfLaufende(data.getInteger());
    }

    @Override
    public void toCSVSerializableString(CSVCellContent content) {
        // The order is important!
        content.put(isKontra);
        content.put(isRe);
        content.put(isTout);
        content.put(isSie);
        content.put(isSchneider);
        content.put(isSchwarz);
        content.put(numberOfLaufende);
    }

    public boolean isRe() {
        return isRe;
    }

    public void setRe(boolean re) {
        isRe = re;
    }

    public boolean isKontra() {
        return isKontra;
    }

    public void setKontra(boolean kontra) {
        isKontra = kontra;
    }

    public boolean isTout() {
        return isTout;
    }

    public void setTout(boolean tout) {
        isTout = tout;
    }

    public boolean isSie() {
        return isSie;
    }

    public void setSie(boolean sie) {
        isSie = sie;
    }

    public boolean isSchneider() {
        return isSchneider;
    }

    public void setSchneider(boolean schneider) {
        isSchneider = schneider;
    }

    public boolean isSchwarz() {
        return isSchwarz;
    }

    public void setSchwarz(boolean schwarz) {
        isSchwarz = schwarz;
    }

    public int getNumberOfLaufende() {
        return numberOfLaufende;
    }

    public void setNumberOfLaufende(int numberOfLaufende) {
        this.numberOfLaufende = numberOfLaufende;
    }

}
