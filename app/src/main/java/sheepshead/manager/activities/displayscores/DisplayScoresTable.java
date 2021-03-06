/*
 * Copyright 2017  Erik Pohle
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

package sheepshead.manager.activities.displayscores;

import android.os.Bundle;
import android.view.View;

import sheepshead.manager.R;
import sheepshead.manager.appcore.AbstractBaseActivity;
import sheepshead.manager.appcore.ActivityDescriptor;
import sheepshead.manager.appcore.SheepsheadManagerApplication;
import sheepshead.manager.session.Session;

/**
 * Activity that lists the results of all game results of the current session in a table-style
 */
public class DisplayScoresTable extends AbstractBaseActivity {

    /**
     * Describes static behaviour of this activity
     */
    private static final ActivityDescriptor DISPLAY_SCORES_TABLE = new ActivityDescriptor(R.layout.activity_display_scores_table)
            .toolbar(R.id.DisplayScoresTable_toolbar)
            .title(R.string.Title_DisplayScoresTable)
            .enableNavigationBackToParent();
    /**
     * The current session
     */
    private Session session;

    /**
     * The dynamic_table layout container
     */
    private View tableContainer;

    /**
     * Don't create this activity by hand. Use an intent
     */
    public DisplayScoresTable() {
        super(DISPLAY_SCORES_TABLE);
        session = SheepsheadManagerApplication.getInstance().getCurrentSession();
        if (session == null) {
            throw new IllegalStateException(DisplayScoresTable.class.getSimpleName() + " cannot find session");
        }
    }

    @Override
    protected void updateUserInterface() {
        //Populate table
        ScoreBoardBuilder builder = new ScoreBoardBuilder(this, tableContainer);
        builder.addHeader(session.getPlayers());
        builder.addBody(session, session.getPlayers());
        builder.build();
    }

    @Override
    protected void registerActivitySpecificServices() {

    }

    @Override
    protected void removeActivitySpecificServices() {

    }

    @Override
    protected void createUserInterface(Bundle savedInstanceState) {
        tableContainer = findView(R.id.DisplayScoresTable_table);
        //Populating is done in update
    }
}
