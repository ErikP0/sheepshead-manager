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
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.widget.ToggleButton;

public class EnumToggleButton<E extends Enum> extends ToggleButton {

    private E representedEnum;

    public EnumToggleButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public E getRepresentation() {
        return representedEnum;
    }

    public void setRepresentation(@NonNull E representation) {
        representedEnum = representation;
    }
}
