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

package sheepshead.manager.uicontrolutils;


import android.content.Context;
import android.support.annotation.ColorRes;
import android.support.v4.content.ContextCompat;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.widget.TextView;

import sheepshead.manager.R;

/**
 * Utility class for creating/modifying {@link TextView}s
 */
public class TextViewUtils {

    private TextViewUtils() {
        //class is not intended to be instantiated
    }

    /**
     * Returns a text view displaying the given number as string.
     * The color of the displayed text depends on the value: A negative value will be shown in red,
     * a positive/zero value will be shown in green.
     *
     * @param context the creation context
     * @param n       the number to display
     * @return a TextView displaying the given number as colored string
     */
    public static TextView createColoredText(Context context, int n) {
        String s = Integer.toString(n);
        SpannableString ss = new SpannableString(s);
        @ColorRes
        int color = n >= 0 ? R.color.scoreboard_positive_text : R.color.scoreboard_negative_text;
        ss.setSpan(new ForegroundColorSpan(ContextCompat.getColor(context, color)), 0, s.length(), 0);
        TextView tv = new TextView(context);
        tv.setText(ss);
        return tv;
    }
}
