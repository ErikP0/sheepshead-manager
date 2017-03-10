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
import android.support.annotation.Nullable;

import java.util.Collection;

public class CollectionUtils {

    /**
     * Iterates through the given collection and returns the first entry that matches the given predicate
     *
     * @param collection Collection to iterate through
     * @param pred       predicate for matching
     * @param <T>        Type of collection elements
     * @return The first entry that matches the given predicate, if any
     */
    @NonNull
    public static <T> Optional<T> findFirst(@NonNull Collection<T> collection, @NonNull Predicate<T> pred) {
        for (T element : collection) {
            if (pred.evaluate(element)) {
                return Optional.ofValue(element);
            }
        }
        return Optional.empty();
    }

    /**
     * Throws a {@link NullPointerException} when the given argument is null
     *
     * @param t   argument
     * @param <T> Type of argument
     * @return the argument
     * @throws NullPointerException If the argument is null
     */
    @NonNull
    public static <T> T nonNull(@Nullable T t) {
        if (t != null) return t;
        throw new NullPointerException("This parameter may not be null");
    }
}
