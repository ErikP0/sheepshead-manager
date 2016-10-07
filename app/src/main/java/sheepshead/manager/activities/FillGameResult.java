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

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import sheepshead.manager.R;
import sheepshead.manager.appcore.AbstractBaseActivity;
import sheepshead.manager.singleGameRequirements.GameType;
import sheepshead.manager.singleGameRequirements.Player;
import sheepshead.manager.singleGameRequirements.SingleGameResult;
import sheepshead.manager.singleGameRequirements.StakeModifier;
import sheepshead.manager.uicontrolutils.CheckBoxGroup;
import sheepshead.manager.uicontrolutils.DialogUtils;
import sheepshead.manager.uicontrolutils.EnumToggleButton;
import sheepshead.manager.uicontrolutils.ICheckboxGroupStateValidator;
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
     * The group of Checkboxes for the modifiers "Tout" and "Sie".
     * Aa group is required because both checkboxes cannot be checked.
     */
    private CheckBoxGroup toutSieGroup;
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
        toutSieGroup.addListener();
    }

    @Override
    protected void removeActivitySpecificServices() {
        gameTypeGroup.addListener();
        schneiderSchwarzGroup.removeListener();
        kontraReGroup.removeListener();
        toutSieGroup.removeListener();
    }

    @Override
    protected void createUserInterface(Bundle savedInstanceState) {
        setContentView(R.layout.activity_fill_game_result);


        //Create game type button group
        EnumToggleButton<GameType> buttonSauspiel = findView(R.id.FillGameResult_toggleBtn_sauspiel);
        EnumToggleButton<GameType> buttonWenz = findView(R.id.FillGameResult_toggleBtn_wenz);
        EnumToggleButton<GameType> buttonSolo = findView(R.id.FillGameResult_toggleBtn_solo);
        gameTypeGroup = new ToggleButtonGroup(Type.ALLOW_ONLY_ONE_PRESSED, buttonSauspiel, buttonWenz, buttonSolo);

        buttonSauspiel.setRepresentation(GameType.SAUSPIEL);
        buttonWenz.setRepresentation(GameType.WENZ);
        buttonSolo.setRepresentation(GameType.SOLO);

        //Create schneider/schwarz group
        final CheckBox checkboxSchneider = findView(R.id.FillGameResult_checkbox_is_schneider);
        final CheckBox checkboxSchwarz = findView(R.id.FillGameResult_checkbox_is_schwarz);
        schneiderSchwarzGroup = new CheckBoxGroup(new CheckboxValidatorDependentCheckboxes(), checkboxSchneider, checkboxSchwarz);

        //Create kontra/re group
        final CheckBox checkboxKontra = findView(R.id.FillGameResult_checkbox_is_kontra);
        final CheckBox checkboxRe = findView(R.id.FillGameResult_checkbox_is_re);
        kontraReGroup = new CheckBoxGroup(new CheckboxValidatorDependentCheckboxes(), checkboxKontra, checkboxRe);

        //Create tout/sie group
        final CheckBox checkboxTout = findView(R.id.FillGameResult_checkbox_is_tout);
        final CheckBox checkboxSie = findView(R.id.FillGameResult_checkbox_is_sie);
        toutSieGroup = new CheckBoxGroup(new CheckboxValidatorContraryCheckboxes(), checkboxTout, checkboxSie);

        //Create consecutive high trumps spinner
        dropdownLaufende = findView(R.id.FillGameResult_laufende_dropdown);
        entriesLaufendeNormal = createSpinnerAdapter(R.array.array_laufende_normal);
        entriesLaufendeMissingGameType = createSpinnerAdapter(R.array.array_no_gametype_selected);
        entriesLaufendeWenz = createSpinnerAdapter(R.array.array_laufende_wenz);
        dropdownLaufende.setAdapter(entriesLaufendeMissingGameType);//TODO load spinner state

        /*
        This adds a Listener to the game type selection, that fires when a new game type is selected
        or was deselected (-> game type missing)
        The listener adjusts the "laufende" dropdown to match with the new selected game
        type
         */
        gameTypeGroup.addOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //v is a ToggleButton
                EnumToggleButton<GameType> button = (EnumToggleButton<GameType>) v;
                if (!button.isChecked()) {
                    //no game type selected
                    //the ToggleButtonGroup only allows one button to be ON, if this active button
                    //is now turned OFF, no other button is ON -> no game type selected
                    switchAdapter(entriesLaufendeMissingGameType);
                } else if (button.getRepresentation().equals(GameType.WENZ)) {
                    switchAdapter(entriesLaufendeWenz);
                } else {
                    //This branch applies for "Sauspiel" and "Farbsolo"
                    switchAdapter(entriesLaufendeNormal);
                }
            }
        });

        /**
         * Add a listener that disables "Tout" and "Sie" checkboxes when no game type/"Sauspiel" was selected
         */
        gameTypeGroup.addOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EnumToggleButton<GameType> button = (EnumToggleButton<GameType>) v;
                boolean enable = true;
                if (!button.isChecked() || button.getRepresentation().equals(GameType.SAUSPIEL)) {
                    //disable "Tout" and "sie" checkboxes
                    enable = false;
                    checkboxTout.setChecked(false);
                    checkboxSie.setChecked(false);
                }
                checkboxTout.setEnabled(enable);
                checkboxSie.setEnabled(enable);
            }
        });

        final CheckBox checkboxPlayerWon = findView(R.id.FillGameResult_checkbox_callerside_won);

        Button confirm = findView(R.id.FillGameResult_button_confirm);
        //Adds a listener, that will display a message containing the filled in attributes or will
        //display a error message when no game type was selected
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Collection<ToggleButton> gameTypeButtons = gameTypeGroup.getPressedButtons();
                if (!gameTypeButtons.isEmpty()) {
                    EnumToggleButton<GameType> pressedButton = (EnumToggleButton<GameType>) (gameTypeButtons.iterator().next());
                    Player[] players = {new Player("A", 0), new Player("B", 1), new Player("C", 2), new Player("D", 3)};
                    SingleGameResult result = createSingleGameResult(Arrays.asList(players), pressedButton.getRepresentation());
                    String message = "";
                    for (Player p : players) {
                        message += p + "\n";
                    }
                    DialogUtils.showInfoDialog(FillGameResult.this, message, getString(R.string.FillGameResult_confirm_dialog), null);
                } else {
                    //No game type was selected -> Display message to user
                    DialogUtils.showInfoDialog(FillGameResult.this, getString(R.string.FillGameResult_missing_game_type_msg), getString(R.string.FillGameResult_confirm_dialog), null);
                }
            }
        });
    }

    private SingleGameResult createSingleGameResult(List<Player> player, GameType selectedGameType) {
        //Temporary, will be removed when selection activity is finished
        player.get(0).setCaller(true);
        if (selectedGameType.equals(GameType.SAUSPIEL)) {
            player.get(1).setCaller(true);
        }

        CheckBox boxCallerSideWon = findView(R.id.FillGameResult_checkbox_callerside_won);
        for (Player p : player) {
            if (p.isCaller()) {
                p.setHasWon(boxCallerSideWon.isChecked());
            } else {
                p.setHasWon(!boxCallerSideWon.isChecked());
            }
        }

        StakeModifier modifier = createStakeModifier();
        SingleGameResult result = new SingleGameResult(player, selectedGameType, modifier);
        result.setSingleGameResult();
        return result;
    }

    private StakeModifier createStakeModifier() {
        StakeModifier modifier = new StakeModifier();

        CheckBox schneiderBox = findView(R.id.FillGameResult_checkbox_is_schneider);
        modifier.setSchneider(schneiderBox.isChecked());
        CheckBox schwarzBox = findView(R.id.FillGameResult_checkbox_is_schwarz);
        modifier.setSchwarz(schwarzBox.isChecked());

        CheckBox kontraBox = findView(R.id.FillGameResult_checkbox_is_kontra);
        modifier.setKontra(kontraBox.isChecked());
        CheckBox reBox = findView(R.id.FillGameResult_checkbox_is_re);
        modifier.setRe(reBox.isChecked());

        CheckBox toutBox = findView(R.id.FillGameResult_checkbox_is_tout);
        modifier.setTout(toutBox.isChecked());
        CheckBox sieBox = findView(R.id.FillGameResult_checkbox_is_sie);
        modifier.setSie(sieBox.isChecked());
        return modifier;
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

    /**
     * Implementation of {@link ICheckboxGroupStateValidator} for a group of two checkboxes
     * with the following rules:
     * <p>
     * <li>Box 2 can only be checked when Box 1 is checked</li>
     * <li>Both boxes can be unchecked</li>
     */
    private static class CheckboxValidatorDependentCheckboxes implements ICheckboxGroupStateValidator {

        @Override
        public boolean isInValidState(boolean[] checkBoxValues) {
            //if first is not checked, second is checked return false
            //otherwise true
            return !(!checkBoxValues[0] && checkBoxValues[1]);
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

    /**
     * Implementation of {@link ICheckboxGroupStateValidator} for a group of two checkboxes with the
     * following rules:
     * <p>
     * <li>Both boxes cannot be checked</li>
     * <li>Both boxes can be unchecked</li>
     */
    private static class CheckboxValidatorContraryCheckboxes implements ICheckboxGroupStateValidator {
        @Override
        public boolean isInValidState(boolean[] checkBoxValues) {
            //not valid when both boxes are checked
            return !(checkBoxValues[0] && checkBoxValues[1]);
        }

        @Override
        public void putIntoValidState(CheckBox[] checkBoxes, boolean[] checkBoxValues, int lastModified) {
            //only one invalid state exists: box1 = box2 = true
            //Two ways to resolve: 1. box1 => false 2. box2 => false

            //Under the assumption that the user wants to select the last modified box
            if (lastModified == 0) {
                //user checked box1 -> use way 2
                checkBoxes[1].setChecked(false);
            } else if (lastModified == 1) {
                //user checked box2 -> use way 1
                checkBoxes[0].setChecked(false);
            }
        }
    }

}
