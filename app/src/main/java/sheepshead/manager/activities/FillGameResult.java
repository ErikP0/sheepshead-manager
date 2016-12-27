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
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import sheepshead.manager.R;
import sheepshead.manager.appcore.AbstractBaseActivity;
import sheepshead.manager.appcore.SheepsheadManagerApplication;
import sheepshead.manager.sessionRequirements.Session;
import sheepshead.manager.singleGameRequirements.GameType;
import sheepshead.manager.singleGameRequirements.Player;
import sheepshead.manager.singleGameRequirements.PlayerRole;
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

    /**
     * Key for storing/retrieving the current selected game type when the activity is reloaded
     */
    private static final String bundlekey_selected_game_type = "game_type_selected";
    /**
     * Key for storing/retrieving the index of the laufende spinner when the activity is reloaded
     */
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

    private Spinner dropdownLaufende;


    private Collection<Player> allPlayers;
    private FillGameResultPlayerSelection callerSelection;
    private FillGameResultPlayerSelection nonCallerSelection;

    public FillGameResult() {
        Session session = SheepsheadManagerApplication.getInstance().getCurrentSession();
        if (session == null) {
            throw new IllegalStateException("FillGameResultActivity did not find a session");
        }
        allPlayers = session.getPlayers();
    }


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

        //Create caller & nonCaller Areas
        View callerView = findViewById(R.id.FillGameResult_callerPanel);
        Button editCallerButton = (Button) callerView.findViewById(R.id.FillGameResultPlayerSelection_button);
        editCallerButton.setText(getString(R.string.FillGameResult_editCallers));
        callerSelection = new FillGameResultPlayerSelection(this, callerView, true, allPlayers);

        View nonCallerView = findViewById(R.id.FillGameResult_nonCallerPanel);
        Button editNonCallerButton = (Button) nonCallerView.findViewById(R.id.FillGameResultPlayerSelection_button);
        editNonCallerButton.setText(getString(R.string.FillGameResult_editNonCallers));
        nonCallerSelection = new FillGameResultPlayerSelection(this, nonCallerView, false, allPlayers);
        callerSelection.setOther(nonCallerSelection);
        nonCallerSelection.setOther(callerSelection);

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

        //Create laufende spinner
        dropdownLaufende = findView(R.id.FillGameResult_laufende_dropdown);
        //load state from bundle (if available)
        GameType loadedType = GameType.LEER;
        int loadedIndex = 0;
        if (savedInstanceState != null && savedInstanceState.containsKey(bundlekey_selected_game_type)) {
            loadedType = (GameType) savedInstanceState.getSerializable(bundlekey_selected_game_type);
            loadedIndex = savedInstanceState.getInt(bundlekey_selected_game_type_index);
        }
        switchAdapter(new LaufendeElement.Builder(loadedType).build());
        dropdownLaufende.setSelection(loadedIndex);

        updateCheckboxesForGameType();
        updatePlayerSelectionForGameType();

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
                GameType selectedGameType = getCurrentlySelectedGameType();
                switchAdapter(new LaufendeElement.Builder(selectedGameType).build());
            }
        });

        /**
         * Add a listener that disables "Tout" and "Sie" checkboxes when no game type/"Sauspiel" was selected
         * and disables "Sie" when "Wenz" is selected
         */
        gameTypeGroup.addOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateCheckboxesForGameType();
            }
        });
        gameTypeGroup.addOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updatePlayerSelectionForGameType();
            }
        });

        Button confirm = findView(R.id.FillGameResult_button_confirm);
        //Adds a listener, that will display a message containing the filled in attributes or will
        //display a error message when no game type was selected
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GameType selectedGameType = getCurrentlySelectedGameType();
                if (!selectedGameType.equals(GameType.LEER)) {
                    SingleGameResult result = createSingleGameResult(selectedGameType);
                    String message = "";
                    for (PlayerRole p : result.getParticipants()) {
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

    private void updatePlayerSelectionForGameType() {
        GameType type = getCurrentlySelectedGameType();
        boolean enable = true;
        View callerView = findViewById(R.id.FillGameResult_callerPanel);
        View nonCallerView = findViewById(R.id.FillGameResult_nonCallerPanel);
        if (type.equals(GameType.LEER)) {
            //disable buttons
            enable = false;

        }
        callerSelection.clearSelection();
        nonCallerSelection.clearSelection();

        callerView.findViewById(R.id.FillGameResultPlayerSelection_button).setEnabled(enable);
        nonCallerView.findViewById(R.id.FillGameResultPlayerSelection_button).setEnabled(enable);
    }


    private void updateCheckboxesForGameType() {
        CheckBox checkboxTout = findView(R.id.FillGameResult_checkbox_is_tout);
        CheckBox checkboxSie = findView(R.id.FillGameResult_checkbox_is_sie);

        GameType selectedGameType = getCurrentlySelectedGameType();
        boolean enableTout = true;
        boolean enableSie = true;
        if (selectedGameType.equals(GameType.LEER) || selectedGameType.equals(GameType.SAUSPIEL)) {
            //disable "Tout" and "sie" checkboxes
            enableTout = false;
            enableSie = false;
            checkboxTout.setChecked(false);
            checkboxSie.setChecked(false);
        }
        if (selectedGameType.equals(GameType.WENZ)) {
            //disable "Tout" checkbox
            enableSie = false;
            checkboxSie.setChecked(false);
        }
        checkboxTout.setEnabled(enableTout);
        checkboxSie.setEnabled(enableSie);
    }

    /**
     * Creates and returns a {@link SingleGameResult} based on the selections the user made
     *
     * @param selectedGameType the current selected game type
     * @return the result of the user made selections
     */
    private SingleGameResult createSingleGameResult(GameType selectedGameType) {
        //Temporary, will be removed when selection activity is finished (TODO)
        CheckBox boxCallerSideWon = findView(R.id.FillGameResult_checkbox_callerside_won);
        boolean callerHasWon = boxCallerSideWon.isChecked();
        List<PlayerRole> roles = new ArrayList<>(callerSelection.getSelectedPlayers().size() + nonCallerSelection.getSelectedPlayers().size());
        for (Player caller : callerSelection.getSelectedPlayers()) {
            roles.add(new PlayerRole(caller, true, callerHasWon));
        }
        for (Player nonCaller : nonCallerSelection.getSelectedPlayers()) {
            roles.add(new PlayerRole(nonCaller, false, !callerHasWon));
        }

        StakeModifier modifier = createStakeModifier();
        SingleGameResult result = new SingleGameResult(roles, selectedGameType, modifier);
        return result;
    }

    /**
     * Creates and returns a {@link StakeModifier} instance that contains all the selection the user
     * made regarding point modifiers (schneider/schwarz, kontra/re, tout/sie and Laufende)
     *
     * @return
     */
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

        LaufendeElement selectedElement = (LaufendeElement) dropdownLaufende.getSelectedItem();
        modifier.setNumberOfLaufende(selectedElement.getAnzLaufende());
        return modifier;
    }

    /**
     * Switches the "Laufende"-Dropdown content to the content provided in the given array
     *
     * @param elements the new content for the "Laufende"-Dropdown
     */
    private void switchAdapter(LaufendeElement[] elements) {
        SpinnerAdapter adapter = new ArrayAdapter<LaufendeElement>(this, android.R.layout.simple_dropdown_item_1line, elements);
        dropdownLaufende.setAdapter(adapter);
    }

    /**
     * Returns the current selected {@link GameType}
     *
     * @return
     */
    GameType getCurrentlySelectedGameType() {
        Collection<ToggleButton> pressedButtons = gameTypeGroup.getPressedButtons();
        if (pressedButtons.isEmpty()) {
            return GameType.LEER;
        } else {
            //Get the pressed button
            EnumToggleButton<GameType> pressedButton = (EnumToggleButton<GameType>) pressedButtons.iterator().next();
            return pressedButton.getRepresentation();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle b) {
        super.onSaveInstanceState(b);
        //save current selected game type
        GameType currentGameType = getCurrentlySelectedGameType();
        b.putSerializable(bundlekey_selected_game_type, currentGameType);
        int selectedIndex = dropdownLaufende.getSelectedItemPosition();
        b.putSerializable(bundlekey_selected_game_type_index, selectedIndex);

    }

    private void updatePlayerSelection(Set<Player> selectedPlayers, View playerSelectionView) {
        TextView text = (TextView) playerSelectionView.findViewById(R.id.FillGameResultPlayerSelection_text);
        StringBuilder b = new StringBuilder();
        int index = 0;
        for (Player player : selectedPlayers) {
            if (index > 0) {
                b.append('\n');
            }
            b.append(player.getName());
            index++;
        }
        text.setText(b.toString());
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
