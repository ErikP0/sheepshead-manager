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


import sheepshead.manager.session.InternalSessionReader;
import sheepshead.manager.session.InternalSessionWriter;

public class CSVRules {


    private char separator;
    private char escapeChar;
    private String encoding;
    private boolean quoteEveryCell;
    private CSVWrite writer;
    private CSVRead reader;

    public CSVRules() {
        this(';', '"', "utf8", false, null, null);
    }

    public CSVRules(char separator, char escapeChar, String encoding, boolean quoteEveryCell, CSVWrite writer, CSVRead reader) {
        this.separator = separator;
        this.escapeChar = escapeChar;
        this.encoding = encoding;
        this.quoteEveryCell = quoteEveryCell;
        this.writer = writer;
        this.reader = reader;
    }

    public char getSeparator() {
        return separator;
    }

    public char getEscape() {
        return escapeChar;
    }

    public String getEncoding() {
        return encoding;
    }

    public boolean quoteEveryCell() {
        return quoteEveryCell;
    }

    public CSVWrite getWriter() {
        return writer;
    }

    public CSVRead getReader() {
        return reader;
    }
}
