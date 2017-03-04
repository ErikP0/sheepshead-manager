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
import java.util.Iterator;
import java.util.List;

import sheepshead.manager.game.SingleGameResult;
import sheepshead.manager.session.Session;

/**
 * A generic .csv writer following the {@link ISessionWriter} interface
 * This writer traverses the {@link Session} and uses a given {@link Writer} to turn session data into
 * an intermediate form that is then transformed into .csv-formatted data by the writer and written out
 */
public class SessionCSVWriter implements ISessionWriter {

    /**
     * The format to follow
     */
    private final CSVFormat format;
    /**
     * The escape character as string
     */
    private String escape;
    /**
     * The separation character as string
     */
    private String separator;
    /**
     * The amount of bytes that were written successfully into the stream
     */
    private int bytesWritten;

    /**
     * Constructs this writer following the given format
     *
     * @param format The format to follow
     */
    public SessionCSVWriter(CSVFormat format) {
        this.format = format;
        escape = Character.toString(this.format.getEscape());
        separator = Character.toString(this.format.getSeparator());
    }

    @Override
    public void writeOut(Session session, OutputStream stream) throws IOException, SessionDataCorruptedException {
        bytesWritten = 0;
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(stream, format.getEncoding()));
        //Write header
        write(format.getWriter().writeHeader(session), writer);

        //Write games
        Iterator<SingleGameResult> it = session.iterator();
        while (it.hasNext()) {
            write(format.getWriter().writeGame(session, it.next()), writer);
        }
        writer.flush();
    }

    private void write(List<String> row, java.io.Writer writer) throws IOException, SessionDataCorruptedException {
        int index = 0;
        for (String cell : row) {
            String pre = index > 0 ? separator : "";
            String post = "";
            if (cell.contains(escape)) {
                throw new SessionDataCorruptedException("The following string contains the control character (" + escape + ") which is not allowed: " + cell);
            }
            if (cell.contains(separator)) {
                pre += escape;
                post += escape;
            }
            String toWrite = pre + cell + post;
            bytesWritten += toWrite.getBytes(format.getEncoding()).length;
            writer.write(toWrite);

            index++;
        }
        writer.write(System.getProperty("line.separator"));
    }

    /**
     * @return The amount of bytes successfully written into the stream
     */
    public int getBytesWritten() {
        return bytesWritten;
    }

    /**
     * Interface for transforming a session into an intermediate form
     */
    public interface Writer {

        /**
         * Called when the header for the csv format should be written out
         *
         * @param session The session to gather data from
         * @return A list of strings each representing the content of one cell
         * @throws SessionDataCorruptedException The implementation may choose to throw this exception when
         *                                       encountering corrupted data or data that does not match the format the implementation expects
         */
        List<String> writeHeader(Session session) throws SessionDataCorruptedException;

        /**
         * Called for every {@link SingleGameResult} in the session
         *
         * @param session The session
         * @param result  The current {@linkplain SingleGameResult}
         * @return A list of strings each representing the content of one cell
         * @throws SessionDataCorruptedException The implementation may choose to throw this exception when
         *                                       encountering corrupted data or data that does not match the format the implementation expects
         */
        List<String> writeGame(Session session, SingleGameResult result) throws SessionDataCorruptedException;
    }
}
