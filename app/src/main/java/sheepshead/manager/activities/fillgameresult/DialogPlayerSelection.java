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

package sheepshead.manager.activities.fillgameresult;


import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import sheepshead.manager.R;
import sheepshead.manager.game.GameType;
import sheepshead.manager.game.Player;
import sheepshead.manager.uicontrolutils.DataCheckBox;

/**
 * Controller class for the player selection dialog in FillGameResultActivity.
 * This class creates a dialog that is closed after the user checked the correct amount of players
 * to participate on the given side (caller side or non caller side).
 * E.g. When game mode Wenz is selected, the caller side dialog closes after 1 selected player; the
 * non-caller side dialog closes after 3 selected players.
 * If there are in total 4 players available, selecting players to participate in one side will cause
 * the other side to be automatically filled with the remaining players (as their selection would be
 * the next action). If there are more than 4 players available the automatic filling might not be the
 * desired behaviour and is therefore not used.
 */
class DialogPlayerSelection implements IPlayerSelection, DialogInterface.OnDismissListener {

    /**
     * Indicates if this selection is for caller or non-caller side
     */
    private final boolean isCallingSide;
    /**
     * The bundle key for storing and loading the selected players in case of a context change
     */
    private final String loadStoreKey;
    //UI elements
    private final TextView text;
    private final Button button;
    private final String noPlayerSelectedText;
    private final String needMorePlayersText;

    /**
     * The other selection controller (is needed for auto filling)
     */
    private DialogPlayerSelection otherSelection;
    /**
     * The dialog, or null when it is not currently displayed
     */
    private
    @Nullable
    Dialog dialog;

    /**
     * All available players. These are used to build the options in the dialog
     */
    private Collection<Player> availablePlayers;
    /**
     * The currently selected players
     */
    private Set<Player> selectedPlayers;
    /**
     * The currently selected game type
     */
    private GameType selectedGameType;

    /**
     * Creates a new controller for a player selection dialog
     *
     * @param isCaller         determines the side (true if caller side; false if non-caller side)
     * @param bundleKey        A key for storing and loading the selection
     * @param view             The view with a button and a TextView corresponding to this controller
     * @param noPlayerSelected String to display in the TextView when no player is currently selected
     * @param needMorePlayers  String to display in the TextView when more players need to be selected
     */
    DialogPlayerSelection(boolean isCaller, String bundleKey, View view, String noPlayerSelected, String needMorePlayers) {
        isCallingSide = isCaller;
        loadStoreKey = bundleKey;
        noPlayerSelectedText = noPlayerSelected;
        needMorePlayersText = needMorePlayers;
        text = (TextView) view.findViewById(R.id.FillGameResultPlayerSelection_text);
        button = (Button) view.findViewById(R.id.FillGameResultPlayerSelection_button);
        selectedPlayers = new HashSet<>();
    }

    /**
     * Updates the TextView element with the currently selected players, possible more needed players,
     * or a message indicating that no players were selected
     */
    private void updateText() {
        StringBuilder builder = new StringBuilder();
        if (selectedPlayers.isEmpty()) {
            builder.append(noPlayerSelectedText);
        } else {
            Iterator<Player> it = selectedPlayers.iterator();
            builder.append(it.next().getName());
            while (it.hasNext()) {
                builder.append(',').append(it.next().getName());
            }

            if (computeRemainingPlayers() > 0) {
                builder.append('\n').append(needMorePlayersText);
            }
        }
        text.setText(builder.toString());
    }

    /**
     * Sets the other controller for features like auto filling
     *
     * @param other Other dialog controller
     */
    void setOther(@NonNull DialogPlayerSelection other) {
        otherSelection = other;
    }

    @Override
    public void addPlayer(Player player) {
        selectedPlayers.add(player);
        updateText();
    }

    @Override
    public void removePlayer(Player player) {
        selectedPlayers.remove(player);
        updateText();
    }

    @Override
    public void setAvailablePlayers(Collection<Player> available) {
        availablePlayers = available;
    }

    @Override
    public Set<Player> getSelectedPlayers() {
        return selectedPlayers;
    }

    @Override
    public void load(Bundle savedInstanceState) {
        String[] selected = savedInstanceState.getStringArray(loadStoreKey);
        for (String name : selected) {
            for (Player p : availablePlayers) {
                if (p.getName().equals(name)) {
                    addPlayer(p);
                    break;
                }
            }
        }
    }

    @Override
    public void store(Bundle savedInstanceState) {
        String[] selected = new String[selectedPlayers.size()];
        int i = 0;
        for (Player p : selectedPlayers) {
            selected[i] = p.getName();
            i++;
        }
        savedInstanceState.putStringArray(loadStoreKey, selected);
    }

    @Override
    public void onGameTypeSelectionChange(GameType newGameType) {
        if (selectedGameType == null || !selectedGameType.equals(newGameType)) {
            selectedGameType = newGameType;
            reset();
        }
        boolean disable = newGameType.equals(GameType.LEER);
        button.setEnabled(!disable);
    }

    /**
     * Resets (clears) the selected players
     */
    private void reset() {
        selectedPlayers.clear();
        updateText();
    }

    @Override
    public void tryAutoFill(IPlayerSelection otherSelection) {
        //player edited the other selection -> selections here may be invalid/unwanted
        reset();
        int remaining = computeRemainingPlayers();
        if (availablePlayers.size() - otherSelection.getSelectedPlayers().size() == remaining) {
            //auto fill is possible
            Set<Player> alreadySelected = otherSelection.getSelectedPlayers();
            for (Player p : availablePlayers) {
                if (!alreadySelected.contains(p)) {
                    addPlayer(p);
                }
            }
        }
    }

    /**
     * @return The amount of players that the user needs to select before the dialog can be closed
     * automatically
     */
    private int computeRemainingPlayers() {
        int max = isCallingSide ? selectedGameType.getNumberOfCallers() : 4 - selectedGameType.getNumberOfCallers();
        return max - selectedPlayers.size();
    }

    /**
     * Closes the dialog if enough players are selected
     */
    private void evaluateClose() {
        if (computeRemainingPlayers() <= 0 && dialog != null) {
            dialog.dismiss();
        }
    }

    /**
     * Builds and displays a dialog where the user can select participating players for the corresponding side
     *
     * @param activity Needed for inflating the dialog layout
     */
    void showDialog(Activity activity) {
        LayoutInflater infalInflater = (LayoutInflater) activity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ViewGroup dialogPanel = (ViewGroup) infalInflater.inflate(R.layout.player_selection_dialog, null);

        TextView text = (TextView) dialogPanel.findViewById(R.id.PlayerSelectionDialog_title);
        text.setText("Wähle " + computeRemainingPlayers() + " Spieler");

        LinearLayout players = new LinearLayout(activity);
        players.setOrientation(LinearLayout.VERTICAL);
        dialogPanel.addView(players);
        for (final Player player : availablePlayers) {
            if (!otherSelection.getSelectedPlayers().contains(player)) {
                DataCheckBox<Player> playerBox = (DataCheckBox<Player>) infalInflater.inflate(R.layout.player_selection_checkbox, null);
                playerBox.setText(player.getName());
                playerBox.put(player);
                playerBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            addPlayer(player);
                        } else {
                            removePlayer(player);
                        }
                        evaluateClose();
                    }
                });
                players.addView(playerBox);
            }
        }
        dialog = new AlertDialog.Builder(activity).setView(dialogPanel).create();
        dialog.setOnDismissListener(this);
        dialog.show();
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        dialog = null;
        otherSelection.tryAutoFill(this);
    }
}
