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

package sheepshead.manager.utils;


import android.support.annotation.NonNull;

import java.util.Collection;

public class CollectionUtils {

    @NonNull
    public static <T> Optional<T> findFirst(@NonNull Collection<T> collection, @NonNull Predicate<T> pred) {
        for (T element : collection) {
            if (pred.evaluate(element)) {
                return Optional.ofValue(element);
            }
        }
        return Optional.empty();
    }
}
