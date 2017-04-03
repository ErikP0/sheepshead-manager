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

package sheepshead.manager.diagram;


import android.content.Context;

import java.util.Collection;
import java.util.LinkedList;

public interface DiagramFactory {

    DiagramViewSupplier buildDiagram(Context context, DiagramData data);

    class DiagramData {
        private Collection<BarDiagramData> barDiagrams;
        private Collection<LineDiagramData> lineDiagrams;
        private Collection<PieDiagramData> pieDiagrams;

        public DiagramData() {
            barDiagrams = new LinkedList<>();
            lineDiagrams = new LinkedList<>();
            pieDiagrams = new LinkedList<>();
        }

        public void add(BarDiagramData diagram) {
            barDiagrams.add(diagram);
        }

        public void add(LineDiagramData diagram) {
            lineDiagrams.add(diagram);
        }

        public void add(PieDiagramData diagram) {
            pieDiagrams.add(diagram);
        }

        public Collection<BarDiagramData> getBarDiagrams() {
            return barDiagrams;
        }

        public Collection<LineDiagramData> getLineDiagrams() {
            return lineDiagrams;
        }

        public Collection<PieDiagramData> getPieDiagrams() {
            return pieDiagrams;
        }
    }


}
