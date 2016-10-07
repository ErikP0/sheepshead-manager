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

package sheepshead.manager.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import sheepshead.manager.R;
import sheepshead.manager.appcore.AbstractBaseActivity;
import sheepshead.manager.uicontrolutils.DialogUtils;

/**
 * A home screen where the user can chose to continue the last session or to create a new session
 */
public class HomeScreen extends AbstractBaseActivity {
    @Override
    protected void registerActivitySpecificServices() {

    }

    @Override
    protected void removeActivitySpecificServices() {

    }

    @Override
    protected void createUserInterface(Bundle savedInstanceState) {
        setContentView(R.layout.activity_home_screen);

        //disable continue button (for now)
        Button continueSession = findView(R.id.HomeScreen_btn_continue_last_session);
        continueSession.setEnabled(false);

        //add listener to new session button
        Button newSession = findView(R.id.HomeScreen_btn_new_session);
        newSession.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO switch to create session activity
                DialogUtils.showInfoDialog(HomeScreen.this, "In Kürze kann hier eine neue Sitzung erstellt werden", "Schade :(", null);
            }
        });
    }
}
