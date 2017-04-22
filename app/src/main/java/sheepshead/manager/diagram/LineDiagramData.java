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
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.SortedSet;
import java.util.TreeSet;

import sheepshead.manager.utils.CollectionUtils;

public class LineDiagramData implements Iterable<LineDiagramData.LineGraphData> {

    private String diagramTitle;
    private Collection<LineGraphData> lines;

    public LineDiagramData(String title) {
        diagramTitle = CollectionUtils.nonNull(title);
        lines = new LinkedList<>();
    }

    public void add(LineGraphData line) {
        lines.add(line);
    }

    public String getDiagramTitle() {
        return diagramTitle;
    }

    @Override
    public Iterator<LineGraphData> iterator() {
        return lines.iterator();
    }

    public static class LineGraphData implements Comparator<LineGraphDataPoint>, Iterable<LineGraphDataPoint> {
        private static final float DEFAULT_THICKNESS = 2;
        private static final float DEFAULT_POINT_RADIUS = 4;
        private String label;
        private int color;
        private boolean isAnimated;
        private boolean drawDataPoints;
        private boolean hasConnectingLines;
        private float thickness;
        private float pointRadius;

        private SortedSet<LineGraphDataPoint> points;

        public LineGraphData(int color) {
            this.color = color;
            points = new TreeSet<>(this);
            thickness = DEFAULT_THICKNESS;
            pointRadius = DEFAULT_POINT_RADIUS;
        }

        public float getThickness() {
            return thickness;
        }

        public void setThickness(float thickness) {
            this.thickness = thickness;
        }

        public float getPointRadius() {
            return pointRadius;
        }

        public void setPointRadius(float pointRadius) {
            this.pointRadius = pointRadius;
        }

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public int getColor() {
            return color;
        }

        public void setColor(int color) {
            this.color = color;
        }

        public boolean isAnimated() {
            return isAnimated;
        }

        public void setAnimated(boolean animated) {
            isAnimated = animated;
        }

        public boolean drawDataPoints() {
            return drawDataPoints;
        }

        public void setDrawDataPoints(boolean drawDataPoints) {
            this.drawDataPoints = drawDataPoints;
        }

        public boolean hasConnectingLines() {
            return hasConnectingLines;
        }

        public void setHasConnectingLines(boolean hasConnectingLines) {
            this.hasConnectingLines = hasConnectingLines;
        }

        public void addPoint(LineGraphDataPoint point) {
            points.add(point);
        }

        @Override
        public int compare(LineGraphDataPoint o1, LineGraphDataPoint o2) {
            return Double.compare(o1.getXValue(), o2.getYValue());
        }

        @Override
        public Iterator<LineGraphDataPoint> iterator() {
            return points.iterator();
        }
    }

    public static class LineGraphDataPoint {
        private double xValue;
        private double yValue;

        public LineGraphDataPoint(double x, double y) {
            xValue = x;
            yValue = y;
        }

        public double getXValue() {
            return xValue;
        }

        public double getYValue() {
            return yValue;
        }
    }
}
