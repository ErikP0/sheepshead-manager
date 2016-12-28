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

package sheepshead.manager.activities.displayscores;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;

import sheepshead.manager.R;
import sheepshead.manager.appcore.AbstractBaseActivity;

public class DisplayScoresPortrait extends AbstractBaseActivity {

    public static final String bundlekey_players = "DisplayScores_players";

    private String[] names = {"Erik", "Simon", "Arian", "Clemens", "Nico"};

    @Override
    protected void registerActivitySpecificServices() {

    }

    @Override
    protected void removeActivitySpecificServices() {

    }

    @Override
    protected void createUserInterface(Bundle savedInstanceState) {
        setContentView(R.layout.activity_display_scores_portrait);

        Button showScoreboardButton = findView(R.id.DisplayScores_btn_show_table);
        showScoreboardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showScoreboard();
            }
        });

        //TODO temporary, replace with use of real session object

        int[] scores = {100, 60, -100, -30, -30};
        int[] deltas = {10, 10, 0, -10, -10};
        TableLayout playerPanel = findView(R.id.DisplayScores_panel_player_scores);
        for (int i = 0; i < names.length; i++) {
            View player = new PlayerScoreOverwievBuilder(names[i], scores[i], deltas[i]).build(this);
            playerPanel.addView(player);
        }
    }

    private void showScoreboard() {
        Intent intent = new Intent(DisplayScoresPortrait.this, DisplayScoresLandscape.class);
        intent.putExtra(bundlekey_players, names);
        startActivity(intent);
    }
}
