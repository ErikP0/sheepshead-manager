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

package sheepshead.manager.activities;


import android.widget.CheckBox;

import sheepshead.manager.uicontrolutils.ICheckboxGroupStateValidator;

/**
 * Implementation of {@link ICheckboxGroupStateValidator} for a group of two checkboxes
 * with the following rules:
 * <p>
 * <li>Box 2 can only be checked when Box 1 is checked</li>
 * <li>Both boxes can be unchecked</li>
 */
public class FillGameResultCheckboxValidator implements ICheckboxGroupStateValidator {

    @Override
    public boolean isInValidState(boolean[] checkboxesChecked) {
        //if first is not checked, second is checked return false
        //otherwise true
        return !(!checkboxesChecked[0] && checkboxesChecked[1]);
    }

    @Override
    public void putIntoValidState(CheckBox[] checkBoxes, boolean[] checkBoxValues, int lastModified) {
        //only one invalid state exists: box1 = false, box2 = true
        //There are two way to resolve this:
        //1. make box2 = false or 2. make box1 = true
        //the 3rd way (swap values from box 1 & 2) would not be intuitive for the user

        //Under the assumption that the user wants the last modified box to keep its state
        if (lastModified == 0) {
            //user set box1 to false -> use way 1
            checkBoxes[1].setChecked(false);
        } else if (lastModified == 1) {
            //user set box2 to true -> use way 2
            checkBoxes[0].setChecked(true);
        }
    }
}
