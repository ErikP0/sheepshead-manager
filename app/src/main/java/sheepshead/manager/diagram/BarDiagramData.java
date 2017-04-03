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


import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import sheepshead.manager.utils.CollectionUtils;

public class BarDiagramData implements Iterable<BarDiagramData.BarGroup> {

    private String title;
    private Collection<BarGroup> groups;
    private double space;

    public BarDiagramData(String title, double space) {
        this.title = CollectionUtils.nonNull(title);
        this.space = space;
        groups = new LinkedList<>();
    }

    @Override
    public Iterator<BarGroup> iterator() {
        return groups.iterator();
    }

    public String getTitle() {
        return title;
    }

    public double getSpace() {
        return space;
    }

    public void addGroup(BarGroup group) {
        groups.add(group);
    }

    public static class BarGroup implements Iterable<BarData> {
        private List<BarData> bars;
        private String groupLabel;
        private double space;

        public BarGroup(String label, double space) {
            groupLabel = CollectionUtils.nonNull(label);
            this.space = space;
            bars = new LinkedList<>();
        }

        public String getGroupLabel() {
            return groupLabel;
        }

        public double getSpace() {
            return space;
        }

        public void addBar(BarData bar) {
            bars.add(bar);
        }

        @Override
        public Iterator<BarData> iterator() {
            return bars.iterator();
        }
    }

    public static class BarData {
        private double height;
        private int color;
        private String label;

        public BarData(double height, int color, String label) {
            this.height = height;
            this.color = color;
            this.label = CollectionUtils.nonNull(label);
        }

        public double getHeight() {
            return height;
        }

        public int getColor() {
            return color;
        }

        public String getLabel() {
            return label;
        }
    }
}
