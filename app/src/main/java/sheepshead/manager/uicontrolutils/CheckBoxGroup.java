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


import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.CheckBox;

/**
 * A CheckBoxGroup is group of an arbitrary number of Checkboxes that have some kind of relation
 * with each other. The CheckBoxGroup gets notified whenever a change to one of the Checkboxes occures
 * and then evaluates using the given {@link ICheckboxGroupStateValidator} if the group as whole is
 * in an invalid state (regarding the "checkedness"). The CheckBoxGroup tries to return into a valid
 * state by using {@link ICheckboxGroupStateValidator#putIntoValidState(CheckBox[], boolean[], int)}
 */
public class CheckBoxGroup extends AbstractWidgetGroup<CheckBox> implements View.OnClickListener {

    /**
     * The validator responsible for detecting invalid states and fixing them
     */
    @NonNull
    private final ICheckboxGroupStateValidator validator;

    /**
     * @param stateValidator          A validator for detecting invalid states
     * @param allcheckboxes all Checkboxes that belong to this CheckBoxGroup
     */
    public CheckBoxGroup(@NonNull ICheckboxGroupStateValidator stateValidator, @NonNull CheckBox... allcheckboxes) {
        super(allcheckboxes);
        validator = stateValidator;
        if (allcheckboxes.length <= 0) {
            throw new IllegalArgumentException("At least one " + CheckBox.class.getSimpleName() + " must be given for this group");
        }
    }

    @Override
    protected void addListenerFor(@Nullable CheckBox box) {
        box.setOnClickListener(this);
    }

    @Override
    protected void removeListenerFor(@Nullable CheckBox box) {
        box.setOnClickListener(null);
    }


    @Override
    public void onClick(View v) {
        //v is a CheckBox
        CheckBox box = (CheckBox) v;
        //gather checkbox states
        boolean[] values = new boolean[groupcomponents.length];
        for (int i = 0; i < values.length; i++) {
            values[i] = groupcomponents[i].isChecked();
        }
        //check if in valid state
        if (!validator.isInValidState(values)) {
            //if not -> fix it
            int indexLastModified = find(box);
            validator.putIntoValidState(groupcomponents, values, indexLastModified);
        }
    }
}
