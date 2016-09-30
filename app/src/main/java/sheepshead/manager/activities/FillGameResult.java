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

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Checkable;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.ToggleButton;

import java.util.Collection;

import sheepshead.manager.R;
import sheepshead.manager.appcore.AbstractBaseActivity;
import sheepshead.manager.uicontrolutils.CheckBoxGroup;
import sheepshead.manager.uicontrolutils.DialogUtils;
import sheepshead.manager.uicontrolutils.ToggleButtonGroup;
import sheepshead.manager.uicontrolutils.ToggleButtonGroup.Type;

/**
 * Activity for selecting game type, who won and additional game modifier (like schneider/schwarz or
 * kontra/re or amount of consecutive high trumps ("Laufende")
 */
public class FillGameResult extends AbstractBaseActivity {

    private static final String bundlekey_selected_game_type = "game_type_selected";
    private static final String bundlekey_selected_game_type_index = "game_type_index";
    /**
     * The button group for selecting the game type.
     * A group is required because there cannot be more than one game type selected
     * Currently available gametpyes: "Sauspiel", "Wenz" and "Farbsolo"
     */
    private ToggleButtonGroup gameTypeGroup;
    /**
     * The group of Checkboxes for the modifiers caused by points ("Schneider" and "Schwarz")
     * A group is required because both checkboxes must always be in a valid state. A invalid state
     * would be schneider=false, schwarz=true: Not possible as scheider is caused when the points are
     * below 30/31, schwarz is caused when points are 0
     */
    private CheckBoxGroup pointModifierGroup;
    /**
     * The group of Checkboxes for the modifiers caused by players ("Kontra" and "Re")
     * A group is required because both checkboxes must always be in a valid state. A invalid state
     * would be kontra=false, re=true: Not possible as "Re" cannot be given without a "Kontra" first
     */
    private CheckBoxGroup playerModifierGroup;
    /**
     * Dropdown list for the consecutive high trumps selection
     * This list only contains one entry claiming no game type was selected
     */
    private SpinnerAdapter consecutiveListMissingGameType;
    /**
     * Dropdown list for the consecutive high trumps selection
     * This list contains entries permitted in a "Sauspiel" or "Farbsolo", so [0, 3-8]
     */
    private SpinnerAdapter consecutiveListNormal;
    /**
     * Dropdown list for the consecutive high trumps selection
     * This list contains entries permitted in a "Wenz", so [2-8]
     */
    private SpinnerAdapter consecutiveListWenz;

    @Override
    protected void registerActivitySpecificServices() {
        gameTypeGroup.addListener();
        pointModifierGroup.addListener();
        playerModifierGroup.addListener();
    }

    @Override
    protected void removeActivitySpecificServices() {
        gameTypeGroup.addListener();
        pointModifierGroup.removeListener();
        playerModifierGroup.removeListener();
    }

    @Override
    protected void createUserInterface(Bundle savedInstanceState) {
        setContentView(R.layout.activity_fill_game_result);


        //Create game type button group
        ToggleButton sau = findView(R.id.FillGameResult_toggleBtn_sauspiel);
        ToggleButton wenz = findView(R.id.FillGameResult_toggleBtn_wenz);
        ToggleButton solo = findView(R.id.FillGameResult_toggleBtn_solo);
        gameTypeGroup = new ToggleButtonGroup(Type.ALLOW_ONLY_ONE_PRESSED, sau, wenz, solo);

        //Create modifier caused by points group
        final CheckBox schneider = findView(R.id.FillGameResult_checkbox_is_schneider);
        final CheckBox schwarz = findView(R.id.FillGameResult_checkbox_is_schwarz);
        pointModifierGroup = new CheckBoxGroup(new FillGameResultCheckboxValidator(), schneider, schwarz);

        //Create modifier caused by players group
        final CheckBox kontra = findView(R.id.FillGameResult_checkbox_is_kontra);
        final CheckBox re = findView(R.id.FillGameResult_checkbox_is_re);
        playerModifierGroup = new CheckBoxGroup(new FillGameResultCheckboxValidator(), kontra, re);

        final CheckBox tout = findView(R.id.FillGameResult_checkbox_is_tout);
        final CheckBox sie = findView(R.id.FillGameResult_checkbox_is_sie);

        //Create consecutive high trumps spinner
        final Spinner consecutiveTrumpsSpinner = findView(R.id.FillGameResult_consecutive_dropdown);
        consecutiveListNormal = createSpinnerAdapter(R.array.array_consecutive_normal);
        consecutiveListMissingGameType = createSpinnerAdapter(R.array.array_no_gametype_selected);
        consecutiveListWenz = createSpinnerAdapter(R.array.array_consecutive_wenz);
        consecutiveTrumpsSpinner.setAdapter(consecutiveListMissingGameType);//TODO load spinner state

        /*
        This adds a Listener to the game type selection, that fires when a new game type is selected
        or was deselected (-> game type missing)
        The listener adjusts the consecutive high trumps dropdown to match with the new selected game
        type
         */
        gameTypeGroup.addOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //v is a ToggleButton
                ToggleButton button = (ToggleButton) v;
                CharSequence text = button.getTextOn();
                if (!button.isChecked()) {
                    //no game type selected
                    //the ToggleButtonGroup only allows one button to be ON, if this active button
                    //is now turned OFF, no other button is ON -> no game type selected
                    switchAdapter(consecutiveListMissingGameType);
                } else if (text.equals(getString(R.string.FillGameResult_wenz))) {
                    switchAdapter(consecutiveListWenz);
                } else {
                    //This branch applies for "Sauspiel" and "Farbsolo"
                    switchAdapter(consecutiveListNormal);
                }
            }

