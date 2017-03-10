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


import android.support.annotation.Nullable;

import org.junit.AfterClass;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

import sheepshead.manager.appcore.SheepsheadManagerApplication;
import sheepshead.manager.game.Player;
import sheepshead.manager.game.PlayerRole;
import sheepshead.manager.game.SingleGameResult;
import sheepshead.manager.game.StakeModifier;
import sheepshead.manager.serialization.ISessionReader;
import sheepshead.manager.serialization.ISessionWriter;
import sheepshead.manager.serialization.SessionCSVReader;
import sheepshead.manager.serialization.SessionCSVWriter;
import sheepshead.manager.serialization.SessionDataCorruptedException;
import sheepshead.manager.utils.CollectionUtils;
import sheepshead.manager.utils.Optional;
import sheepshead.manager.utils.Predicate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class InternalSessionSerializationTest {

    private static final String outputPath = "test.csv";

    @AfterClass
    public static void cleanup() {
        //remove the file, if existing
        File f = new File(outputPath);
        if (f.exists()) {
            boolean success = f.delete();
            assertTrue(success);
        }
    }

    @Test
    public void testDummySession() throws IOException, SessionDataCorruptedException {
        Session session = new DummySession();
        FileOutputStream fos = new FileOutputStream(outputPath);
        ISessionWriter writer = new SessionCSVWriter(SheepsheadManagerApplication.INTERNAL_LOAD_SAVE_FORMAT);
        writer.writeOut(session, fos);
        fos.flush();
        fos.close();

        FileInputStream fis = new FileInputStream(outputPath);
        ISessionReader reader = new SessionCSVReader(SheepsheadManagerApplication.INTERNAL_LOAD_SAVE_FORMAT);
        Session loaded = reader.readFrom(fis);
        fis.close();

        //assert that the loaded session is equal

        //players:
        assertEquals(session.getPlayers().size(), loaded.getPlayers().size());
        Iterator<Player> it = session.getPlayers().iterator();
        for (Player p : loaded.getPlayers()) {
            Player expected = it.next();
            assertEquals(expected.getName(), p.getName());
            assertEquals(expected.getSessionMoney(), p.getSessionMoney());
        }

        //stake
        assertEquals(session.getSessionStake().getGrundTarif(), loaded.getSessionStake().getGrundTarif());
        assertEquals(session.getSessionStake().getLaufendeTarif(), loaded.getSessionStake().getLaufendeTarif());
        assertEquals(session.getSessionStake().getSoloTarif(), loaded.getSessionStake().getSoloTarif());

        //game results
        assertEquals(session.getGameAmount(), loaded.getGameAmount());
        Iterator<SingleGameResult> gamesIt = loaded.iterator();
        Iterator<SingleGameResult> expectedGamesIt = session.iterator();
        while (expectedGamesIt.hasNext()) {
            compareSingleGameResult(expectedGamesIt.next(), gamesIt.next());
        }
    }

    private void compareSingleGameResult(SingleGameResult exp, SingleGameResult prov) {
        assertEquals(exp.getGameType(), prov.getGameType());
        Collection<PlayerRole> expectedRoles = exp.getParticipants();
        Collection<PlayerRole> provRoles = prov.getParticipants();
        assertEquals(expectedRoles.size(), provRoles.size());
        for (final PlayerRole expectedRole : expectedRoles) {
            Optional<PlayerRole> providedRole = CollectionUtils.findFirst(provRoles, new Predicate<PlayerRole>() {
                @Override
                public boolean evaluate(@Nullable PlayerRole element) {
                    assertTrue(element != null);
                    return element.getPlayer().getName().equals(expectedRole.getPlayer().getName());
                }
            });
            assertFalse(providedRole.isEmpty());
            comparePlayerRole(expectedRole, providedRole.getValue());
        }

        //stake modifier
        StakeModifier expectedMod = exp.getStakeModifier();
        StakeModifier provMod = prov.getStakeModifier();
        assertEquals(expectedMod.getNumberOfLaufende(), provMod.getNumberOfLaufende());
        assertEquals(expectedMod.isKontra(), provMod.isKontra());
        assertEquals(expectedMod.isSchneider(), provMod.isSchneider());
        assertEquals(expectedMod.isRe(), provMod.isRe());
        assertEquals(expectedMod.isSie(), provMod.isSie());
        assertEquals(expectedMod.isTout(), provMod.isTout());
        assertEquals(expectedMod.isSchwarz(), provMod.isSchwarz());
    }

    private void comparePlayerRole(PlayerRole exp, PlayerRole prov) {
        assertEquals(exp.getPlayer().getName(), prov.getPlayer().getName());
        assertEquals(exp.getMoney(), prov.getMoney());
        assertEquals(exp.getPlayerBalance(), prov.getPlayerBalance());
        assertEquals(exp.isCaller(), prov.isCaller());
        assertEquals(exp.isWinner(), prov.isWinner());
    }
}
