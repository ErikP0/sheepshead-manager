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


import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Iterator;
import java.util.List;

import sheepshead.manager.game.SingleGameResult;
import sheepshead.manager.session.Session;

public class SessionCSVWriter implements ISessionWriter {

    private final CSVRules rule;
    private String escape;
    private String separator;

    public SessionCSVWriter(CSVRules csvRule) {
        rule = csvRule;
        escape = Character.toString(rule.getEscape());
        separator = Character.toString(rule.getSeparator());
    }

    @Override
    public void writeOut(Session session, OutputStream stream) throws IOException, SessionDataCorruptedException {
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(stream, rule.getEncoding()));
        //Write header
        write(rule.getWriter().writeHeader(session), writer);

        //Write games
        Iterator<SingleGameResult> it = session.iterator();
        while (it.hasNext()) {
            write(rule.getWriter().writeGame(session, it.next()), writer);
        }
    }

    private void write(List<String> row, Writer writer) throws IOException, SessionDataCorruptedException {
        int index = 0;
        for (String cell : row) {
            String pre = index > 0 ? separator : "";
            String post = index < row.size() - 1 ? separator : "";
            if (cell.contains(escape)) {
                throw new SessionDataCorruptedException("The following string contains the control character (" + escape + ") which is not allowed: " + cell);
            }
            if (cell.contains(separator)) {
                pre += escape;
                post += escape;
            }
            writer.write(pre + cell + post);

            index++;
        }
        writer.write(System.getProperty("line.separator"));
    }
}
