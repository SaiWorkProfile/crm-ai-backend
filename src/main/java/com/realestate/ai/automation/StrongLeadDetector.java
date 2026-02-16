package com.realestate.ai.automation;



public class StrongLeadDetector {

    private static final String[] KEYWORDS = {
        "book",
        "site visit",
        "final price",
        "ready to buy",
        "loan approved",
        "budget ready"
    };

    public static boolean isStrong(String msg) {

        msg = msg.toLowerCase();

        for (String k : KEYWORDS) {
            if (msg.contains(k))
                return true;
        }

        return false;
    }
}
