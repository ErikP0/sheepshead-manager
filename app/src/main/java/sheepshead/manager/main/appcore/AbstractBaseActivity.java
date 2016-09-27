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

package sheepshead.manager.main.appcore;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;


public abstract class AbstractBaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        createUserInterface(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        SheepsheadManagerApplication.getInstance().registerServices();
        registerActivitySpecificServices();
    }

    @Override
    public void onPause() {
        super.onPause();
        SheepsheadManagerApplication.getInstance().unregisterServices();
        removeActivitySpecificServices();
    }

    @Override
    public void onStop() {
        super.onStop();
        SheepsheadManagerApplication.getInstance().saveApplicationState();
    }

    protected abstract void registerActivitySpecificServices();

    protected abstract void removeActivitySpecificServices();

    protected abstract void createUserInterface(Bundle savedInstanceState);
}
