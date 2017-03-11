/*
 * Copyright 2017  Erik Pohle
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

package sheepshead.manager.utils;


import android.graphics.Color;

import sheepshead.manager.game.Player;

public class ColorManagement {

    //see http://stackoverflow.com/questions/470690/how-to-automatically-generate-n-distinct-colors
    private static final int[] KELLY_COLORS = {
            Color.parseColor("#FFB300"),    // Vivid Yellow
            Color.parseColor("#803E75"),    // Strong Purple
            Color.parseColor("#FF6800"),    // Vivid Orange
            Color.parseColor("#A6BDD7"),    // Very Light Blue
            Color.parseColor("#C10020"),    // Vivid Red
            Color.parseColor("#CEA262"),    // Grayish Yellow
            Color.parseColor("#817066"),    // Medium Gray

            Color.parseColor("#007D34"),    // Vivid Green
            Color.parseColor("#F6768E"),    // Strong Purplish Pink
            Color.parseColor("#00538A"),    // Strong Blue
            Color.parseColor("#FF7A5C"),    // Strong Yellowish Pink
            Color.parseColor("#53377A"),    // Strong Violet
            Color.parseColor("#FF8E00"),    // Vivid Orange Yellow
            Color.parseColor("#B32851"),    // Strong Purplish Red
            Color.parseColor("#F4C800"),    // Vivid Greenish Yellow
            Color.parseColor("#7F180D"),    // Strong Reddish Brown
            Color.parseColor("#93AA00"),    // Vivid Yellowish Green
            Color.parseColor("#593315"),    // Deep Yellowish Brown
            Color.parseColor("#F13A13"),    // Vivid Reddish Orange
            Color.parseColor("#232C16"),    // Dark Olive Green
    };

    public static int getColorForIndex(int index) {
        return KELLY_COLORS[Math.abs(index) % KELLY_COLORS.length];
    }

    public static int getColorForPlayer(Player p) {
        return getColorForIndex(p.hashCode());
    }
}
