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

package sheepshead.manager.appcore;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import sheepshead.manager.R;

/**
 * Abstract superclass for all activities used.
 * The class calls some of the functionality in {@link SheepsheadManagerApplication} under certain conditions.
 * As a extender of this class, make sure to call super.x() when overriding x of {@link AppCompatActivity}
 */
public abstract class AbstractBaseActivity extends AppCompatActivity {

    private final ActivityDescriptor activityDescriptor;
    private ActionBar toolbar;

    protected AbstractBaseActivity(ActivityDescriptor descriptor) {
        activityDescriptor = descriptor;
    }

    @Override
    final protected void onCreate(Bundle savedInstanceState) {
        /*
        onCreate is called once per lifetime of this activity.
        It can be used to create the UserInterface
         */
        super.onCreate(savedInstanceState);
        setContentView(activityDescriptor.getLayoutId());
        createToolbar();
        createUserInterface(savedInstanceState);
    }

    private void createToolbar() {
        if (!activityDescriptor.getToolbarId().isEmpty()) {
            Toolbar bar = findView(activityDescriptor.getToolbarId().getValue());
            setSupportActionBar(bar);
            toolbar = getSupportActionBar();
            toolbar.setElevation(4.0f);
            toolbar.setDisplayHomeAsUpEnabled(activityDescriptor.hasNavigationBackToParentEnabled());
            bar.setTitleTextAppearance(this, R.style.SheepsheadText_Title_Big);
            if (!activityDescriptor.getTitle().isEmpty()) {
                toolbar.setTitle(activityDescriptor.getTitle().getValue());
            }
        }
    }

    @Nullable
    protected ActionBar getToolbar() {
        return toolbar;
    }

    @Override
    public void onResume() {
        /*
        onResume is called every time this activity gains focus (so calls are upon creation and whenever the user maximizes the application again)
        It can be used to register ui-listener or services belonging to this activity
         */
        super.onResume();
        SheepsheadManagerApplication.getInstance().registerServices();
        registerActivitySpecificServices();
        updateUserInterface();
    }

    @Override
    public void onPause() {
        /*
        onPause is called every time this activity loses focus (so when the user presses the home button)
        It can be used to remove ui-listener or services that should not work in other activities
         */
        super.onPause();
        SheepsheadManagerApplication.getInstance().unregisterServices();
        removeActivitySpecificServices();
    }

    @Override
    public void onStop() {
        /*
        onStop is called when the application gets terminated
         */
        super.onStop();
        SheepsheadManagerApplication.getInstance().saveApplicationState();
    }

    /**
     * Add/Register UserInterface-Listener or specific services here
     */
    protected abstract void registerActivitySpecificServices();

    /**
     * Remove/unbind UserInterface-Listener or specific services here
     */
    protected abstract void removeActivitySpecificServices();

    /**
     * Create the userinterface for this activity here.
     * Use <code>setContentView(R.activity_name)</code> when using a layout.xml
     *
     * @param savedInstanceState {@link AppCompatActivity#onCreate(Bundle)}
     */
    protected abstract void createUserInterface(Bundle savedInstanceState);

    protected abstract void updateUserInterface();

    /**
     * Utility method for {@link #findViewById(int)}.
     * The return type is not a view, but will be casted into the type of the catching variable.
     * So a explicit cast is no longer needed:
     * <code>Button b = findView(someId)</code> instead of
     * <code>Button b = (Button)findViewById(someId)</code>
     *
     * @param id
     * @param <T> result type
     * @return
     */
    protected <T extends android.view.View> T findView(@android.support.annotation.IdRes int id) {
        return (T) findViewById(id);
    }
}
