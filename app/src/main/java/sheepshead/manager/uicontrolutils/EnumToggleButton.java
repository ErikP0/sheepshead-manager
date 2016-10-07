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

/**
 * A ToggleButton that represents a certain Enum value
 * <p>
 * A small note about generics in this case:
 * When this button is used in a layout.xml, magic happens and the constructor is called.
 * As the android magic does not know the type of the generic parameter, the type will be the
 * highest concrete class (therefore Enum). This means that any Enum type can be stored in this
 * button, more specifically different enum types can be stored (one after the other) in one instance
 * of this class.
 * Care must be taken when storing/retrieving enums as the enum types of the stored and retrieved enums
 * in the same button instance is not compiler-enforced and therefore can cause
 * {@link ClassCastException} or other runtime exceptions.
 * <p>
 * <pre><code>
 * EnumToggleButton{@literal <}SomeEnumType> b1 = find(id);
 * b1.setRepresentation(SomeEnumType.VALUE);
 * EnumToggleButton{@literal <}OtherEnumType> b2 = find(id);
 * b2.getRepresentation();//expect a ClassCastException here
 * </code></pre>
 * <code>b2.getRepresentation()</code> performs an implicit cast to its TypeParameter (OtherEnumType)
 * So the generic typing of this Button is more a cast helper (one does not have to explicitly cast
 * every time when using {@link #getRepresentation()}).
 *
 * @param <E> The enum type this button should represent a value of
 */
public class EnumToggleButton<E extends Enum> extends ToggleButton {

    private E representedEnum;

    //A constructor enforced by ToggleButton to be used in a layout.xml
    public EnumToggleButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * @return the stored enum value this button is representing
     */
    public E getRepresentation() {
        return representedEnum;
    }

    /**
     * Stores a representing enum value for this button
     *
     * @param representation
     */
    public void setRepresentation(@NonNull E representation) {
        representedEnum = representation;
    }
}
