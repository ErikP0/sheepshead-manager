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


public final class SheepsheadManagerApplication {

    private static SheepsheadManagerApplication singleton;

    private SheepsheadManagerApplication() {
        singleton = this;
    }

    public static void create() {
        if (singleton != null) {
            throw new IllegalStateException(SheepsheadManagerApplication.class.getSimpleName() + " is a singleton and can therefore only be created once");
        }
        singleton = new SheepsheadManagerApplication();
    }

    public static SheepsheadManagerApplication getInstance() {
        return singleton;
    }

    public void registerServices() {
        //no service to register yet
    }

    public void unregisterServices() {
        //no service to unregister yet
    }

    public void saveApplicationState() {
        //nothing to save yet
    }

    public void loadingScreen() {
        //nothing to load at the start of this app

    }

    void startup() {
        //single-run code on startup here
    }
}
