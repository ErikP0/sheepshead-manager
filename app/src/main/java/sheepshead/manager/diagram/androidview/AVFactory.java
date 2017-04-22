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

package sheepshead.manager.diagram.androidview;

import android.content.Context;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sheepshead.manager.diagram.BarDiagramData;
import sheepshead.manager.diagram.DiagramFactory;
import sheepshead.manager.diagram.DiagramViewSupplier;
import sheepshead.manager.diagram.LineDiagramData;


public class AVFactory implements DiagramFactory {
    private static final Comparator<DataPoint> X_COMP = new Comparator<DataPoint>() {
        @Override
        public int compare(DataPoint o1, DataPoint o2) {
            return Double.compare(o1.getX(), o2.getX());
        }
    };

    @Override
    public DiagramViewSupplier buildDiagram(Context context, DiagramData data) {
        GraphView diagramView = new GraphView(context);
        // activate horizontal zooming and scrolling
        diagramView.getViewport().setScalable(true);
        // activate horizontal scrolling
        diagramView.getViewport().setScrollable(true);
        // activate horizontal and vertical zooming and scrolling
        diagramView.getViewport().setScalableY(true);
        // activate vertical scrolling
        diagramView.getViewport().setScrollableY(true);
        diagramView.getLegendRenderer().setVisible(true);
        diagramView.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);

        for (LineDiagramData line : data.getLineDiagrams()) {
            handleLineDiagram(line, diagramView);
        }
        for (BarDiagramData barDiagrams : data.getBarDiagrams()) {
            handleBarDiagram(barDiagrams, diagramView);
        }
        return new DiagramViewSupplier.SimpleViewSupplier(diagramView);
    }

    private void handleLineDiagram(LineDiagramData lineData, GraphView diagramView) {
        for (LineDiagramData.LineGraphData line : lineData) {
            //TODO optimize: use array instead of arraylist in first place
            ArrayList<DataPoint> points = new ArrayList<>();
            for (LineDiagramData.LineGraphDataPoint point : line) {
                points.add(new DataPoint(point.getXValue(), point.getYValue()));
            }

            Collections.sort(points, X_COMP);
            LineGraphSeries<DataPoint> series = new LineGraphSeries<>(points.toArray(new DataPoint[points.size()]));
            series.setAnimated(line.isAnimated());
            series.setThickness(Math.round(line.getThickness()));
            series.setDataPointsRadius(Math.round(line.getPointRadius()));
            series.setDrawDataPoints(line.drawDataPoints());
            series.setDrawAsPath(line.hasConnectingLines());
            series.setColor(line.getColor());
            series.setTitle(line.getLabel());
            diagramView.addSeries(series);
        }
        diagramView.setTitle(lineData.getDiagramTitle());
    }

    private void handleBarDiagram(BarDiagramData barData, GraphView diagramView) {
        double groupSpacing = barData.getSpace();
        double position = 1;
        Map<ColorLabelKey, BarSeriesHelper> series = new HashMap<>();
        for (BarDiagramData.BarGroup group : barData) {
            for (BarDiagramData.BarData bar : group) {
                ColorLabelKey key = new ColorLabelKey(bar.getColor(), bar.getLabel());
                BarSeriesHelper helper = series.get(key);
                if (helper == null) {
                    helper = new BarSeriesHelper();
                    series.put(key, helper);
                }

                helper.addBar(position, bar);
                position += group.getSpace();
            }
            position += groupSpacing;
        }

        for (BarSeriesHelper helper : series.values()) {
            diagramView.addSeries(helper.consume());
        }
        diagramView.setTitle(barData.getTitle());
    }

    private class BarSeriesHelper {
        private List<Double> positions;
        private List<BarDiagramData.BarData> bars;

        public BarSeriesHelper() {
            positions = new ArrayList<>(2);
            bars = new ArrayList<>(2);
        }

        public void addBar(double position, BarDiagramData.BarData bar) {
            positions.add(position);
            bars.add(bar);
        }

        public BarGraphSeries<DataPoint> consume() {
            DataPoint[] points = new DataPoint[positions.size()];
            for (int i = 0; i < points.length; i++) {
                points[i] = new DataPoint(positions.get(i), bars.get(i).getHeight());
            }
            BarGraphSeries<DataPoint> series = new BarGraphSeries<>(points);
            if (!positions.isEmpty()) {
                BarDiagramData.BarData first = bars.get(0);
                series.setTitle(first.getLabel());
                series.setColor(first.getColor());
            }
            return series;
        }
    }

    private class ColorLabelKey {
        private int color;
        private String label;

        public ColorLabelKey(int c, String s) {
            color = c;
            label = s;
        }

        @Override
        public int hashCode() {
            int hash = 31;
            hash += hash * color;
            hash += hash * label.hashCode();
            return hash;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || !(o instanceof ColorLabelKey)) {
                return false;
            }
            ColorLabelKey other = (ColorLabelKey) o;
            return color == other.color && label.equals(other.label);
        }
    }
}
