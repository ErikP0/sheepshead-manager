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


import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import sheepshead.manager.session.Session;

public class SessionCSVReader implements ISessionReader {

    private final CSVRules rules;
    private String escape;
    private String separator;

    public SessionCSVReader(CSVRules csvRules) {
        rules = csvRules;
        escape = Character.toString(rules.getEscape());
        separator = Character.toString(rules.getSeparator());
    }

    @Override
    public Session readFrom(InputStream inputStream) throws IOException, SessionDataCorruptedException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, rules.getEncoding()));
        String currentLine = reader.readLine();
        if (currentLine == null) {
            throw new SessionDataCorruptedException("The file is empty!");
        }
        List<String> currentParsedLine = parseLine(currentLine);
        if (currentParsedLine == null) {
            throw new SessionDataCorruptedException("Empty header!");
        }
        rules.getReader().readHeader(currentParsedLine);
        while ((currentLine = reader.readLine()) != null) {
            currentParsedLine = parseLine(currentLine);
            //in the body, empty lines are ok
            if (currentParsedLine != null) {
                rules.getReader().readGame(currentParsedLine);
            }
        }
        reader.close();
        return rules.getReader().buildSession();
    }

    @Nullable
    private List<String> parseLine(String line) throws SessionDataCorruptedException {
        if (line.isEmpty()) {
            return null;
        }
        String[] cells = line.split(separator);
        if (cells.length < 1) {
            return null;
        }
        return merge(cells);
    }

    @NonNull
    private List<String> merge(String[] cells) throws SessionDataCorruptedException {
        List<String> mergedCells = new ArrayList<>();
        String currentCell = "";
        boolean inEscaped = false;
        for (String cellContent : cells) {
            if (inEscaped) {
                if (cellContent.endsWith(escape)) {
                    //add the whole cell group (excludng the escape character at the end)
                    mergedCells.add(currentCell + cellContent.substring(0, cellContent.length() - 1));
                    currentCell = "";
                    inEscaped = false;
                } else {
                    //the separator is cut out by String.split, but was in a escape sequence,
                    //so we have to add it back in
                    currentCell += separator + cellContent;
                }
            } else {
                if (cellContent.startsWith(escape)) {
                    inEscaped = true;
                    currentCell = cellContent.substring(1);
                } else {
                    mergedCells.add(cellContent);
                }
            }
        }
        if (inEscaped) {
            throw new SessionDataCorruptedException("Escape sequence was not closed");
        }
        return mergedCells;
    }
}
