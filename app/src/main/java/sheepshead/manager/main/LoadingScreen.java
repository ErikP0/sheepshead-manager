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

package sheepshead.manager.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import sheepshead.manager.R;
import sheepshead.manager.activities.HomeScreen;
import sheepshead.manager.appcore.AbstractBaseActivity;
import sheepshead.manager.appcore.ActivityDescriptor;
import sheepshead.manager.appcore.SheepsheadManagerApplication;

/**
 * Java-class for the LoadingScreen Activity.
 * The loading screen is the entry-point of the application.
 */
public class LoadingScreen extends AbstractBaseActivity {

    private static final ActivityDescriptor LOADING_SCREEN = new ActivityDescriptor(R.layout.activity_loading_screen);

    /**
     * Ensures that the {@link SheepsheadManagerApplication#loadingScreen()} is not called more than once.
     * This is necessary as {@link #createUserInterface(Bundle)} may get called multiple times (e.g.
     * when the screen is tilted the current activity is re-created)
     */
    private static boolean loadingStarted = false;

    public LoadingScreen() {
        super(LOADING_SCREEN);
    }

    @Override
    protected void updateUserInterface() {
        if (!loadingStarted) {
            loadingStarted = true;
            SheepsheadManagerApplication.getInstance().loadingScreen();

        }
    }

    @Override
    protected void registerActivitySpecificServices() {
        //no loading screen specific services/listeners
    }

    @Override
    protected void removeActivitySpecificServices() {
        //no loading screen specific services/listeners
    }

    @Override
    protected void createUserInterface(Bundle savedInstanceState) {
        //Temporarily go to the home screen by one click
        View activityPanel = findView(R.id.activity_loading_screen);
        activityPanel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoadingScreen.this, HomeScreen.class);
                startActivity(intent);

            }
        });
    }
}
