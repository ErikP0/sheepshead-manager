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

package sheepshead.manager.diagram.androidview;

import com.jjoe64.graphview.DefaultLabelFormatter;


public class GameLabelFormatter extends DefaultLabelFormatter {

    @Override
    public String formatLabel(double value, boolean isValueX) {
        if (isValueX) {
            if (value == Math.floor(value) && !Double.isInfinite(value)) {
                return super.formatLabel(value, true);
            }
            return "";
        } else {
            return super.formatLabel(value, false);
        }
    }
}
