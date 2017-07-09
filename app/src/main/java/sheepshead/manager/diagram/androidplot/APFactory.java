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

package sheepshead.manager.diagram.androidplot;


import android.content.Context;
import android.view.View;

import com.androidplot.xy.XYPlot;

import sheepshead.manager.diagram.BarDiagramData;
import sheepshead.manager.diagram.DiagramFactory;
import sheepshead.manager.diagram.DiagramViewSupplier;

public class APFactory implements DiagramFactory {
    @Override
    public DiagramViewSupplier buildDiagram(Context context, DiagramData data) {
        final XYPlot diagram = new XYPlot(context, "");

        for (BarDiagramData barDiagram : data.getBarDiagrams()) {
            handle(diagram, barDiagram);
        }


        return new DiagramViewSupplier() {
            @Override
            public View getDiagramView() {
                return diagram;
            }
        };
    }

    private void handle(XYPlot diagram, BarDiagramData barDiagram) {
//        BarRenderer renderer = diagram.getRenderer(BarRenderer.class);
//        Map<ColorLabelKey, BarFormatter> barTypes = new HashMap<>();
//        for (BarDiagramData.BarGroup grp : barDiagram) {
//            for (BarDiagramData.BarData bar : grp) {
//                ColorLabelKey key = new ColorLabelKey(bar.getColor(), bar.getLabel());
//                if (!barTypes.containsKey(key)) {
//                    barTypes.put(key, new BarFormatter(bar.getColor(), ))
//                }
//            }
//        }

    }

    private static class ColorLabelKey {
        private int color;
        private String label;

        public ColorLabelKey(int c, String l) {
            color = c;
            label = l;
        }

        @Override
        public int hashCode() {
            int hash = 31;
            hash += hash * color;
            hash += hash * label.hashCode();
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null || !(obj instanceof ColorLabelKey)) {
                return false;
            }
            ColorLabelKey other = (ColorLabelKey) obj;
            return color == other.color && label.equals(other.label);
        }
    }
}
