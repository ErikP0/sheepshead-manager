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
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import sheepshead.manager.R;
import sheepshead.manager.appcore.ActivityDescriptor;
import sheepshead.manager.appcore.SheepsheadManagerApplication;
import sheepshead.manager.serialization.CSVFormat;
import sheepshead.manager.serialization.SessionCSVWriter;
import sheepshead.manager.serialization.SessionDataCorruptedException;
import sheepshead.manager.session.Session;
import sheepshead.manager.uicontrolutils.DialogUtils;


/**
 * A chainable export action that exports a session with a certain csv format to a certain file path.
 * The exported file is passed to the next chain element
 */
public class FileExport extends ChainableExport<FileExport.ExportParams, File> {

    /**
     * @param next The next chain element, or null if this is supposed to be the last element
     */
    public FileExport(@Nullable ChainableExport<File, ?> next) {
        super(next);
    }

    private static void saveSessionTo(@NonNull Session session, @NonNull File file, @NonNull CSVFormat format) {
        SessionCSVWriter writer = new SessionCSVWriter(format);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            writer.writeOut(session, fos);
            fos.close();
        } catch (IOException e) {
            int bytes = writer.getBytesWritten();
            throw new RuntimeException("Couldn't save session (I/O Error). Wrote " + bytes + "Bytes", e);
        } catch (SessionDataCorruptedException e) {
            throw new RuntimeException("Cannot save session due to ", e);
        }
    }

    /**
     * Creates and returns a menu action that saves the current session.
     * This first opens a dialog where the user can name the file, it is then exported to <code>saveDirName/filename_by_user</code>
     *
     * @param saveDirName The directory name where the exported session will be stored
     * @return a menu action for saving the current session
     */
    public static ActivityDescriptor.MenuAction saveCurrentSession(final String saveDirName) {
        return new ActivityDescriptor.MenuAction() {
            @Override
            public void onAction(Activity activity) {
                File saveDir = new File(activity.getFilesDir(), saveDirName);
                Session session = SheepsheadManagerApplication.getInstance().getCurrentSession();
                if (session != null) {
                    new FileNameDialog("csv", new FileExport(null))
                            .startActionChain(new ExportParams(saveDir, session, SheepsheadManagerApplication.INTERNAL_LOAD_SAVE_FORMAT), activity);
                } else {
                    DialogUtils.showInfoDialog(activity, activity.getString(R.string.Menu_no_session_available), activity.getString(android.R.string.ok), null);
                }

            }
        };
    }

    @Override
    public void performAction(ExportParams params, Activity activity) {
        params.exportDestination.delete();
        params.exportDestination.getParentFile().mkdirs();
        try {
            saveSessionTo(params.exportSession, params.exportDestination, params.exportRules);
            nextAction(params.exportDestination, activity);
        } catch (RuntimeException e) {
            DialogUtils.showErrorDialog(activity, e, null);
        }
    }

    /**
     * File Export Parameter Object that contains the destination, the format and the session to export
     */
    public static class ExportParams {
        private File exportDestination;
        private CSVFormat exportRules;
        private Session exportSession;

        public ExportParams(@NonNull File file, @NonNull Session session, @NonNull CSVFormat rules) {
            exportDestination = file;
            exportRules = rules;
            exportSession = session;
        }

        public File getExportDestination() {
            return exportDestination;
        }

        public void setExportDestination(@NonNull File newDestination) {
            exportDestination = newDestination;
        }

        public CSVFormat getCSVRules() {
            return exportRules;
        }

        void setCSVRules(@NonNull CSVFormat rule) {
            exportRules = rule;
        }
    }
}
