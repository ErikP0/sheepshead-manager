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
import sheepshead.manager.activities.displayscores.DisplayScoresHome;
import sheepshead.manager.appcore.AbstractBaseActivity;
import sheepshead.manager.appcore.ActivityDescriptor;
import sheepshead.manager.appcore.SheepsheadManagerApplication;

/**
 * A home screen where the user can chose to continue the last session or to create a new session
 */
public class HomeScreen extends AbstractBaseActivity {

    /**
     * Describes static behaviour of this activity
     */
    private static final ActivityDescriptor HOME_SCREEN = new ActivityDescriptor(R.layout.activity_home_screen)
            .toolbar(R.id.HomeScreen_toolbar)
            .title(R.string.Title_HomeScreen);

    public HomeScreen() {
        super(HOME_SCREEN);
    }

    @Override
    protected void registerActivitySpecificServices() {

    }

    @Override
    protected void removeActivitySpecificServices() {

    }

    @Override
    protected void createUserInterface(Bundle savedInstanceState) {

        //add listener to new session button
        Button newSession = findView(R.id.HomeScreen_btn_new_session);
        newSession.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intentToUpActivity(CreateSession.class);
            }
        });
        //add listener to continue button
        Button continueSession = findView(R.id.HomeScreen_btn_continue_last_session);
        continueSession.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intentToUpActivity(DisplayScoresHome.class);
            }
        });
    }

    @Override
    protected void updateUserInterface() {
        //enable continue button if a session is present
        Button continueSession = findView(R.id.HomeScreen_btn_continue_last_session);
        continueSession.setEnabled(SheepsheadManagerApplication.getInstance().getCurrentSession() != null);
    }
}
