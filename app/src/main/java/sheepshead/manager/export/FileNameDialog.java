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

package sheepshead.manager.export;


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

import sheepshead.manager.R;

/**
 * A chainable export action that opens a dialog and asks the user for a (file-)name. This filename is
 * then set in the file export parameter object and passed to the next chain element
 */
public class FileNameDialog extends ChainableExport<FileExport.ExportParams, FileExport.ExportParams> implements TextWatcher, View.OnClickListener {

    /**
     * A regular expression pattern for filtering the users input
     */
    private static final String allowedChars = "[\\w\\d\\s-]+";//alphanumeric and -_

    /**
     * The file extesion that will be appended to the user input
     */
    private String extension;

    private Activity activity;
    private FileExport.ExportParams exportParams;

    //dialog attributes
    private AlertDialog dialog;
    private TextView infoTextView;
    private Button confirmButton;
    private EditText filenameInput;

    /**
     * @param fileExtension This extension is appended to the user input
     * @param next          The next action, or null if this is supposed to be the last element in the chain
     */
    public FileNameDialog(String fileExtension, @NonNull ChainableExport<FileExport.ExportParams, ?> next) {
        super(next);
        extension = fileExtension;
    }

    private void setInfoText(String errorText) {
        String infoText = String.format(activity.getString(R.string.DialogSelectName_info_text),
                exportParams.getExportDestination().getAbsolutePath(), errorText);
        infoTextView.setText(infoText);
    }

    @Override
    public void performAction(FileExport.ExportParams input, Activity activity) {
        this.activity = activity;
        exportParams = input;
        View dialogView = activity.getLayoutInflater().inflate(R.layout.dialog_select_name, null);
        filenameInput = (EditText) dialogView.findViewById(R.id.DialogSelectName_edit_text);
        infoTextView = (TextView) dialogView.findViewById(R.id.DialogSaveSession_info_text);
        confirmButton = (Button) dialogView.findViewById(R.id.DialogSelectName_btn_save);
        setInfoText("");//no error message
        filenameInput.addTextChangedListener(this);
        confirmButton.setOnClickListener(this);

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(activity);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setTitle(R.string.DialogSelectName_title_text);
        dialog = dialogBuilder.show();
    }

    @Override
    public void onClick(View v) {
        File namedFile = new File(exportParams.getExportDestination(), filenameInput.getText().toString() + "." + extension);
        dialog.dismiss();
        exportParams.setExportDestination(namedFile);
        nextAction(exportParams, activity);
    }

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
            errormsg = activity.getString(R.string.DialogSelectName_error_empty_name);
        } else {
            String name = text.toString();
            if (!name.matches(allowedChars)) {
                errormsg = activity.getString(R.string.DialogSelectName_error_invalid_name);
            }
        }
        confirmButton.setEnabled(errormsg.isEmpty());
        setInfoText(errormsg);
    }
}
