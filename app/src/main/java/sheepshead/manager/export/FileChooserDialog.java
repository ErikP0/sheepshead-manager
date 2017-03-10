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
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import java.io.File;
import java.io.FilenameFilter;

import sheepshead.manager.R;

public class FileChooserDialog extends ChainableAction<File, File> implements AdapterView.OnItemSelectedListener, View.OnClickListener {

    private static final FilenameFilter FILTER = new FilenameFilter() {
        @Override
        public boolean accept(File file, String s) {
            return s.toLowerCase().endsWith(".csv");
        }
    };

    private Activity activity;
    private File[] selectableFiles;
    private int selectedItem;
    private Dialog dialog;

    public FileChooserDialog(@Nullable ChainableAction<File, ?> next) {
        super(next);
    }

    @Override
    protected void performAction(File input, Activity activity) {
        this.activity = activity;
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(R.string.DialogFileChooser_title_text);
        View dialogView = activity.getLayoutInflater().inflate(R.layout.dialog_file_chooser, null);
        builder.setView(dialogView);
        selectableFiles = getSelectableFiles(input);
        Spinner spinner = (Spinner) dialogView.findViewById(R.id.DialogFileChooser_spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(activity,
                android.R.layout.simple_spinner_dropdown_item, getFilenames(selectableFiles));
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        Button confirmButton = (Button) dialogView.findViewById(R.id.DialogFileChooser_btn_confirm);
        confirmButton.setOnClickListener(this);
        confirmButton.setEnabled(selectableFiles.length > 0);
        dialog = builder.show();
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        selectedItem = adapterView.getSelectedItemPosition();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    public void onClick(View view) {
        dialog.dismiss();
        nextAction(selectableFiles[selectedItem], activity);
    }

    private File[] getSelectableFiles(File dir) {
        if (dir.isFile()) {
            return new File[]{dir};
        } else {
            return dir.listFiles(FILTER);
        }
    }

    private String[] getFilenames(File[] files) {
        String[] names = new String[files.length];
        for (int i = 0; i < names.length; ++i) {
            String fn = files[i].getName();
            names[i] = fn.substring(0, fn.length() - ".csv".length());//set name without .csv extension
        }
        return names;
    }
}
