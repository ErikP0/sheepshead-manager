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

package sheepshead.manager.activities;


import android.os.Bundle;

import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.Series;

import java.text.NumberFormat;
import java.util.Iterator;

import sheepshead.manager.R;
import sheepshead.manager.appcore.AbstractBaseActivity;
import sheepshead.manager.appcore.ActivityDescriptor;
import sheepshead.manager.appcore.SheepsheadManagerApplication;
import sheepshead.manager.game.Player;
import sheepshead.manager.game.PlayerRole;
import sheepshead.manager.game.SingleGameResult;
import sheepshead.manager.session.Session;
import sheepshead.manager.utils.ColorManagement;

public class DiagramActivity extends AbstractBaseActivity {

    private static final ActivityDescriptor DIAGRAM_ACTIVITY =
            new ActivityDescriptor(R.layout.activity_diagram)
                    .enableNavigationBackToParent()
                    .toolbar(R.id.DiagramActivity_toolbar);

    private GraphView diagramView;

    public DiagramActivity() {
        super(DIAGRAM_ACTIVITY);
    }

    @Override
    protected void registerActivitySpecificServices() {

    }

    @Override
    protected void removeActivitySpecificServices() {

    }

    @Override
    protected void createUserInterface(Bundle savedInstanceState) {
        diagramView = findView(R.id.DiagramActivity_graph_view);
        // activate horizontal zooming and scrolling
        diagramView.getViewport().setScalable(true);
        // activate horizontal scrolling
        diagramView.getViewport().setScrollable(true);
        // activate horizontal and vertical zooming and scrolling
        diagramView.getViewport().setScalableY(true);
        // activate vertical scrolling
        diagramView.getViewport().setScrollableY(true);

        Session session = SheepsheadManagerApplication.getInstance().getCurrentSession();


        for (Player player : session.getPlayers()) {
            diagramView.addSeries(createLineSeriesFor(session, player));
        }
        diagramView.getLegendRenderer().setVisible(true);
        diagramView.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);

        NumberFormat nf = NumberFormat.getInstance();
        nf.setMinimumFractionDigits(0);
        nf.setMaximumFractionDigits(0);
        nf.setMinimumIntegerDigits(1);
        diagramView.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter(nf, nf));
    }

    @Override
    protected void updateUserInterface() {

    }

    private Series<DataPoint> createLineSeriesFor(Session session, Player player) {
        DataPoint[] points = new DataPoint[session.getGameAmount()];
        Iterator<SingleGameResult> it = session.iterator();
        int last = 0;
        int game = 0;
        while (it.hasNext()) {
            PlayerRole role = it.next().findRole(player);
            if (role != null) {
                last = role.getPlayerBalance();
            }
            points[game] = new DataPoint(game, last);
            game++;
        }
        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(points);
        series.setTitle(player.getName());
        series.setColor(ColorManagement.getColorForPlayer(player));
        series.setAnimated(true);
        series.setDrawDataPoints(true);
        series.setDataPointsRadius(3f);
        series.setThickness(2);
        return series;
    }
}
