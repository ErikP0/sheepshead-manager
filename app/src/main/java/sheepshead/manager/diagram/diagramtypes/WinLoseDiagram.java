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

import java.util.Iterator;

import sheepshead.manager.diagram.BarDiagramData;
import sheepshead.manager.diagram.DiagramDataCreator;
import sheepshead.manager.diagram.DiagramFactory;
import sheepshead.manager.game.Player;
import sheepshead.manager.game.PlayerRole;
import sheepshead.manager.game.SingleGameResult;
import sheepshead.manager.session.Session;

public class WinLoseDiagram implements DiagramDataCreator {

    public DiagramFactory.DiagramData createFromSession(Session s) {
        BarDiagramData diagram = new BarDiagramData("Spiele gewonnen/verloren", 30);

        for (Player player : s.getPlayers()) {
            BarDiagramData.BarGroup group = new BarDiagramData.BarGroup(player.getName(), 5);

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
            group.addBar(new BarDiagramData.BarData(-lost, Color.RED, "Verloren"));
            int ratioColor = won - lost > 0 ? Color.GREEN : Color.RED;
            group.addBar(new BarDiagramData.BarData(won - lost, ratioColor, "Verhältnis"));

            diagram.addGroup(group);
        }
        DiagramFactory.DiagramData data = new DiagramFactory.DiagramData();
        data.add(diagram);

        return data;
    }
}
