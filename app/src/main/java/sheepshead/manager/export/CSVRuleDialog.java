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
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Switch;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;

import sheepshead.manager.R;
import sheepshead.manager.serialization.CSVFormat;
import sheepshead.manager.uicontrolutils.DialogUtils;

/**
 * A dialog where the user can specify the .csv-export format (escape and separation characters, encoding, ...).
 * This can be part of a ExportChain described in {@link ChainableExport}
 */
public class CSVRuleDialog extends ChainableExport<FileExport.ExportParams, FileExport.ExportParams> implements Button.OnClickListener {

    /**
     * A list of all available file encodings/charsets
     */
    private static final String[] available_encodings = getAvailableEncodings();

    private Activity activity;
    private FileExport.ExportParams params;

    private AlertDialog dialog;

    //dialog attributes
    private Switch quoteAllSwitch;
    private EditText separatorInput;
    private EditText escapeInput;
    private Spinner encodingSpinner;
    private Button confirmButton;

    /**
     * @param next The next element of the export chain, or null if this is supposed to be the last action
     */
    public CSVRuleDialog(@NonNull ChainableExport<FileExport.ExportParams, ?> next) {
        super(next);
    }

    private static String[] getAvailableEncodings() {
        Map<String, Charset> charsets = Charset.availableCharsets();
        int index = 0;
        String[] result = new String[charsets.size()];
        Iterator<String> it = charsets.keySet().iterator();
        while (it.hasNext()) {
            result[index] = it.next();
            index++;
        }
        return result;
    }

    @Override
    protected void performAction(FileExport.ExportParams input, final Activity activity) {
        this.activity = activity;
        params = input;
        //show dialog
        View dialogView = activity.getLayoutInflater().inflate(R.layout.dialog_select_csv_rules, null);
        quoteAllSwitch = (Switch) dialogView.findViewById(R.id.DialogSelectCSVRule_switch_quote_cells);
        separatorInput = (EditText) dialogView.findViewById(R.id.DialogSelectCSVRule_edit_text_separator);
        escapeInput = (EditText) dialogView.findViewById(R.id.DialogSelectCSVRule_edit_text_escape);
        encodingSpinner = (Spinner) dialogView.findViewById(R.id.DialogSelectCSVRule_spinner_encoding);
        confirmButton = (Button) dialogView.findViewById(R.id.DialogSelectCSVRule_btn_confirm);

        ImageButton infoButton = (ImageButton) dialogView.findViewById(R.id.DialogSelectCSVRule_btn_info);
        infoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogUtils.showInfoDialog(activity, activity.getString(R.string.DialogSelectCSVRule_info_text),
                        activity.getString(android.R.string.ok), null);
            }
        });

        //fill encoding spinner
        encodingSpinner.setAdapter(new ArrayAdapter<>(activity,
                android.R.layout.simple_spinner_dropdown_item,
                available_encodings));

        //apply input rules
        apply(input.getCSVRules());

        //add button listener
        confirmButton.setOnClickListener(this);

        //create dialog and show
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setView(dialogView);
        builder.setTitle(R.string.DialogSelectCSVRule_title_text);
        dialog = builder.show();
    }

    private void apply(CSVFormat rule) {
        quoteAllSwitch.setChecked(rule.quoteEveryCell());
        separatorInput.setText(Character.toString(rule.getSeparator()));
        escapeInput.setText(Character.toString(rule.getEscape()));
        //get the encoding name (not a alias)
        String encoding = Charset.forName(rule.getEncoding()).name();

        int index = Arrays.binarySearch(available_encodings, encoding);
        if (index < 0) {
            throw new IllegalArgumentException("Cannot select (alias?) encoding "
                    + rule.getEncoding() + " with standard name " + encoding
                    + " in selection (" + Arrays.toString(available_encodings) + ")");
        }
        encodingSpinner.setSelection(index);
        checkIfValid();
    }

    private void checkIfValid() {
        //need to check, if separator/escape input are a character
        boolean valid = separatorInput.getText().length() == 1 && escapeInput.getText().length() == 1;
        confirmButton.setEnabled(valid);
    }

    @Override
    public void onClick(View v) {
        CSVFormat selectedRule = new CSVFormat(getChar(separatorInput), getChar(escapeInput),
                available_encodings[encodingSpinner.getSelectedItemPosition()],
                quoteAllSwitch.isChecked(), new ExportCSVWriter(activity), null);//<-- no reader supplied
        params.setCSVRules(selectedRule);
        dialog.dismiss();
        nextAction(params, activity);
    }

    private char getChar(EditText t) {
        char[] chars = new char[1];
        t.getText().getChars(0, 1, chars, 0);
        return chars[0];
    }
}
