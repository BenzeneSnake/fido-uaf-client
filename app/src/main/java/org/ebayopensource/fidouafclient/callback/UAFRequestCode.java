package org.ebayopensource.fidouafclient.callback;

public enum UAFRequestCode {

    DISCOVERY_INFO_1(1, "Discover info"),
    REG_ACTIVITY_RES_3(3, "Register Response"),
    AUTH_ACTIVITY_RES_5(5, "Authentication Response"),
    DEREG_ACTIVITY_RES_4(4, "Deregister Response");

    private final int code;
    private final String description;

    UAFRequestCode(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public static String getDescriptionByCode(int resCode) {
        for (UAFRequestCode uafCode : values()) {
            if (uafCode.code == resCode) {
                return uafCode.description;
            }
        }
        return "UNKNOWN";
    }

}

