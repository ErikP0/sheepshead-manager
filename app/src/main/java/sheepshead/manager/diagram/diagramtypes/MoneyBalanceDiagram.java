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
import com.jjoe64.graphview.LegendRenderer;

import java.util.Iterator;

import sheepshead.manager.diagram.DiagramDataCreator;
import sheepshead.manager.diagram.DiagramFactory;
import sheepshead.manager.diagram.LineDiagramData;
import sheepshead.manager.diagram.androidview.GameLabelFormatter;
import sheepshead.manager.game.Player;
import sheepshead.manager.game.PlayerRole;
import sheepshead.manager.game.SingleGameResult;
import sheepshead.manager.session.Session;
import sheepshead.manager.utils.ColorManagement;

public class MoneyBalanceDiagram implements DiagramDataCreator {

    public DiagramFactory.DiagramData createFromSession(Session s) {
        LineDiagramData diagram = new LineDiagramData("Geldverlauf");
        for (Player player : s.getPlayers()) {
            LineDiagramData.LineGraphData line = new LineDiagramData.LineGraphData(ColorManagement.getColorForPlayer(player));
            line.setDrawDataPoints(true);
            line.setAnimated(true);
            line.setHasConnectingLines(true);
            line.setLabel(player.getName());


            double balance = 0;
            Iterator<SingleGameResult> it = s.iterator();
            int gameNr = 1;
            while (it.hasNext()) {
                SingleGameResult result = it.next();
                PlayerRole role = result.findRole(player);
                if (role != null) {
                    balance = role.getPlayerBalance();
                }
                line.addPoint(new LineDiagramData.LineGraphDataPoint(gameNr, balance));
                gameNr++;
            }

            diagram.add(line);
        }
        DiagramFactory.DiagramData data = new DiagramFactory.DiagramData();
        data.add(diagram);
        return data;
    }

    @Override
    public void specialize(GraphView diagramView) {
        // activate horizontal zooming and scrolling
        diagramView.getViewport().setScalable(true);
        diagramView.getLegendRenderer().setVisible(true);
        diagramView.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);
        diagramView.getLegendRenderer().setBackgroundColor(Color.TRANSPARENT);
        diagramView.getGridLabelRenderer().setLabelFormatter(new GameLabelFormatter());
    }
}
