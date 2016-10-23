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

import java.util.Collection;
import java.util.List;

import sheepshead.manager.sessionRequirements.Stake;

/**
 * Created by Nicolas on 01.10.2016.
 */

public class SingleGameResult {
    public static final int PLAYERS_PER_GAME = 4;

    /**
     * Wer spielt mit in aktuellem Spiel
     */
    private List<PlayerRole> participants;

    private GameType gameType;

    private StakeModifier stakeModifier;


    /**
     * Bei neuem Spiel: Wer spielt mit, welcher GameType, wer mit wem, wie ging das Spiel aus, welcher Spieler kriegt/verliert wieviel
     */

    public SingleGameResult(List<PlayerRole> playerList, GameType gameType, StakeModifier stakeModifier) {
        if (playerList.size() != PLAYERS_PER_GAME) {
            throw new IllegalArgumentException("The amount of players must exactly be the amount of players per game");
        }
        participants = playerList;

        this.gameType = gameType;

        this.stakeModifier = stakeModifier;

    }

    public void calculate(Stake stake) {
        int winLoseMultiplier = 1;

        final int stakeValue = calculateStakeValue(stake);

        for (PlayerRole player : participants) {
            if (player.isWinner()) {
                winLoseMultiplier = 1;
            } else {
                winLoseMultiplier = -1;
            }

            if (player.isCaller()) {
                player.setMoney(gameType.getTeamMultiplier() * winLoseMultiplier * stakeValue);
            } else {
                player.setMoney(winLoseMultiplier * stakeValue);
            }
        }
    }

    private int calculateStakeValue(Stake stake) {
        int aktStakeValue = stake.getTarifFor(gameType);

        if (stakeModifier.getNumberOfLaufende() >= 3 && (gameType.equals(GameType.SOLO) || gameType.equals(GameType.SAUSPIEL))
                || stakeModifier.getNumberOfLaufende() >= 2 && gameType.equals(GameType.WENZ)) {
            aktStakeValue = aktStakeValue + stakeModifier.getNumberOfLaufende() * stake.getLaufendeTarif();
        }

        //Laut Wiki wird bei Tout und Sie kein Schneider oder Schwarz gerechnet
        if (stakeModifier.isTout()) {
            aktStakeValue = aktStakeValue * 2;
        } else if (stakeModifier.isSie()) {
            aktStakeValue = aktStakeValue * 4;
        } else { //Weder Tout oder Sie werden gespielt,
            if (stakeModifier.isSchneider()) {
                aktStakeValue = aktStakeValue + SheapsheadConstants.GRUNDTARIF;
            }
            if (stakeModifier.isSchwarz()) {
                aktStakeValue = aktStakeValue + SheapsheadConstants.GRUNDTARIF;
            }
        }

        if (stakeModifier.isKontra() && !stakeModifier.isRe()) {
            aktStakeValue = aktStakeValue * 2;
        } else if (stakeModifier.isRe()) {
            aktStakeValue = aktStakeValue * 4;
        }
        return aktStakeValue;

    }

    public Collection<PlayerRole> getParticipants() {
        return participants;
    }
}
