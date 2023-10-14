package dev.duma.android.usbscale.enums;

import java.util.HashMap;
import java.util.Map;

public enum StatusEnum {
    Fault("fault"),
    Zero("zero"),
    InMotion("in-motion"),
    Stable("stable"),
    UnderZero("under-zero"),
    OverWeight("over-weight"),
    NeedCalibration("need-calibration"),
    NeedZeroing("need-zeroing"),
    Unknown("unknown");


    private final String name;
    private static final Map<String, StatusEnum> map = new HashMap<>();

    StatusEnum(String name) {
        this.name = name;
    }

    static {
        for (StatusEnum e : StatusEnum.values()) {
            map.put(e.name, e);
        }
    }

    public static StatusEnum nameOf(String name) {
        if(! map.containsKey(name)) {
            throw new RuntimeException("There is no StatusEnum for name of: "+name);
        }

        return map.get(name);
    }

    public String getName() {
        return name;
    }
}
