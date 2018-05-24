package com.jkys.sailerxwalkview.action;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by on
 * Author: Zern
 * DATE: 17/4/27
 * Time: 16:59
 * Email:AndroidZern@163.com
 */

public class ActionManager {
    private static List<SailerActionHandler> actions = new LinkedList<>();

    public static List<SailerActionHandler> getActions() {
        return actions;
    }
}
