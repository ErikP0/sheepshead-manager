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

import android.os.Bundle;

import sheepshead.manager.R;
import sheepshead.manager.appcore.SheepsheadManagerApplication;
import sheepshead.manager.appcore.StartupActivity;

/**
 * Java-class for the LoadingScreen Activity.
 * The loading screen is the entry-point of the application.
 */
public class LoadingScreen extends StartupActivity {

    public LoadingScreen() {

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
        setContentView(R.layout.activity_loading_screen);

        SheepsheadManagerApplication.getInstance().loadingScreen();
    }
}
