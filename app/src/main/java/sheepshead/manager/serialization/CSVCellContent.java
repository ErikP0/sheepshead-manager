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


import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

/**
 * A CSVCellContent represents the content of one cell in the csv format.
 * Each cell may contain zero, one or more entries. These entries are separated using a given separator.
 * <br>Use Case: Fill CSVCell<br>
 * Use {@link #put(boolean)},{@link #put(int)} and {@link #put(String)} to fill the cell,<br>
 * Use {@link #consume()} to get a String containing all entries and necessary separators
 * <br>Use Case: Get CSVCell entries<br>
 * Use {@link #CSVCellContent(String, char)} to parse the content of the given string,<br>
 * Use {@link #getReader()} and {@link Reader#getBoolean()}, {@link Reader#getInteger()}, {@link Reader#getString()}
 * to retrieve the data
 */
public class CSVCellContent {

    /**
     * The secondary separator, separating each cell entry inside of one cell
     */
    private final char separator;
    private List<String> contents;

    /**
     * Creates an empty CSVCellContent with the given separator
     *
     * @param separator A separation character
     */
    public CSVCellContent(char separator) {
        this.separator = separator;
        contents = new ArrayList<>();
    }

    /**
     * Creates a CSVCellContent from the given string
     *
     * @param data      entries separated by the given separator
     * @param separator a separation character
     */
    public CSVCellContent(String data, char separator) {
        this(separator);
        String[] parsed = data.split(Pattern.quote(Character.toString(this.separator)));
        Collections.addAll(contents, parsed);
    }

    private void checkForConsumed() {
        if (contents == null) {
            throw new IllegalStateException("This " + CSVCellContent.class.getSimpleName() + " has already been consumed");
        }
    }

    private void checkForSeparator(String s) {
        if (s.contains(Character.toString(separator))) {
            throw new IllegalArgumentException("Argument " + s + " contains illegal control character '" + separator + "'");
        }
    }

    /**
     * Pushes a string onto this cell content
     *
     * @param s string to be pushed
     */
    public void put(String s) {
        checkForConsumed();
        checkForSeparator(s);
        contents.add(s);
    }

    /**
     * Pushes a boolean onto this cell content
     *
     * @param bool boolean to be pushed
     */
    public void put(boolean bool) {
        put(Boolean.toString(bool));
    }

    /**
     * Pushes an integer onto this cell content
     *
     * @param i integer to be pushed
     */
    public void put(int i) {
        put(Integer.toString(i));
    }

    /**
     * Consumes, i.e. joins the pushed entries into one string.
     * After consumption, this object may not be used
     *
     * @return a string containing all pushed entries
     */
    public String consume() {
        StringBuilder builder = new StringBuilder();
        Iterator<String> it = contents.iterator();
        if (it.hasNext()) {
            builder.append(it.next());
            while (it.hasNext()) {
                builder.append(separator);
                builder.append(it.next());
            }
        }
        contents = null;
        return builder.toString();
    }

    /**
     * @return A reader for this cell content
     */
    public Reader getReader() {
        return new Reader();
    }

    /**
     * Class to access the contents of a {@link CSVCellContent}-Object.
     */
    public class Reader {
        private Iterator<String> it;

        private Reader() {
            it = contents.iterator();
        }

        private void hasNext() {
            if (!it.hasNext()) {
                throw new IllegalStateException("No next content is available");
            }
        }

        /**
         * @return The next entry on the stack as a string
         * @throws IllegalStateException If there is no next entry
         */
        public String getString() {
            hasNext();
            return it.next();
        }

        /**
         * The next entry is parsed using {@link Boolean#parseBoolean(String)}
         *
         * @return the next entry on the stack as a boolean value
         * @throws IllegalStateException If there is no next entry
         */
        public boolean getBoolean() {
            return Boolean.parseBoolean(getString());
        }

        /**
         * @return the next entry on the stack as an integer value
         * @throws IllegalStateException If there is no next entry
         * @throws NumberFormatException If the entry cannot be converted to integer
         */
        public int getInteger() {
            return Integer.parseInt(getString());
        }
    }
}
