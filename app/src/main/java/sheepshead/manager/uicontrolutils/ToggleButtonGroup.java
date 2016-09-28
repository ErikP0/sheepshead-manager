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

import android.view.View;
import android.widget.CompoundButton;
import android.widget.ToggleButton;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * A ToggleButtonGroup is a group of ToggleButtons with a relation with each other.
 * The ToggleButtonGroup can have multiple behaviours:
 * {@link ToggleButtonGroup.Type#ALLOW_ONLY_ONE_PRESSED}: Only one button can be ON at the same time.
 * If another button is pressed, all the other buttons are turned OFF
 */
public class ToggleButtonGroup extends AbstractWidgetGroup<ToggleButton> implements CompoundButton.OnCheckedChangeListener {

    /**
     * The current type/behaviour
     */
    private Type grouptype;
    /**
     * Registered Listener that get notified when one of the buttons of the group is clicked
     */
    private List<View.OnClickListener> listener;

    /**
     * Creates a new ToggleButtonGroup using the type behaviour.
     *
     * @param type       The behaviour of this group
     * @param allButtons All buttons that belong to this group
     * @see ToggleButtonGroup.Type
     */
    public ToggleButtonGroup(Type type, ToggleButton... allButtons) {
        super(allButtons);
        if (allButtons.length <= 0) {
            throw new IllegalArgumentException("At least one " + ToggleButton.class.getSimpleName() + " must be given for this group");
        }
        grouptype = type;
        listener = new LinkedList<>();
    }

    /**
     * Registers a {@link android.view.View.OnClickListener} that gets notified when
     * a button of the group is clicked
     *
     * @param l the listener to be added
     */
    public void addOnClickListener(View.OnClickListener l) {
        if (!listener.contains(l)) {
            listener.add(l);
        }
    }

    /**
     * Removes a {@link android.view.View.OnClickListener}.
     * l will no longer get notified when a button of the group is clicked
     *
     * @param l listener to be removed
     */
    public void removeOnClickListener(View.OnClickListener l) {
        listener.remove(l);
    }

    /**
     * Notifies all registered listeners
     *
     * @param buttonPressed
     */
    private void fire(ToggleButton buttonPressed) {
        for (View.OnClickListener l : listener) {
            l.onClick(buttonPressed);
        }
    }

    /**
     * Handles behaviour of type {@link ToggleButtonGroup.Type#ALLOW_ONLY_ONE_PRESSED}
     *
     * @param b         the button that was toggled
     * @param isToggled
     * @see android.widget.CompoundButton.OnCheckedChangeListener#onCheckedChanged(CompoundButton, boolean)
     */
    private void handleAllowOnlyOnePressed(ToggleButton b, boolean isToggled) {
        //Act only if the button was pressed, not released as
        // no pressed button is ok with Type ALLOW_ONLY_ONE_PRESSED
        if (isToggled) {
            int indexRecentlyPressed = find(b);
            if (indexRecentlyPressed >= 0) {
                //unpress all other buttons
                for (int i = 0; i < groupcomponents.length; i++) {
                    if (i != indexRecentlyPressed) {
                        groupcomponents[i].setChecked(false);//untoggle
                    }
                }
            } else {
                throw new IllegalStateException("Button (ID " + b.getId() + ", TextOn "
                        + b.getTextOn() + ", TextOff " + b.getTextOff() +
                        ") is not registered in this " + ToggleButtonGroup.class.getSimpleName());
            }
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        ToggleButton buttonClicked = (ToggleButton) buttonView;
        switch (grouptype) {
            case ALLOW_ONLY_ONE_PRESSED:
                handleAllowOnlyOnePressed(buttonClicked, isChecked);
                break;
            default:
                throw new IllegalStateException("Unknown Grouptype " + grouptype);
        }
        fire(buttonClicked);

    }

    /**
     * Returns all buttons that are pressed at the moment
     *
     * @return
     */
    public Collection<ToggleButton> getPressedButtons() {
        Collection<ToggleButton> result = new ArrayList<>(groupcomponents.length);
        for (ToggleButton button : groupcomponents) {
            if (button.isChecked()) {
                result.add(button);
            }
        }
        return result;
    }

    @Override
    protected void addListenerFor(ToggleButton button) {
        button.setClickable(true);
        button.setOnCheckedChangeListener(this);
    }

    @Override
    protected void removeListenerFor(ToggleButton button) {
        button.setOnCheckedChangeListener(this);
    }

    /**
     * Specifies the behaviour of the ToggleButtonGroup
     */
    public enum Type {
        /**
         * Allows only one button (out of the many in the group) to be ON. If another button is toggled
         * ON, all other buttons except the new one will be toggled OFF
         */
        ALLOW_ONLY_ONE_PRESSED;
    }
}
