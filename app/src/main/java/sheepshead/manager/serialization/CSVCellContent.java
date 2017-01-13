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

public class CSVCellContent {

    private final char separator;
    private List<String> contents;

    public CSVCellContent(char separator) {
        this.separator = separator;
        contents = new ArrayList<>();
    }

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

    public void put(String s) {
        checkForConsumed();
        checkForSeparator(s);
        contents.add(s);
    }

    public void put(boolean bool) {
        put(Boolean.toString(bool));
    }

    public void put(int i) {
        put(Integer.toString(i));
    }

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

    public Reader getReader() {
        return new Reader();
    }

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

        public String getString() {
            hasNext();
            return it.next();
        }

        public boolean getBoolean() {
            return Boolean.parseBoolean(getString());
        }

        public int getInteger() {
            return Integer.parseInt(getString());
        }
    }
}
