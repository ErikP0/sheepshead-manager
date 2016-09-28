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

package sheepshead.manager.uicontrolutils;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

/**
 * Abstraction for a group of elements.
 * This class provides common functionality like find element's index to avoid code redundancy
 *
 * @param <E> Type of the stores elements
 */
public abstract class AbstractWidgetGroup<E extends View> {

    /**
     * Stored elements
     */
    protected E[] groupcomponents;

    /**
     * Stores the given array and thus makes it available to common functions
     *
     * @param comps elements to store
     */
    public AbstractWidgetGroup(@NonNull E[] comps) {
        groupcomponents = comps;
    }

    /**
     * Adds a specific listener to element
     *
     * @param element
     */
    protected abstract void addListenerFor(@Nullable E element);

    /**
     * Removes the specific listener from element
     *
     * @param element
     */
    protected abstract void removeListenerFor(@Nullable E element);

    /**
     * Returns the index of the given element
     *
     * @param element Element to get the index from
     * @return array-based index of element, or -1 if the element is not contained in this group
     */
    protected int find(@Nullable E element) {
        for (int i = 0; i < groupcomponents.length; i++) {
            //Use == for compare, we want to find the same instance not a similar behaving one
            if (groupcomponents[i] == element) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Adds specific listener to all elements of this group to control behaviour in the intended way.
     * In short: Call this to start the group behaviour
     */
    public void addListener() {
        for (E element : groupcomponents) {
            addListenerFor(element);
        }
    }

    /**
     * Removed specific listeners from all elements of this group that are controling the groups
     * behaviour in the intended way
     * In short: Call this to end the group behaviour
     */
    public void removeListener() {
        for (E element : groupcomponents) {
            removeListenerFor(element);
        }
    }


}
