package com.realestate.ai.util;

public class LeadIntentAnalyzer {

    /*
     SCORE RULES
     +2 → property type
     +2 → bhk
     +2 → budget / price
     +2 → city
     +1 → intent words
    */

    public static int score(String message) {

        int score = 0;
        String lower = message.toLowerCase();

        if (lower.contains("flat") || lower.contains("villa")
            || lower.contains("plot") || lower.contains("house")
            || lower.contains("apartment")) {
            score += 2;
        }

        if (lower.matches(".*\\b[1-9]\\s*bhk\\b.*")) {
            score += 2;
        }

        if (lower.matches(".*(lakh|lakhs|crore|cr).*")) {
            score += 2;
        }

        if (IndianCities.containsCity(lower)) {
            score += 2;
        }

        if (lower.contains("buy") || lower.contains("rent")
            || lower.contains("looking") || lower.contains("interested")) {
            score += 1;
        }

        return score;
    }

    public static boolean isStrongLead(String message) {
        return score(message) >= 5;
    }
}
