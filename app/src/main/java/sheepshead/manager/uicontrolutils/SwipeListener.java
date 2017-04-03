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

package sheepshead.manager.uicontrolutils;


import android.view.MotionEvent;
import android.view.View;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import sheepshead.manager.utils.Consumer;

public class SwipeListener implements View.OnGenericMotionListener {

    private Map<SwipeDirection, List<Consumer<SwipeDirection>>> listeners;
    private float lastX;
    private float lastY;

    public SwipeListener() {
        listeners = new TreeMap<>();
    }

    @Override
    public boolean onGenericMotion(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //finger down
                lastX = event.getX();
                lastY = event.getY();
                break;
            case MotionEvent.ACTION_UP:
                //finger up
                SwipeDirection direction = SwipeDirection.getDirection(lastX, lastY, event.getX(), event.getY());
                fire(direction);
                break;
            default:
                //nothing to do, a motion we don't want to capture
        }
        return false;
    }

    public void addListener(SwipeDirection direction, Consumer<SwipeDirection> listener) {
        List<Consumer<SwipeDirection>> list = listeners.get(direction);
        if (list == null) {
            list = new LinkedList<Consumer<SwipeDirection>>();
            listeners.put(direction, list);
        }
        list.add(listener);
    }

    private void fire(SwipeDirection direction) {
        List<Consumer<SwipeDirection>> list = listeners.get(direction);
        if (list != null) {
            for (Consumer<SwipeDirection> listener : list) {
                listener.accept(direction);
            }
        }
    }


    public enum SwipeDirection {
        LEFT, RIGHT, UP, DOWN;

        private static SwipeDirection getDirection(float startX, float startY, float endX, float endY) {
            SwipeDirection dir = null;
            float distance = Integer.MIN_VALUE;
            if (startX <= endX && distance < Math.abs(endX - startX)) {
                //RIGHT
                dir = RIGHT;
                distance = Math.abs(endX - startX);
            }
            if (startX > endX && distance < Math.abs(endX - startX)) {
                //LEFT
                dir = LEFT;
                distance = Math.abs(endX - startX);
            }
            if (startY <= endY && distance < Math.abs(endY - startY)) {
                //DOWN
                dir = DOWN;
                distance = Math.abs(endY - startY);
            }
            if (startY > endY && distance < Math.abs(endY - startY)) {
                //UP
                dir = UP;
                distance = Math.abs(endY - startY);
            }
            return dir;
        }
    }
}
