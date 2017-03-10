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

package sheepshead.manager.game;


/**
 * The player class represents a participating player with name and money who is persistent through a
 * whole session
 */
public class Player {

    /**
     * Name of the player which is unique in the session
     */
    private String name;

    /**
     * current balance of this player
     */
    private int sessionMoney;

    /**
     * Creates a new player with the given name.
     * Note that the name must be unique in the session
     *
     * @param name The name of this player
     */
    public Player(String name) {
        this.name = name;
        sessionMoney = 0;
    }

    /**
     * @return The name of the player
     */
    public String getName() {
        return name;
    }

    /**
     * @return The current balance of the player
     */
    public int getSessionMoney() {
        return sessionMoney;
    }

    /**
     * Sets the current balance of this player
     *
     * @param sessionMoney The new balance
     */
    public void setSessionMoney(int sessionMoney) {
        this.sessionMoney = sessionMoney;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Player \"");
        stringBuilder.append(name);
        stringBuilder.append("\"(");
        stringBuilder.append(sessionMoney);
        stringBuilder.append(')');
        return stringBuilder.toString();
    }

}
