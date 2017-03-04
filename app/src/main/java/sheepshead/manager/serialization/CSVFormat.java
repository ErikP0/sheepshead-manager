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


/**
 * Specifies the format for .csv files.
 * It contains:<br>
 * <li>A cell separation character</li>
 * <li>An escape character</li>
 * <li>An encoding</li>
 * <li>A Reader and Writer implementation for the use in the generic {@link SessionCSVReader}/{@link SessionCSVWriter}</li>
 */
public class CSVFormat {
    /**
     * The separation character
     */
    private char separator;
    /**
     * The escape character (if the data contains the separator character, the data must be escaped
     * in order to be handled correctly)
     */
    private char escapeChar;
    /**
     * The encoding used in this format
     */
    private String encoding;
    /**
     * Indicates if every cell will be quoted aka escaped
     */
    private boolean quoteEveryCell;
    /**
     * A writer implementation for the use in {@link SessionCSVWriter}
     */
    private SessionCSVWriter.Writer writer;
    /**
     * A reader implementation for the use in {@link SessionCSVReader}
     */
    private SessionCSVReader.Reader reader;

    /**
     * Creates a csv format object with the given parameters
     *
     * @param separator      The separation character
     * @param escapeChar     The escape character
     * @param encoding       The encoding/charset to be used
     * @param quoteEveryCell True if every cell should be escaped
     * @param writer         A writer implementation for {@link SessionCSVWriter}
     * @param reader         A reader implementation for {@link SessionCSVReader}
     */
    public CSVFormat(char separator, char escapeChar, String encoding, boolean quoteEveryCell, SessionCSVWriter.Writer writer, SessionCSVReader.Reader reader) {
        this.separator = separator;
        this.escapeChar = escapeChar;
        this.encoding = encoding;
        this.quoteEveryCell = quoteEveryCell;
        this.writer = writer;
        this.reader = reader;
    }

    /**
     * @return The separation character
     */
    public char getSeparator() {
        return separator;
    }

    /**
     * @return The escape character
     */
    public char getEscape() {
        return escapeChar;
    }

    /**
     * @return The encoding/charset
     */
    public String getEncoding() {
        return encoding;
    }

    /**
     * @return true if every cell is meant to be escaped, otherwise false
     */
    public boolean quoteEveryCell() {
        return quoteEveryCell;
    }

    /**
     * @return The writer implementation
     */
    public SessionCSVWriter.Writer getWriter() {
        return writer;
    }

    /**
     * @return The reader implementation
     */
    public SessionCSVReader.Reader getReader() {
        return reader;
    }
}
