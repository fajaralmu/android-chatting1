package com.fajar.android.chatting1.util;

import java.util.ArrayList;
import java.util.List;

public class ComponentUtil {
    public static List<Integer> generateButtonValues(int limit, int totalData, int currentPage) {

        /* DISPLAYED BUTTONS */
        List<Integer> displayed_buttons = new ArrayList<Integer>();
        int buttonCount = totalData % limit == 0 ? totalData / limit : totalData / limit + 1;
        int min = currentPage - 2;
        int max = currentPage + 2;
        if (buttonCount > 1) {
            displayed_buttons.add(1);
        }
        for (int i = min; i <= max; i++) {
            if (i > 1 && i <= buttonCount)
                displayed_buttons.add(i);
        }
        if(max < buttonCount){
            displayed_buttons.add(buttonCount);
        }

        return displayed_buttons;
    }


}
