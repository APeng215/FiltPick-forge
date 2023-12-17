package net.apeng.filtpick.util;

import com.electronwill.nightconfig.core.conversion.InvalidValueException;

public class TypeTranslator {

    public static boolean intToBool(int i) {
        switch (i) {
            case 0 -> {
                return false;
            }
            case 1 -> {
                return true;
            }
        }
        throw new InvalidValueException("Can't translate int type to bool type!");
    }

    public static int boolToInt(boolean i) {
        if (i) return 1;
        else return 0;
    }
}