            /**
             * Switches the spinner adapter if needed
             * @param newOne
             */
            private void switchAdapter(SpinnerAdapter newOne) {
                if (consecutiveTrumpsSpinner.getAdapter() != newOne) {
                    consecutiveTrumpsSpinner.setAdapter(newOne);
                }
            }
        });

        gameTypeGroup.addOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToggleButton button = (ToggleButton) v;
                boolean enable = true;
                if (!button.isChecked() || button.getTextOn().equals(getString(R.string.FillGameResult_sauspiel))) {
                    //disable "Tout" and "sie" checkboxes
                    enable = false;
                    tout.setChecked(false);
                    sie.setChecked(false);
                }
                tout.setEnabled(enable);
                sie.setEnabled(enable);
            }
        });

        final CheckBox playerWon = findView(R.id.FillGameResult_checkbox_playerside_won);

        Button confirm = findView(R.id.FillGameResult_button_confirm);
        //Adds a listener, that will display a message containing the filled in attributes or will
        //display a error message when no game type was selected
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Collection<ToggleButton> gameTypeButtons = gameTypeGroup.getPressedButtons();
                if (!gameTypeButtons.isEmpty()) {
                    //TODO insert inputs into GameResult-Model
                    //resolve inputs
                    StringBuilder b = new StringBuilder();
                    b.append("Trage ").append(gameTypeButtons.iterator().next().getTextOn());
                    b.append(" ein.\n");
                    if (playerWon.isChecked()) {
                        b.append("Spieler hat sein Spiel gewonnen.\n");
                    } else {
                        b.append("Spieler hat sein Spiel verloren.\n");
                    }
                    printIf(schneider, b, "Schneider");
                    printIf(schwarz, b, "Schwarz");
                    printIf(kontra, b, "Kontra");
                    printIf(re, b, "Re");
                    printIf(tout, b, "Tout");
                    printIf(sie, b, "Sie");
                    b.append("Anzahl Laufenden: ");
                    b.append(getAmountOfConsecutiveTrumpsSelected(consecutiveTrumpsSpinner));
                    b.append('\n');
                    DialogUtils.showInfoDialog(FillGameResult.this, b.toString(), getString(R.string.FillGameResult_confirm_dialog), null);
                } else {
                    //No game type was selected -> Display message to user
                    DialogUtils.showInfoDialog(FillGameResult.this, getString(R.string.FillGameResult_missing_game_type_msg), getString(R.string.FillGameResult_confirm_dialog), null);
                }
            }
        });
    }

    /**
     * Creates and returns a SpinnerAdapter from a resource id.
     *
     * @param id Resource id (should be a textArrayId)
     * @return ArrayAdapter, created using resources from the given id
     */
    private SpinnerAdapter createSpinnerAdapter(int id) {
        return ArrayAdapter.createFromResource(this, id, android.R.layout.simple_spinner_dropdown_item);
    }

    /**
     * Prints the message msg into the given b, only if the given Checkbox is checked
     *
     * @param condition
     * @param b
     * @param msg
     */
    private void printIf(@NonNull Checkable condition, @NonNull StringBuilder b, @Nullable String msg) {
        if (condition.isChecked()) {
            b.append(msg);
            b.append('\n');
        }
    }

    /**
     * Returns the amount of consecutive high trumps selected
     *
     * @param s The spinner to select from
     * @return
     */
    private int getAmountOfConsecutiveTrumpsSelected(@NonNull Spinner s) {
        String value = (String) s.getSelectedItem();
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException ignore) {
            return 0;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle b) {
        super.onSaveInstanceState(b);
        //TODO save spinner state

    }
}
