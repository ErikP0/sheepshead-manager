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


import android.content.Context;
import android.util.AttributeSet;
import android.widget.CheckBox;

public class DataCheckBox<T> extends CheckBox {
    private T data;

    public DataCheckBox(Context context) {
        super(context);
    }

    public DataCheckBox(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DataCheckBox(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void put(T newData) {
        data = newData;
    }

    public T getData() {
        return data;
    }

}
