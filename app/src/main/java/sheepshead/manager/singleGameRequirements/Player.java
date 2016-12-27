/*
 * Copyright 2016  Erik Pohle
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

package sheepshead.manager.singleGameRequirements;

/**
 * Created by Nicolas on 01.10.2016.
 */

public class Player {

    private String name;

    private int number; //it should always start with 0 for the first player, 1 for the second ...

    private int sessionMoney = 0;


    public Player(String name, int number) {
        this.name = name;
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public int getNumber() {
        return number;
    }


    public int getSessionMoney() {
        return sessionMoney;
    }

    public void setSessionMoney(int sessionMoney) {
        this.sessionMoney = sessionMoney;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Player \"");
        stringBuilder.append(name);
        stringBuilder.append("\"(");
        stringBuilder.append(number);
        stringBuilder.append(')');
        return stringBuilder.toString();
    }

}
