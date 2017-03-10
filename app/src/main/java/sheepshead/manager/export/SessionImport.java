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

package sheepshead.manager.export;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

import sheepshead.manager.R;
import sheepshead.manager.appcore.ActivityDescriptor;
import sheepshead.manager.appcore.SheepsheadManagerApplication;
import sheepshead.manager.game.Player;
import sheepshead.manager.serialization.SerializationActions;
import sheepshead.manager.serialization.SessionDataCorruptedException;
import sheepshead.manager.session.Session;
import sheepshead.manager.uicontrolutils.DialogUtils;

public class SessionImport extends ChainableAction<File, Void> implements DialogInterface.OnClickListener {

    private Session loadedSession;
    private Dialog dialog;
    private Activity activity;

    protected SessionImport() {
        super();
    }

    public static ActivityDescriptor.MenuAction loadSession(final String saveDir) {
        return new ActivityDescriptor.MenuAction() {
            @Override
            public void onAction(Activity activity) {
                File dir = new File(activity.getFilesDir(), saveDir);
                new FileChooserDialog(new SessionImport()).startActionChain(dir, activity);
            }
        };
    }

    @Override
    protected void performAction(File input, Activity activity) {
        this.activity = activity;
        try {
            loadedSession = SerializationActions.loadSession(input, SheepsheadManagerApplication.INTERNAL_LOAD_SAVE_FORMAT);
            showSessionInfo(activity);
        } catch (IOException e) {
            String message = String.format(activity.getString(R.string.SessionImport_text_io_exception), input.getName(), e.getMessage());
            DialogUtils.showInfoDialog(activity, message, activity.getString(android.R.string.ok), null);
        } catch (SessionDataCorruptedException e) {
            String message = String.format(activity.getString(R.string.SessionImport_text_session_corrupted), e.getMessage());
            DialogUtils.showInfoDialog(activity, message, activity.getString(android.R.string.ok), null);
        }
    }

    private void showSessionInfo(Activity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(R.string.SessionImport_dialog_title);
        String progressWarning = "";
        if (SheepsheadManagerApplication.getInstance().getCurrentSession() != null) {
            progressWarning = activity.getString(R.string.SessionImport_dialog_unsaved_progress_warning);
        }
        int anzPlayer = loadedSession.getPlayers().size();
        int anzGames = loadedSession.getGameAmount();
        String playerNames = getPlayerNames(loadedSession.getPlayers());
        String message = String.format(activity.getString(R.string.SessionImport_dialog_messsage), anzPlayer, playerNames, anzGames, progressWarning);
        builder.setMessage(message);
        builder.setPositiveButton(R.string.SessionImport_dialog_confirm, this);
        builder.setNegativeButton(R.string.Sessionimport_dialog_abort, this);
        dialog = builder.show();
    }

    @Override
    public void onClick(DialogInterface dialogInterface, int which) {
        if (which == DialogInterface.BUTTON_POSITIVE) {
            //set loaded session
            SheepsheadManagerApplication.getInstance().setCurrentSession(loadedSession);
        }
        dialog.dismiss();
    }

    private String getPlayerNames(Collection<Player> players) {
        StringBuilder b = new StringBuilder();
        Iterator<Player> it = players.iterator();
        if (it.hasNext()) {
            b.append(it.next().getName());
        }
        while (it.hasNext()) {
            b.append(',');
            b.append(' ');
            b.append(it.next().getName());
        }
        return b.toString();
    }
}
