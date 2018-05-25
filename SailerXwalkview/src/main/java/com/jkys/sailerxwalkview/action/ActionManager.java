package com.jkys.sailerxwalkview.action;

import java.util.HashMap;

/**
 * Created by on
 * Author: Zern
 * DATE: 17/4/27
 * Time: 16:59
 * Email:AndroidZern@163.com
 */

public class ActionManager {
    private static HashMap<String, SailerActionHandler> actions = new HashMap<>();

    public static HashMap<String, SailerActionHandler> getActionsMap() {
        return actions;
    }
}
