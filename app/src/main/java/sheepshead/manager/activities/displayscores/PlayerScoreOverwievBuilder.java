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


import android.content.Context;
import android.graphics.BitmapFactory;
import android.support.annotation.DrawableRes;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import sheepshead.manager.R;
import sheepshead.manager.game.PlayerRole;

/**
 * Builder convenience class for generating the balance overview for the DisplayScoresHome-Activity
 */
class PlayerScoreOverwievBuilder {

    private static final
    @LayoutRes
    int layoutId = R.layout.display_scores_overview;
    private static final
    @IdRes
    int playerNameId = R.id.DisplayScoresOverview_text_player_name;
    private static final
    @IdRes
    int playerScoreId = R.id.DisplayScoresOverview_text_player_score;
    private static final
    @IdRes
    int playerIconId = R.id.DisplayScoresOverview_status_icon;

    private
    @NonNull
    PlayerScoreEntry data;

    /**
     * Creates a new builder with the given data
     *
     * @param entry The PlayerScoreEntry that should be displayed
     */
    PlayerScoreOverwievBuilder(@NonNull PlayerScoreEntry entry) {
        data = entry;
    }

    /**
     * Creates and returns a view displaying the PlayerScoreEntry
     *
     * @param context Context for inflating the layout
     * @return The view displaying the player data
     */
    public View build(Context context) {
        String displayedScore = Integer.toString(data.getBalance());
        PlayerRole role = data.getRole();
        if (role != null) {
            displayedScore += "(" + Integer.toString(role.getMoney()) + ")";
        }
        LayoutInflater infalInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View buildView = infalInflater.inflate(layoutId, null);
        //fill with values
        TextView nameView = (TextView) buildView.findViewById(playerNameId);
        TextView scoreView = (TextView) buildView.findViewById(playerScoreId);
        ImageView iconView = (ImageView) buildView.findViewById(playerIconId);

        nameView.setText(data.getName());
        scoreView.setText(displayedScore);
        ScoreState state = ScoreState.determinateScoreState(data.getRole());
        iconView.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), state.getIcon()));

        return buildView;
    }

    /**
     * Enum for associating a participation state with a symbol to display
     */
    private enum ScoreState {
        /**
         * The player did not participate in the latest game
         */
        NOT_PARTICIPATET(android.R.drawable.presence_offline),
        /**
         * The player participated and earned some money in the latest game
         */
        RISING(android.R.drawable.presence_online),
        /**
         * The player participated and lost some money in the latest game
         */
        FALLING(android.R.drawable.presence_busy);

        private
        @DrawableRes
        int iconId;

        ScoreState(@DrawableRes int icon) {
            iconId = icon;
        }

        /**
         * Returns the appropriate ScoreState for the given PlayerRole:
         * If the role is null, {@link #NOT_PARTICIPATET} is returned.
         * If the earned money greater than zero, {@link #RISING}, otherwise
         * {@link #FALLING} is returned.
         *
         * @param playerRole The role to associate a ScoreState
         * @return An appropriate score state
         */
        static ScoreState determinateScoreState(@Nullable PlayerRole playerRole) {
            if (playerRole == null) {
                return ScoreState.NOT_PARTICIPATET;
            } else if (playerRole.getMoney() < 0) {
                return ScoreState.FALLING;
            } else {
                return ScoreState.RISING;
            }
        }

        public
        @DrawableRes
        int getIcon() {
            return iconId;
        }
    }
}
