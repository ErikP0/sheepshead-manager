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


import java.io.IOException;
import java.io.OutputStream;

import sheepshead.manager.session.Session;

/**
 * Interface for describing a writer, i.e. an instance that takes {@link Session} and writes data
 * to a {@link OutputStream}
 */
public interface ISessionWriter {

    /**
     * Uses the given session to write data to the given {@link OutputStream}
     *
     * @param session The session to write data from
     * @param stream  The stream to write data into
     * @throws IOException                   The implementation may choose to throw an
     *                                       {@linkplain IOException} when encountering IO-Problems
     * @throws SessionDataCorruptedException The implementation may choose to throw this exception when
     *                                       encountering corrupted data or data that does not match the format the implementation expects
     */
    void writeOut(Session session, OutputStream stream) throws IOException, SessionDataCorruptedException;
}
