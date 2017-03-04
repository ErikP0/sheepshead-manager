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

package sheepshead.manager.session;


import sheepshead.manager.serialization.CSVCellContent;

/**
 * A interface for csv serialization used in {@link InternalSessionReader}/{@link InternalSessionWriter}.
 * When Implementing this interface, it is usually needed to provide a static factory method or a
 * constructor for constructing an object from a {@link sheepshead.manager.serialization.CSVCellContent.Reader}
 */
public interface ICSVSerializable {

    /**
     * Called for serializing this object.
     * The implementor can fill the given {@linkplain CSVCellContent} with attributes and values that
     * need to be serialized
     *
     * @param content A object where attributes can be stored for serialization
     */
    void toCSVSerializableString(CSVCellContent content);
}
