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

package sheepshead.manager.uicontrolutils;


import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;

/**
 * Util class that contains methods to show different dialogs
 */
public final class DialogUtils {

    //class will only contain static methods
    private DialogUtils() {

    }

    /**
     * Immediately shows a user information dialog with only one (confirm) button.
     * A click on the button closes the dialog
     *
     * @param context    The context/view this dialog operates in
     * @param msg        Message that the dialog will display to the user
     * @param buttonText The text on the (confirm) button
     * @param listener   A listener that will be attached to the button
     */
    public static void showInfoDialog(@NonNull Context context, CharSequence msg, CharSequence buttonText, @Nullable DialogInterface.OnClickListener listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(msg);
        builder.setPositiveButton(buttonText, listener);
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
