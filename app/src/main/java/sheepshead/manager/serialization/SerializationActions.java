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

package sheepshead.manager.serialization;


import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import sheepshead.manager.R;
import sheepshead.manager.appcore.ActivityDescriptor;
import sheepshead.manager.appcore.SheepsheadManagerApplication;
import sheepshead.manager.session.Session;
import sheepshead.manager.uicontrolutils.DialogUtils;

public final class SerializationActions {
    public static final String sessionSaveDirectory = "saved";
    private static final String allowedChars = "[\\w\\d\\s-]+";//alphanumeric and -_

    //class will only contain static methods
    private SerializationActions() {
    }

    public static
    @NonNull
    ActivityDescriptor.MenuAction saveCurrentSessionAction(final String saveDirName) {
        return new ActivityDescriptor.MenuAction() {
            @Override
            public void onAction(final Activity activity) {
                Session session = SheepsheadManagerApplication.getInstance().getCurrentSession();
                if (session != null) {
                    final File saveDir = new File(activity.getFilesDir(), saveDirName);


                    final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(activity);
                    View view = activity.getLayoutInflater().inflate(R.layout.dialog_save_session, null);
                    final EditText input = (EditText) view.findViewById(R.id.DialogSaveSession_edit_text);
                    final TextView info = (TextView) view.findViewById(R.id.DialogSaveSession_info_text);
                    final Button saveButton = (Button) view.findViewById(R.id.DialogSaveSession_btn_save);
                    info.setText(String.format(activity.getString(R.string.DialogSaveSession_info_text),
                            saveDir.getAbsolutePath(), ""));
                    dialogBuilder.setView(view);
                    dialogBuilder.setTitle(R.string.DialogSaveSession_title_text);
                    final AlertDialog dialog = dialogBuilder.show();
                    input.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                        }

                        @Override
                        public void afterTextChanged(Editable text) {
                            String errormsg = "";
                            if (text.length() <= 0) {
                                //text is empty -> show error message
                                errormsg = activity.getString(R.string.DialogSaveSession_error_empty_name);
                            } else {
                                String name = text.toString();
                                if (!name.matches(allowedChars)) {
                                    errormsg = activity.getString(R.string.DialogSaveSession_error_invalid_name);
                                }
                            }
                            info.setSelected(!errormsg.isEmpty());
                            saveButton.setEnabled(errormsg.isEmpty());
                            String savePath = saveDir.getAbsolutePath();
                            info.setText(String.format(activity.getString(R.string.DialogSaveSession_info_text), savePath, errormsg));
                        }
                    });
                    saveButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            File saveTo = new File(saveDir, input.getText().toString());
                            saveDir.mkdirs();
                            try {
                                saveSessionTo(SheepsheadManagerApplication.getInstance().getCurrentSession(), saveTo, SheepsheadManagerApplication.INTERNAL_LOAD_SAVE_RULE);
                                dialog.dismiss();
                            } catch (RuntimeException e) {
                                e.printStackTrace();
                                DialogUtils.showErrorDialog(activity, e, null);
                            }
                        }
                    });


                } else {
                    //show error message
                    DialogUtils.showInfoDialog(activity, activity.getString(R.string.Menu_no_session_available), activity.getString(android.R.string.ok), null);
                }

            }
        };
    }


    public static void saveSessionTo(@NonNull Session session, @NonNull File file, @NonNull CSVRules format) {
        SessionCSVWriter writer = new SessionCSVWriter(format);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            writer.writeOut(session, fos);
            fos.close();
        } catch (IOException e) {
            int bytes = writer.getBytesWritten();
            throw new RuntimeException("Couldn't save session (I/O Error). Wrote " + bytes + "Bytes");
        } catch (SessionDataCorruptedException e) {
            throw new RuntimeException("Cannot save session due to ", e);
        }
    }

    public static Session loadSession(@NonNull File from, @NonNull CSVRules format) throws IOException, SessionDataCorruptedException {
        SessionCSVReader reader = new SessionCSVReader(format);
        try {
            FileInputStream fis = new FileInputStream(from);
            Session session = reader.readFrom(fis);
            fis.close();
            return session;
        } catch (IOException | SessionDataCorruptedException e) {
            System.out.println("Could not load save file: " + e.getMessage());
            e.printStackTrace();
            //rename the file and keep it for debugging purposes
            String newName = "corrupted_" + new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US)
                    .format(Calendar.getInstance().getTime()) + from.getName();
            File corr = new File(from.getParentFile(), newName);
            boolean success = from.renameTo(corr);
            System.out.print("Renamed corrupted file to " + newName);
            if (success) {
                System.out.println(" with success");
            } else {
                System.out.println(" with a failure");
            }
            throw e;
        }
    }
}
