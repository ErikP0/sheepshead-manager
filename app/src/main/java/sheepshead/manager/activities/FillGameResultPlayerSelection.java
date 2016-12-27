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


import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.BitmapFactory;
import android.support.annotation.DrawableRes;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import sheepshead.manager.R;
import sheepshead.manager.singleGameRequirements.Player;
import sheepshead.manager.uicontrolutils.DataCheckBox;

public class FillGameResultPlayerSelection implements View.OnClickListener, Dialog.OnDismissListener {

    private static final
    @DrawableRes
    int ICON_VALID = android.R.drawable.presence_online;
    private static final
    @DrawableRes
    int ICON_INVALID = android.R.drawable.presence_away;

    private Set<Player> selected;
    private FillGameResultPlayerSelection other;
    private Collection<Player> allPlayers;
    private boolean isCaller;

    private TextView label;
    private ImageView icon;
    private Button button;
    private Dialog dialog;
    private ViewGroup playerCheckboxes;
    private FillGameResult activity;
    private int remaining;

    public FillGameResultPlayerSelection(FillGameResult context, View panel, boolean caller, Collection<Player> allPlayers) {
        activity = context;
        isCaller = caller;
        this.allPlayers = allPlayers;
        selected = new HashSet<>();

        label = (TextView) panel.findViewById(R.id.FillGameResultPlayerSelection_text);
        icon = (ImageView) panel.findViewById(R.id.FillGameResultPlayerSelection_icon);
        button = (Button) panel.findViewById(R.id.FillGameResultPlayerSelection_button);
        button.setOnClickListener(this);
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        update();
    }

    public Set<Player> getSelectedPlayers() {
        return selected;
    }

    public void setOther(FillGameResultPlayerSelection o) {
        other = o;
    }

    @Override
    public void onClick(View v) {
        remaining = getAmountOfSelectedPlayersNeeded();

        LayoutInflater infalInflater = (LayoutInflater) activity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ViewGroup dialogPanel = (ViewGroup) infalInflater.inflate(R.layout.player_selection_dialog, null);

        TextView text = (TextView) dialogPanel.findViewById(R.id.PlayerSelectionDialog_title);
        text.setText("Wähle " + remaining + " Spieler");

        LinearLayout players = new LinearLayout(activity);
        players.setOrientation(LinearLayout.VERTICAL);
        dialogPanel.addView(players);
        for (Player player : allPlayers) {
            if (!other.getSelectedPlayers().contains(player)) {
                DataCheckBox<Player> playerBox = (DataCheckBox<Player>) infalInflater.inflate(R.layout.player_selection_checkbox, null);
                playerBox.setText(player.getName());
                playerBox.put(player);
                playerBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            remaining--;
                        } else {
                            remaining++;
                        }
                        evaluateClose();
                    }
                });
                players.addView(playerBox);
            }
        }
        playerCheckboxes = players;
        dialog = new AlertDialog.Builder(activity).setView(dialogPanel).create();
        dialog.setOnDismissListener(this);
        dialog.show();
    }

    private void evaluateClose() {
        if (remaining <= 0) {
            selected.clear();
            for (int i = 0; i < playerCheckboxes.getChildCount(); i++) {
                DataCheckBox<Player> playerBox = (DataCheckBox<Player>) playerCheckboxes.getChildAt(i);
                if (playerBox.isChecked()) {
                    selected.add(playerBox.getData());
                }
            }
            dialog.dismiss();
        }
    }

    private int getAmountOfSelectedPlayersNeeded() {
        int result = activity.getCurrentlySelectedGameType().getNumberOfCallers();
        if (!isCaller) {
            result = 4 - result;
        }
        return result;
    }

    public void update() {
        int selectedPlayersNeeded = getAmountOfSelectedPlayersNeeded();
        StringBuilder labelMsg = new StringBuilder();
        if (selected.size() == selectedPlayersNeeded) {
            icon.setImageBitmap(BitmapFactory.decodeResource(activity.getResources(), ICON_VALID));
            for (Player p : selected) {
                labelMsg.append(p.getName());
                labelMsg.append(", ");
            }
        } else {
            icon.setImageBitmap(BitmapFactory.decodeResource(activity.getResources(), ICON_INVALID));
            labelMsg.append(activity.getString(R.string.FillGameResult_invalidPlayerAmount));
        }

        label.setText(labelMsg.toString());
        other.tryAutoFill();
    }

    public void tryAutoFill() {
        int needed = getAmountOfSelectedPlayersNeeded();
        if (selected.size() < needed) {
            //calculate all players who could be selected
            Set<Player> selectablePlayers = new HashSet<>();
            for (Player p : allPlayers) {
                if (!other.getSelectedPlayers().contains(p)) {
                    selectablePlayers.add(p);
                }
            }
            //fill only, if all selectable players must be selected to meet the game type requirements
            if (selectablePlayers.size() + selected.size() == needed) {
                //fill
                selected.addAll(selectablePlayers);
                update();
            }
        }
    }

    public void clearSelection() {
        selected.clear();
        update();
    }
}
