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

package sheepshead.manager.utils;


import android.support.annotation.NonNull;

/**
 * Simple, naive implementation of an optional value.
 * As it is not in the android library, it is added here
 *
 * @param <T>
 */
public class Optional<T> {
    private boolean isEmpty;
    private T value;

    private Optional(T value) {
        isEmpty = false;
        this.value = value;
    }

    private Optional() {
        isEmpty = true;
        value = null;
    }

    @NonNull
    public static <T> Optional<T> empty() {
        return new Optional<T>();
    }

    @NonNull
    public static <T> Optional<T> ofValue(T value) {
        return new Optional<>(value);
    }

    @NonNull
    public static <T> Optional<T> ofNullable(T value) {
        return value == null ? Optional.<T>empty() : ofValue(value);
    }

    public T getValue() {
        if (isEmpty) {
            throw new IllegalStateException("Optional is empty");
        }
        return value;
    }

    public boolean isEmpty() {
        return isEmpty;
    }


}
