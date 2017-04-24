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
import android.view.View;
import android.widget.Button;
import android.widget.ViewFlipper;

import sheepshead.manager.R;
import sheepshead.manager.appcore.AbstractBaseActivity;
import sheepshead.manager.appcore.ActivityDescriptor;
import sheepshead.manager.appcore.SheepsheadManagerApplication;
import sheepshead.manager.diagram.DiagramDataCreator;
import sheepshead.manager.diagram.DiagramFactory;
import sheepshead.manager.diagram.androidview.AVFactory;
import sheepshead.manager.diagram.diagramtypes.MoneyBalanceDiagram;
import sheepshead.manager.diagram.diagramtypes.WinLoseDiagram;
import sheepshead.manager.session.Session;
import sheepshead.manager.uicontrolutils.SwipeListener;
import sheepshead.manager.utils.Consumer;

public class DiagramActivity extends AbstractBaseActivity implements Consumer<SwipeListener.SwipeDirection> {

    private static final ActivityDescriptor DIAGRAM_ACTIVITY =
            new ActivityDescriptor(R.layout.activity_diagram)
                    .enableNavigationBackToParent()
                    .toolbar(R.id.DiagramActivity_toolbar);

    private static final DiagramFactory FACTORY = new AVFactory();
    private final DiagramDataCreator[] diagramTypes = {new WinLoseDiagram(), new MoneyBalanceDiagram()};
    private ViewFlipper flipper;

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
        flipper = findView(R.id.DiagramActivity_view_container);

        SwipeListener swipeListener = new SwipeListener();
        swipeListener.addListener(SwipeListener.SwipeDirection.LEFT, this);
        swipeListener.addListener(SwipeListener.SwipeDirection.RIGHT, this);
        flipper.setOnGenericMotionListener(swipeListener);
        Session session = SheepsheadManagerApplication.getInstance().getCurrentSession();

        for (DiagramDataCreator creator : diagramTypes) {
            View diagramView = FACTORY.buildDiagram(this, creator, session).getDiagramView();
            flipper.addView(diagramView);
            diagramView.setOnGenericMotionListener(swipeListener);
        }

        Button prev = findView(R.id.DiagramActivity_btn_prev);
        Button next = findView(R.id.DiagramActivity_btn_next);
        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                accept(SwipeListener.SwipeDirection.LEFT);
            }
        });
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                accept(SwipeListener.SwipeDirection.RIGHT);
            }
        });

//        for (Player player : session.getPlayers()) {
//            diagramView.addSeries(createLineSeriesFor(session, player));
//        }
//
//
//        NumberFormat nf = NumberFormat.getInstance();
//        nf.setMinimumFractionDigits(0);
//        nf.setMaximumFractionDigits(0);
//        nf.setMinimumIntegerDigits(1);
//        diagramView.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter(nf, nf));
    }

    @Override
    protected void updateUserInterface() {

    }

    @Override
    public void accept(SwipeListener.SwipeDirection direction) {
        if (direction.equals(SwipeListener.SwipeDirection.RIGHT)) {
            flipper.setInAnimation(this, R.anim.in_left);
            flipper.setOutAnimation(this, R.anim.out_right);
            flipper.showPrevious();
        } else if (direction.equals(SwipeListener.SwipeDirection.LEFT)) {
            flipper.setInAnimation(this, R.anim.in_right);
            flipper.setOutAnimation(this, R.anim.out_left);
            flipper.showNext();
        }
    }
}
