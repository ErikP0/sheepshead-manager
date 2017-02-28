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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import sheepshead.manager.serialization.CSVRules;
import sheepshead.manager.serialization.SerializationActions;
import sheepshead.manager.serialization.SessionDataCorruptedException;
import sheepshead.manager.session.InternalSessionReader;
import sheepshead.manager.session.InternalSessionWriter;
import sheepshead.manager.session.Session;

/**
 * Singleton class for the general application behaviour.
 * It can be used by calling <code>SheepsheadManagerApplication.getInstance()</code>.
 */
public final class SheepsheadManagerApplication extends Application {

    public static final CSVRules INTERNAL_LOAD_SAVE_RULE = new CSVRules(';', '"', "utf8", false, new InternalSessionWriter(), new InternalSessionReader());
    private static final String sessionSavePath = "latest_session.csv";

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
        if (currentSession != null) {
            File dir = getFilesDir();
            File saveTo = new File(dir, sessionSavePath);
            SerializationActions.saveSessionTo(currentSession, saveTo, INTERNAL_LOAD_SAVE_RULE);
        }
    }

    private void tryLoadSession() {
        File dir = getFilesDir();
        File loadFrom = new File(dir, sessionSavePath);
        if (loadFrom.exists() && currentSession == null) {
            try {
                currentSession = SerializationActions.loadSession(loadFrom, INTERNAL_LOAD_SAVE_RULE);
            } catch (FileNotFoundException e) {
                //should never be called (loadFrom.exists() -> true)
                throw new RuntimeException(e);
            } catch (IOException | SessionDataCorruptedException ignore) {
                // could not load last session -> ignore
                currentSession = null;
            }
        } else {
            System.out.println("No last session found");
        }
    }

    /**
     * Called by {@link sheepshead.manager.main.LoadingScreen} when the application is starting
     * Can be used to read persistent data
     */
    public void loadingScreen() {
        //nothing to load at the start of this app
        System.out.println("Loading");
        tryLoadSession();
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
