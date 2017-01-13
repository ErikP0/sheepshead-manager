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

package sheepshead.manager.session;


import sheepshead.manager.game.GameType;
import sheepshead.manager.serialization.CSVCellContent;
import sheepshead.manager.serialization.ICSVSerializable;

/**
 * The stake class represents the different stake values for the game types and stake modifiers
 */
public class Stake implements ICSVSerializable {

    /**
     * The base value used for Sauspiel and most of the stake modifiers
     */
    private int grundTarif;
    /**
     * The base value used for Solo and Wenz
     */
    private int soloTarif;
    /**
     * The value that is added for each "Laufenden"
     */
    private int laufendeTarif;

    /**
     * Creates a new stake with the given values
     *
     * @param priceSauspiel base value
     * @param priceSolo     value for solo and wenz
     * @param priceLaufende "Laufende" value
     * @throws IllegalArgumentException If any of the values is negative or zero
     */
    public Stake(int priceSauspiel, int priceSolo, int priceLaufende) {
        if (priceSauspiel <= 0 || priceSolo <= 0 || priceLaufende <= 0)
            throw new IllegalArgumentException("Stake must be a positive number");

        this.grundTarif = priceSauspiel;
        this.soloTarif = priceSolo;
        laufendeTarif = priceLaufende;
    }

    public Stake(CSVCellContent.Reader data) {
        this(data.getInteger(), data.getInteger(), data.getInteger());
    }

    @Override
    public void toCSVSerializableString(CSVCellContent content) {
        // The order is important!
        content.put(grundTarif);
        content.put(soloTarif);
        content.put(laufendeTarif);
    }

    /**
     * @return The value for each "Laufenden"
     */
    public int getLaufendeTarif() {
        return laufendeTarif;
    }

    /**
     * @return The value for the base stake
     */
    public int getGrundTarif() {
        return grundTarif;
    }

    /**
     * @return The base value for solo and wenz game types
     */
    public int getSoloTarif() {
        return soloTarif;
    }

    /**
     * @param type game type, may not be LEER
     * @return The base value for the given game type
     * @throws IllegalArgumentException For any unknown game type or invalid game types (like GameType.LEER)
     */
    public int getTarifFor(GameType type) {
        switch (type) {
            case SAUSPIEL:
                return getGrundTarif();
            case WENZ://fall through
            case SOLO:
                return getSoloTarif();
            default:
                throw new IllegalArgumentException("Unknown GameType " + type);
        }
    }
}
