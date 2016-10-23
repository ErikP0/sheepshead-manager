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

import android.app.Application;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import sheepshead.manager.sessionRequirements.Session;

/**
 * Singleton class for the general application behaviour.
 * It can be used by calling <code>SheepsheadManagerApplication.getInstance()</code>.
 */
public final class SheepsheadManagerApplication extends Application {

    /**
     * singleton instance
     */
    private static SheepsheadManagerApplication singleton;
    private
    @Nullable
    Session currentSession;

    /**
     * Returns the singleton instance.
     *
     * @return Instance of SheepsheadManagerApplication
     */
    public static
    @NonNull
    SheepsheadManagerApplication getInstance() {
        return singleton;
    }

    @Override
    public void onCreate() {
        //this is called at the beginning of the lifetime of this application
        super.onCreate();
        singleton = this;
        startup();
    }

    @Override
    public void onTerminate() {
        //this is called at the end of the lifetime of this application
        super.onTerminate();
        saveApplicationState();
    }

    /**
     * Called by {@link AbstractBaseActivity} whenever the user gained focus (again) for the current activity.
     * It is recommended to bind to services here (see http://www.vogella.com/tutorials/AndroidLifeCycle/article.html#the-live-cycle-methods)
     */
    public void registerServices() {
        //no service to register yet
    }

    /**
     * Called by {@link AbstractBaseActivity} whenever the user loses focus for the current activity.
     * It is recommended to unbind services here
     */
    public void unregisterServices() {
        //no service to unregister yet
    }

    /**
     * Called by {@link AbstractBaseActivity} when the application is terminating
     */
    public void saveApplicationState() {
        //nothing to save yet
        System.out.println("Save");
    }

    /**
     * Called by {@link sheepshead.manager.main.LoadingScreen} when the application is starting
     * Can be used to read persistent data
     */
    public void loadingScreen() {
        //nothing to load at the start of this app
        System.out.println("Loading");
    }

    /**
     * This method will be called upon the very beginning of application start.
     * It is intended to add code here that initializes/creates static, global or singleton-like instances for the data model
     */
    void startup() {
        //single-run code on startup here
        System.out.println("Startup");
    }

    public
    @Nullable
    Session getCurrentSession() {
        return currentSession;
    }

    public void setCurrentSession(@NonNull Session session) {
        currentSession = session;
    }
}
