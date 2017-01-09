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

package sheepshead.manager.serialization;


import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import sheepshead.manager.game.GameType;
import sheepshead.manager.game.Player;
import sheepshead.manager.game.PlayerRole;
import sheepshead.manager.game.SingleGameResult;
import sheepshead.manager.game.StakeModifier;
import sheepshead.manager.session.Session;
import sheepshead.manager.session.Stake;

import static org.junit.Assert.assertEquals;

public class SessionCSVReaderWriterTest {
    private static final String lineSeparator = "\r\n";
    private static final String[] players = {"P1", "p2", "p3", "p4"};
    private static final String expected = "header1;header2;header3;\"header;w sep\"" + lineSeparator
            + ";something;\"som;w sep\";\";;;;;;; sahdkg;;;;;dhfjh   \"" + lineSeparator
            + "a;b;c;d" + lineSeparator;

    private static final String[] header = {"header1", "header2", "header3", "header;w sep"};
    private static final String[][] body = {{"", "something", "som;w sep", ";;;;;;; sahdkg;;;;;dhfjh   "}, {"a", "b", "c", "d"}};

    @Test
    public void testReadWriteNormal() throws IOException, SessionDataCorruptedException {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        CSVRules rules = new CSVRules(';', '"', "utf8", false, new FakeSessionWriter(), new FakeSessionReader());
        SessionCSVWriter writer = new SessionCSVWriter(rules);
        Session fakeSession = prepareFakeSession();
        writer.writeOut(fakeSession, stream);
        String s = new String(stream.toByteArray(), rules.getEncoding());
        assertEquals(expected, s);

        ByteArrayInputStream is = new ByteArrayInputStream(stream.toByteArray());
        SessionCSVReader reader = new SessionCSVReader(rules);
        reader.readFrom(is);
    }

    private Session prepareFakeSession() {
        Session fake = new Session(Arrays.asList(players), new Stake(1, 1, 1));
        List<PlayerRole> playerRoles = new ArrayList<>();
        for (Player p : fake.getPlayers()) {
            playerRoles.add(new PlayerRole(p, true, true));
        }
        fake.addGame(new SingleGameResult(playerRoles, GameType.SAUSPIEL, new StakeModifier()));
        fake.addGame(new SingleGameResult(playerRoles, GameType.SAUSPIEL, new StakeModifier()));
        return fake;
    }

    @Test(expected = SessionDataCorruptedException.class)
    public void testReadEmpty() throws IOException, SessionDataCorruptedException {
        ByteArrayInputStream is = new ByteArrayInputStream(new byte[0]);
        SessionCSVReader reader = new SessionCSVReader(new CSVRules());
        reader.readFrom(is);
    }

    private static class FakeSessionWriter implements CSVWrite {
        int i = 0;

        @Override
        public List<String> writeHeader(Session session) throws SessionDataCorruptedException {
            return Arrays.asList(header);
        }

        @Override
        public List<String> writeGame(Session session, SingleGameResult result) throws SessionDataCorruptedException {
            List<String> list = Arrays.asList(body[i]);
            i++;
            return list;
        }
    }

    private static class FakeSessionReader implements CSVRead {
        private List<String> headerList;
        private List<List<String>> bodyList;

        FakeSessionReader() {
            headerList = new ArrayList<>();
            bodyList = new ArrayList<>();
        }

        @Override
        public void readHeader(List<String> headerCellContent) {
            headerList.addAll(headerCellContent);
        }

        @Override
        public void readGame(List<String> rowContent) {
            bodyList.add(new ArrayList<>(rowContent));
        }

        @Override
        public Session buildSession() {
            //perform the test
            assertEquals(header.length, headerList.size());
            for (int i = 0; i < header.length; i++) {
                assertEquals(header[i], headerList.get(i));
            }
            assertEquals(body.length, bodyList.size());
            for (int i = 0; i < body.length; i++) {
                assertEquals(body[i].length, bodyList.get(i).size());
                for (int k = 0; k < body[i].length; k++) {
                    assertEquals(body[i][k], bodyList.get(i).get(k));
                }
            }
            return null;
        }
    }
}
