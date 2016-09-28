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


import android.widget.CheckBox;

/**
 * Validates the state of a CheckBoxGroup and can return the CheckBoxGroup into a valid state
 * if neccessary
 */
public interface ICheckboxGroupStateValidator {

    /**
     * Validates the state of a CheckBoxGroup using the given checked-values of each CheckBox.
     *
     * @param checkboxesChecked the checked-value of every CheckBox in the CheckBoxGroup.
     *                          The order/indexing of these values is the insertion order of the
     *                          CheckBoxes into the CheckBoxGroup
     * @return true if the state is valid, otherwise false
     */
    public boolean isInValidState(boolean[] checkboxesChecked);

    /**
     * Modifies the given checkBoxes in a way that after this call the CheckBoxGroup is considered
     * valid again.
     *
     * @param checkBoxes     All checkboxes of the this group
     * @param checkBoxValues The checked-values from the checkboxes
     * @param lastModified   The index of the CheckBox that was the last one modified. In other words
     *                       the index of the CheckBox that put the group from a valid state into an
     *                       invalid state
     */
    public void putIntoValidState(CheckBox[] checkBoxes, boolean[] checkBoxValues, int lastModified);
}
