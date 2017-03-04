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
 * Exception indicating corrupted, missing, or unexpected data while reading or writing session data
 */
public class SessionDataCorruptedException extends Exception {
    //reflecting all constructors of Exception

    public SessionDataCorruptedException() {
        super();
    }

    public SessionDataCorruptedException(Throwable throwable) {
        super(throwable);
    }

    public SessionDataCorruptedException(String message, Throwable cause) {
        super(message, cause);
    }

    public SessionDataCorruptedException(String message) {
        super(message);
    }
}
