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

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import sheepshead.manager.R;
import sheepshead.manager.appcore.AbstractBaseActivity;
import sheepshead.manager.appcore.SheepsheadManagerApplication;
import sheepshead.manager.game.Player;
import sheepshead.manager.session.Session;
import sheepshead.manager.session.Stake;
import sheepshead.manager.uicontrolutils.AbstractListItem;
import sheepshead.manager.uicontrolutils.DialogUtils;

/**
 * Activity for selecting price values for "Sauspiel" and "Solo" and for selecting all players that
 * should be part of the new created session
 */
public class CreateSession extends AbstractBaseActivity {

    /**
     * List index where to find the stake selection
     */
    private int STAKE_INDEX;
    /**
     * List index where to find the player selection
     */
    private int PLAYER_INDEX;

    /**
     * A listener for the "Add-Player" button, on click, it shows a dialog where the name of the player
     * can be written into
     */
    private AddNewPlayerListener addNewPlayerListener;
    /**
     * A listener for both stake selections ("Sauspiel & "Solo")
     */
    private StakeInputWatcher stakeInputWatcher;

    /**
     * A list of currently added players
     */
    private List<Player> players;
    /**
     * The list adapter
     */
    private CreateSessionListAdapter adapter;

    private Button btnCreateSession;

    public CreateSession() {
        addNewPlayerListener = new AddNewPlayerListener();
        stakeInputWatcher = new StakeInputWatcher();
        players = new ArrayList<>();
    }

    @Override
    protected void registerActivitySpecificServices() {

    }

    @Override
    protected void removeActivitySpecificServices() {

    }

    @Override
    protected void createUserInterface(Bundle savedInstanceState) {
        setContentView(R.layout.activity_create_session);

        //build the expandable list & fill it with content
        CreateSessionListAdapter.Builder builder = new CreateSessionListAdapter.Builder(this);
        STAKE_INDEX = builder.addItem(getString(R.string.CreateSession_header_stake), new StakeSelectionListItem());
        PLAYER_INDEX = builder.addItem(getString(R.string.CreateSession_header_players), new PlayerSelectionListItem());

        adapter = builder.build();

        ExpandableListView listView = findView(R.id.CreateSession_listView);
        listView.setAdapter(adapter);

        //Add the stake selection listener to the stake selection EditTexts
        View inputStakeView = adapter.getItemView(STAKE_INDEX);
        EditText inputSauspiel = (EditText) inputStakeView.findViewById(R.id.CreateSession_input_sauspiel);
        EditText inputSolo = (EditText) inputStakeView.findViewById(R.id.CreateSession_input_solo);
        inputSauspiel.addTextChangedListener(stakeInputWatcher);
        inputSolo.addTextChangedListener(stakeInputWatcher);

        //Add the add player listener to the matching button
        View addPlayerView = adapter.getItemView(PLAYER_INDEX);
        ImageButton addButton = (ImageButton) addPlayerView.findViewById(R.id.CreateSession_AddPlayer);
        addButton.setOnClickListener(addNewPlayerListener);

        btnCreateSession = findView(R.id.CreateSession_btn_create);
        btnCreateSession.setOnClickListener(new CreateSessionAction());

        if (savedInstanceState != null) {
            //load (if possible) selected states
            adapter.loadInstanceState(savedInstanceState);
        }
        revalidateIcons();
    }

    /**
     * Attempts to add a new player with <code>name</code>.
     * Is a player with the same name already added, the user gets notifier via Dialog and the new
     * player is not added.
     *
     * @param name The name of the player
     */
    private void addNewPlayer(String name) {
        if (name.equalsIgnoreCase("")) {
            DialogUtils.showInfoDialog(this, getString(R.string.CreateSession_warning_invalid_player_name), getString(R.string.FillGameResult_confirm_dialog), null);
            return;//exit
        }

        //add player if not already there
        for (Player player : players) {
            if (player.getName().equalsIgnoreCase(name)) {
                DialogUtils.showInfoDialog(this, getString(R.string.CreateSession_warning_player_already_exists), getString(R.string.FillGameResult_confirm_dialog), null);
                return;//exit
            }
        }

        players.add(new Player(name, players.size()));//TODO richtige ID vergeben
        updatePlayerlist();
    }

    /**
     * Removes a player with <code>name</code>
     *
     * @param name The name of the player to remove from the list
     */
    private void removePlayer(String name) {
        ListIterator<Player> it = players.listIterator();
        while (it.hasNext()) {
            Player next = it.next();
            if (next.getName().equalsIgnoreCase(name)) {
                it.remove();
            }
        }
        updatePlayerlist();
    }

