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

public class Stake {

    private int grundTarif;
    private int soloTarif;
    private int laufendeTarif;

    public Stake(int priceSauspiel, int priceSolo, int priceLaufende) {
        this.grundTarif = priceSauspiel;
        this.soloTarif = priceSolo;
        laufendeTarif = priceLaufende;
    }

    public int getLaufendeTarif() {
        return laufendeTarif;
    }

    public int getGrundTarif() {
        return grundTarif;
    }

    public int getSoloTarif() {
        return soloTarif;
    }

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
