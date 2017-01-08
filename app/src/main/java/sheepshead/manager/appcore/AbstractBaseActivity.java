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

import android.content.Intent;
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

    /**
     * The creation description of the specific activity
     */
    private final ActivityDescriptor activityDescriptor;
    /**
     * The toolbar of this activity (if available)
     */
    private ActionBar toolbar;

    /**
     * Creates the superclass for basic funtionality
     *
     * @param descriptor for description of static behaviour, title, toolbar, etc
     */
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

    /**
     * Enables the toolbar and sets the title (if available)
     */
    private void createToolbar() {
        if (!activityDescriptor.getToolbarId().isEmpty()) {
            //look for the toolbar widget in the given view container
            Toolbar bar = (Toolbar) findView(activityDescriptor.getToolbarId().getValue()).findViewById(R.id.toolbar);
            setSupportActionBar(bar);
            toolbar = getSupportActionBar();
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
     *
     * @param savedInstanceState {@link AppCompatActivity#onCreate(Bundle)}
     */
    protected abstract void createUserInterface(Bundle savedInstanceState);

    /**
     * Update the userinterface for this activity here.
     * Beware that the session object might have changed
     */
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

    /**
     * Performs an intent to a "up" activity (an activity that belongs on top of the backstack).
     * Consider {@link #finish()} if the activity wants to return to its parent/a activity on the backstack below it
     *
     * @param target
     */
    protected void intentToUpActivity(Class<? extends AbstractBaseActivity> target) {
        Intent intent = new Intent(this, target);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}