    /**
     * Changes the player selection view to match to the internal player list
     */
    private void updatePlayerlist() {
        LinearLayout list = (LinearLayout) adapter.getItemView(PLAYER_INDEX).findViewById(R.id.CreateSession_playerlist);
        list.removeAllViews();//remove old player-entries
        for (Player player : players) {
            //create a new player-entry
            LayoutInflater infalInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View child = infalInflater.inflate(R.layout.create_session_playerlist_row, null);
            TextView textPlayername = (TextView) child.findViewById(R.id.CreateSession_playername);
            final String name = player.getName();
            textPlayername.setText(name);

            //add listener to remove button of this player-entry
            ImageButton removePlayerButton = (ImageButton) child.findViewById(R.id.CreateSession_RemovePlayer);
            removePlayerButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    removePlayer(name);
                }
            });

            //add this player-entry
            list.addView(child);
        }
        revalidateIcons();
    }

    /**
     * Updates all state icons of each selection. If all selections are valid, the create new session
     * button is enabled
     */
    private void revalidateIcons() {
        boolean inputValidForCreation = true;

        //validate input of sauspiel & solo
        View priceInputView = adapter.getItemView(STAKE_INDEX);
        EditText inputSaupiel = (EditText) priceInputView.findViewById(R.id.CreateSession_input_sauspiel);
        EditText inputSolo = (EditText) priceInputView.findViewById(R.id.CreateSession_input_solo);
        try {
            //Integer.parseInt throws a NumberFormatException which is a subclass of IllegalArgumentException
            //when the input is not parsable
            int valueSauspiel = Integer.parseInt(inputSaupiel.getText().toString());
            int valueSolo = Integer.parseInt(inputSolo.getText().toString());
            if (valueSauspiel <= 0 || valueSolo <= 0) {
                throw new IllegalArgumentException();
            }
            //both texts are parsable as integers & are non-negative
            adapter.changeItemState(STAKE_INDEX, CreateSessionListAdapter.ListItemState.COMPLETE);
        } catch (IllegalArgumentException ignore) {
            adapter.changeItemState(STAKE_INDEX, CreateSessionListAdapter.ListItemState.PENDING);
            inputValidForCreation = false;
        }

        //validate that enough players are added for a game (at least 4)
        if (players.size() < 4) {
            adapter.changeItemState(PLAYER_INDEX, CreateSessionListAdapter.ListItemState.PENDING);
            inputValidForCreation = false;
        } else {
            adapter.changeItemState(PLAYER_INDEX, CreateSessionListAdapter.ListItemState.COMPLETE);
        }

        btnCreateSession.setEnabled(inputValidForCreation);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        adapter.saveInstanceState(outState);
    }

    /**
     * Updates the icons when the user edits any of the stake selection TextEdits
     */
    private class StakeInputWatcher implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            revalidateIcons();
        }
    }

    /**
     * Creates a new session with the stake values and players entered by the user
     */
    private class CreateSessionAction implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            //gather data
            EditText inputSauspiel = (EditText) adapter.getItemView(STAKE_INDEX).findViewById(R.id.CreateSession_input_sauspiel);
            EditText inputSolo = (EditText) adapter.getItemView(STAKE_INDEX).findViewById(R.id.CreateSession_input_solo);
            int priceSauspiel = Integer.parseInt(inputSauspiel.getText().toString());
            int priceSolo = Integer.parseInt(inputSolo.getText().toString());
            Stake stake = new Stake(priceSauspiel, priceSolo, priceSauspiel);

            List<Player> playerInSession = new ArrayList<>();
            playerInSession.addAll(players);
            Session session = new Session(playerInSession, stake);
            SheepsheadManagerApplication.getInstance().setCurrentSession(session);

            Intent intent = new Intent(CreateSession.this, FillGameResult.class);
            startActivity(intent);
        }
    }

    /**
     * Shows a dialog where the user can enter a players name
     */
    private class AddNewPlayerListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(CreateSession.this);
            final EditText textfield = new EditText(CreateSession.this);
            textfield.setInputType(InputType.TYPE_CLASS_TEXT);
            dialogBuilder.setView(textfield);
            dialogBuilder.setMessage(getString(R.string.CreateSession_dialog_text_enter_player_name));
            dialogBuilder.setCancelable(true);
            dialogBuilder.setPositiveButton(getString(R.string.FillGameResult_confirm_dialog), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    addNewPlayer(textfield.getText().toString());
                }
            });
            dialogBuilder.setNegativeButton(getString(R.string.Dialog_text_back), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            dialogBuilder.show();
        }
    }

    /**
     * A custom data container for the stake selection item in the {@link ExpandableListView}.
     * This container saves and loads the values in the stake selection
     */
    private class StakeSelectionListItem extends AbstractListItem {

        private static final String bundlekey_stake_sauspiel = "create_session_stake_sauspiel";
        private static final String bundlekey_stake_solo = "create_session_stake_solo";
        private static final
        @IdRes
        int elementSauspiel = R.id.CreateSession_input_sauspiel;
        private static final
        @IdRes
        int elementSolo = R.id.CreateSession_input_solo;

        public StakeSelectionListItem() {
            super(R.layout.create_session_item_stake);
        }

        @Override
        public void save(Bundle saveTo, View headerView) {
            EditText inputSauspiel = findView(headerView, elementSauspiel);
            EditText inputSolo = findView(headerView, elementSolo);
            saveTo.putString(bundlekey_stake_sauspiel, inputSauspiel.getText().toString());
            saveTo.putString(bundlekey_stake_solo, inputSolo.getText().toString());
        }

        @Override
        public void load(Bundle loadFrom, View headerView) {
            EditText inputSauspiel = findView(headerView, elementSauspiel);
            EditText inputSolo = findView(headerView, elementSolo);
            inputSauspiel.setText(loadFrom.getString(bundlekey_stake_sauspiel));
            inputSolo.setText(loadFrom.getString(bundlekey_stake_solo));
        }
    }

    /**
     * A custom data container for the player selection in the {@link ExpandableListView}
     * This container saves and loads the current added players
     */
    private class PlayerSelectionListItem extends AbstractListItem {
        private static final String bundlekey_players = "create_session_players";

        public PlayerSelectionListItem() {
            super(R.layout.create_session_item_players);
        }

        @Override
        public void save(Bundle saveTo, View headerView) {
            String[] names = new String[players.size()];
            for (int i = 0; i < names.length; i++) {
                names[i] = players.get(i).getName();
            }
            saveTo.putStringArray(bundlekey_players, names);
        }

        @Override
        public void load(Bundle loadFrom, View headerView) {
            String[] names = loadFrom.getStringArray(bundlekey_players);
            for (String name : names) {
                addNewPlayer(name);
            }
        }
    }
}


