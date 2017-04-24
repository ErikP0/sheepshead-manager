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

package sheepshead.manager.diagram.diagramtypes;


import android.graphics.Color;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.Series;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import sheepshead.manager.diagram.BarDiagramData;
import sheepshead.manager.diagram.DiagramDataCreator;
import sheepshead.manager.diagram.DiagramFactory;
import sheepshead.manager.game.Player;
import sheepshead.manager.game.PlayerRole;
import sheepshead.manager.game.SingleGameResult;
import sheepshead.manager.session.Session;

public class WinLoseDiagram implements DiagramDataCreator {

    private List<String> playerNames;

    public DiagramFactory.DiagramData createFromSession(Session s) {
        playerNames = new ArrayList<>(s.getPlayers().size());
        BarDiagramData diagram = new BarDiagramData("Spiele gewonnen/verloren", 30);

        for (Player player : s.getPlayers()) {
            playerNames.add(player.getName());
            BarDiagramData.BarGroup group = new BarDiagramData.BarGroup(player.getName(), 30);

            int won = 0;
            int lost = 0;
            Iterator<SingleGameResult> it = s.iterator();
            while (it.hasNext()) {
                PlayerRole role = it.next().findRole(player);
                if (role != null) {
                    if (role.isWinner()) {
                        won++;
                    } else {
                        lost++;
                    }
                }
            }

            group.addBar(new BarDiagramData.BarData(won, Color.GREEN, "Gewonnen"));
            group.addBar(new BarDiagramData.BarData(lost, Color.RED, "Verloren"));

            diagram.addGroup(group);
        }
        DiagramFactory.DiagramData data = new DiagramFactory.DiagramData();
        data.add(diagram);

        return data;
    }

    @Override
    public void specialize(GraphView diagramView) {
        diagramView.getGridLabelRenderer().setGridStyle(GridLabelRenderer.GridStyle.NONE);
        diagramView.getGridLabelRenderer().setVerticalLabelsVisible(false);
        diagramView.getGridLabelRenderer().setHorizontalLabelsVisible(false);
        diagramView.getViewport().setScalable(true);
        diagramView.getLegendRenderer().setVisible(true);
        diagramView.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);
        for (Series series : diagramView.getSeries()) {
            BarGraphSeries bar = (BarGraphSeries) series;
            bar.setDrawValuesOnTop(true);
            bar.setValuesOnTopColor(Color.BLACK);
        }
    }
}
