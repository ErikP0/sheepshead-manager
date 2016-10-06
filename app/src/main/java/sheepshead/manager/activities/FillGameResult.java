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
    private CheckBoxGroup schneiderSchwarzGroup;
    /**
     * The group of Checkboxes for the modifiers caused by players ("Kontra" and "Re")
     * A group is required because both checkboxes must always be in a valid state. A invalid state
     * would be kontra=false, re=true: Not possible as "Re" cannot be given without a "Kontra" first
     */
    private CheckBoxGroup kontraReGroup;
    /**
     * Dropdown list for the "Laufende" selection
     * This list only contains one entry claiming no game type was selected
     */
    private SpinnerAdapter entriesLaufendeMissingGameType;
    /**
     * Dropdown list for the "Laufende" selection
     * This list contains entries permitted in a "Sauspiel" or "Farbsolo", so [0, 3-8]
     */
    private SpinnerAdapter entriesLaufendeNormal;
    /**
     * Dropdown list for the "Laufende" selection
     * This list contains entries permitted in a "Wenz", so [2-4]
     */
    private SpinnerAdapter entriesLaufendeWenz;

    private Spinner dropdownLaufende;

    @Override
    protected void registerActivitySpecificServices() {
        gameTypeGroup.addListener();
        schneiderSchwarzGroup.addListener();
        kontraReGroup.addListener();
    }

    @Override
    protected void removeActivitySpecificServices() {
        gameTypeGroup.addListener();
        schneiderSchwarzGroup.removeListener();
        kontraReGroup.removeListener();
    }

    @Override
    protected void createUserInterface(Bundle savedInstanceState) {
        setContentView(R.layout.activity_fill_game_result);


        //Create game type button group
        ToggleButton buttonSauspiel = findView(R.id.FillGameResult_toggleBtn_sauspiel);
        ToggleButton buttonWenz = findView(R.id.FillGameResult_toggleBtn_wenz);
        ToggleButton buttonSolo = findView(R.id.FillGameResult_toggleBtn_solo);
        gameTypeGroup = new ToggleButtonGroup(Type.ALLOW_ONLY_ONE_PRESSED, buttonSauspiel, buttonWenz, buttonSolo);

        //Create schneider/schwarz group
        final CheckBox checkboxSchneider = findView(R.id.FillGameResult_checkbox_is_schneider);
        final CheckBox checkboxSchwarz = findView(R.id.FillGameResult_checkbox_is_schwarz);
        schneiderSchwarzGroup = new CheckBoxGroup(new FillGameResultCheckboxValidator(), checkboxSchneider, checkboxSchwarz);

        //Create kontra/re group
        final CheckBox checkboxKontra = findView(R.id.FillGameResult_checkbox_is_kontra);
        final CheckBox checkboxRe = findView(R.id.FillGameResult_checkbox_is_re);
        kontraReGroup = new CheckBoxGroup(new FillGameResultCheckboxValidator(), checkboxKontra, checkboxRe);

        //Create consecutive high trumps spinner
        dropdownLaufende = findView(R.id.FillGameResult_laufende_dropdown);
        entriesLaufendeNormal = createSpinnerAdapter(R.array.array_laufende_normal);
        entriesLaufendeMissingGameType = createSpinnerAdapter(R.array.array_no_gametype_selected);
        entriesLaufendeWenz = createSpinnerAdapter(R.array.array_laufende_wenz);
        dropdownLaufende.setAdapter(entriesLaufendeMissingGameType);//TODO load spinner state

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
                    switchAdapter(entriesLaufendeMissingGameType);
                } else if (text.equals(getString(R.string.FillGameResult_wenz))) {
                    switchAdapter(entriesLaufendeWenz);
                } else {
                    //This branch applies for "Sauspiel" and "Farbsolo"
                    switchAdapter(entriesLaufendeNormal);
                }
            }
        });

        final CheckBox checkboxTout = findView(R.id.FillGameResult_checkbox_is_tout);
        final CheckBox checkboxSie = findView(R.id.FillGameResult_checkbox_is_sie);

        /**
         * Add a listener that disables "Tout" and "Sie" checkboxes when no game type/"Sauspiel" was selected
         */
        gameTypeGroup.addOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToggleButton button = (ToggleButton) v;
                boolean enable = true;
                if (!button.isChecked() || button.getTextOn().equals(getString(R.string.FillGameResult_sauspiel))) {
                    //disable "Tout" and "sie" checkboxes
                    enable = false;
                    checkboxTout.setChecked(false);
                    checkboxSie.setChecked(false);
                }
                checkboxTout.setEnabled(enable);
                checkboxSie.setEnabled(enable);
            }
        });

        final CheckBox checkboxPlayerWon = findView(R.id.FillGameResult_checkbox_playerside_won);

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
                    if (checkboxPlayerWon.isChecked()) {
                        b.append("Spieler hat sein Spiel gewonnen.\n");
                    } else {
                        b.append("Spieler hat sein Spiel verloren.\n");
                    }
                    printIf(checkboxSchneider, b, "Schneider");
                    printIf(checkboxSchwarz, b, "Schwarz");
                    printIf(checkboxKontra, b, "Kontra");
                    printIf(checkboxRe, b, "Re");
                    printIf(checkboxTout, b, "Tout");
                    printIf(checkboxSie, b, "Sie");
                    b.append("Anzahl Laufenden: ");
                    b.append(getAmountOfConsecutiveTrumpsSelected(dropdownLaufende));
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
     * Switches the spinner adapter if needed
     *
     * @param newOne
     */
    private void switchAdapter(SpinnerAdapter newOne) {
        if (dropdownLaufende.getAdapter() != newOne) {
            dropdownLaufende.setAdapter(newOne);
        }
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
