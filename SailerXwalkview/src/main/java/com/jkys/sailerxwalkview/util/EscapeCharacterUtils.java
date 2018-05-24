package com.jkys.sailerxwalkview.util;

import android.text.TextUtils;

/**
 * Created by on
 * Author: Zern
 * DATE: 17/12/12
 * Time: 16:55
 * Email:AndroidZern@163.com
 */

public class EscapeCharacterUtils {

    /**
     * @param origin 原来的字符串
     * @return 把所有的字符串中有转义字符的都加\ (以下例子从计算机打印角度来看)如：\r => \\r; \n => \\n...
     */
    public static String changeCharacter (String origin) {
        if (!TextUtils.isEmpty(origin)) {
            origin = origin.replaceAll("\\\\", "\\\\\\\\");
        }
        return origin;
    }
}
