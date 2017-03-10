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

package sheepshead.manager.serialization;


import java.io.IOException;
import java.io.InputStream;

import sheepshead.manager.session.Session;

/**
 * Interface for describing a reader, i.e. an instance that parses a {@link InputStream} and produces
 * a valid {@link Session}
 */
public interface ISessionReader {

    /**
     * Reads as much data as needed from the given {@link InputStream}, parses the data and returns
     * a valid {@link Session}
     *
     * @param inputStream The stream to read from
     * @return a session build from data of the given {@link InputStream}
     * @throws IOException                   The implementation may choose to throw an
     *                                       {@linkplain IOException} when encountering IO-Problems
     * @throws SessionDataCorruptedException The implementation may choose to throw this exception when
     *                                       encountering corrupted data or data that does not match the format the implementation expects
     */
    Session readFrom(InputStream inputStream) throws IOException, SessionDataCorruptedException;
}
