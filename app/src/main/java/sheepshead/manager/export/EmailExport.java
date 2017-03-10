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
import android.content.Intent;
import android.net.Uri;
import android.support.v4.content.FileProvider;

import java.io.File;

import sheepshead.manager.R;
import sheepshead.manager.appcore.ActivityDescriptor;
import sheepshead.manager.appcore.SheepsheadManagerApplication;
import sheepshead.manager.session.Session;
import sheepshead.manager.uicontrolutils.DialogUtils;

/**
 * A chainable export action that lets the user pick an installed email client and starts a send-email
 * intent with a file as attachment
 */
public class EmailExport extends ChainableExport<File, Void> {

    /**
     * The authority string for the email attachment file provider
     */
    private static final String MAIL_AUTHORITY = "sheepshead.manager.export.EmailExport";

    /**
     * @return A menu action that exports the current session via email
     */
    public static ActivityDescriptor.MenuAction emailCurrentSession() {
        return new ActivityDescriptor.MenuAction() {
            @Override
            public void onAction(Activity activity) {
                Session session = SheepsheadManagerApplication.getInstance().getCurrentSession();
                if (session != null) {
                    //The only valid email attachment path is defined in xml/provider_paths.xml
                    File attachmentPath = new File(activity.getFilesDir(), "mailed/session.csv");
                    FileExport.ExportParams params = new FileExport.ExportParams(attachmentPath, session, SheepsheadManagerApplication.INTERNAL_LOAD_SAVE_FORMAT);
                    new CSVRuleDialog(new FileExport(new EmailExport())).startActionChain(params, activity);
                } else {
                    DialogUtils.showInfoDialog(activity, activity.getString(R.string.Menu_no_session_available), activity.getString(android.R.string.ok), null);
                }
            }
        };
    }

    @Override
    protected void performAction(File file, Activity activity) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setData(Uri.parse("mailto:")); // only email apps should handle this
        intent.putExtra(Intent.EXTRA_EMAIL, new String[0]);//the user may choose the mail addresses
        intent.putExtra(Intent.EXTRA_SUBJECT, activity.getString(R.string.EmailExport_subject));
        intent.putExtra(Intent.EXTRA_TEXT, "");
        intent.setType("message/rfc822");
        Uri attachment = FileProvider.getUriForFile(activity, MAIL_AUTHORITY, file);
        intent.putExtra(Intent.EXTRA_STREAM, attachment);
        //check if there is an application installed that can handle this intent
        if (intent.resolveActivity(activity.getPackageManager()) != null) {
            activity.startActivity(Intent.createChooser(intent, activity.getString(R.string.EmailExport_chooser_title)));
        } else {
            DialogUtils.showInfoDialog(activity, activity.getString(R.string.EmailExport_no_mail_installed), activity.getString(android.R.string.ok), null);
        }
    }
}
