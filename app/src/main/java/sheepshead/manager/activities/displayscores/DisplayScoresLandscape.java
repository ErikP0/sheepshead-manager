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

package sheepshead.manager.activities.displayscores;

import android.os.Bundle;
import android.widget.TableLayout;

import sheepshead.manager.R;
import sheepshead.manager.appcore.AbstractBaseActivity;
import sheepshead.manager.appcore.SheepsheadManagerApplication;
import sheepshead.manager.session.Session;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class DisplayScoresLandscape extends AbstractBaseActivity {
    private Session session;

    public DisplayScoresLandscape() {
        super();
        session = SheepsheadManagerApplication.getInstance().getCurrentSession();
        if (session == null) {
            throw new IllegalStateException(DisplayScoresLandscape.class.getSimpleName() + " cannot find session");
        }
    }

    @Override
    protected void registerActivitySpecificServices() {

    }

    @Override
    protected void removeActivitySpecificServices() {

    }

    @Override
    protected void createUserInterface(Bundle savedInstanceState) {
        setContentView(R.layout.activity_display_scores_landscape);

        //Populate table
        TableLayout header = findView(R.id.DisplayScoresLandscape_header);
        TableLayout scoreboard = findView(R.id.DisplayScoresLandscape_table);
        ScoreBoardBuilder builder = new ScoreBoardBuilder(this, header, scoreboard);
        builder.addHeader(session.getPlayers());
        builder.addBody(session, session.getPlayers());
        builder.build();
    }
}
