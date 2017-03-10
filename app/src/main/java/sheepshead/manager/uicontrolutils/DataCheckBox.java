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

package sheepshead.manager.uicontrolutils;


import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.CheckBox;

/**
 * A CheckBox that also contains a value of type T.
 * This value can be put and retrieved.
 *
 * @param <T> The type of value that this CheckBox can store
 */
public class DataCheckBox<T> extends CheckBox {
    /**
     * stored data
     */
    private
    @Nullable
    T data;

    // some constructors for creation by layout inflation
    public DataCheckBox(Context context) {
        super(context);
    }

    public DataCheckBox(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DataCheckBox(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * Puts the given data into the storage of this CheckBox
     *
     * @param newData data to be stored
     */
    public void put(@Nullable T newData) {
        data = newData;
    }

    /**
     * @return The stored data, or null if no data has been put in (yet)
     */
    public
    @Nullable
    T getData() {
        return data;
    }

}
