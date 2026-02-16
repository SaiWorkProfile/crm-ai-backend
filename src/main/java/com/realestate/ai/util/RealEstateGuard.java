package com.realestate.ai.util;

import java.util.List;
import java.util.regex.Pattern;

public class RealEstateGuard {

    private static final List<String> PROPERTY_WORDS = List.of(
        // English
        "property","flat","apartment","villa","plot","land","house","home",
        // Hindi
        "मकान","फ्लैट","जमीन","प्लॉट",
        // Telugu
        "ఇల్లు","ఫ్లాట్","విల్లా","ప్లాట్",
        // Tamil
        "வீடு","பிளாட்","வில்லா","நிலம்",
        // Kannada
        "ಮನೆ","ಫ್ಲಾಟ್","ವಿಲ್ಲಾ","ಭೂಮಿ",
        // Marathi
        "घर","फ्लॅट","जमीन"
    );

    private static final Pattern BHK_PATTERN =
        Pattern.compile("\\b[1-9]\\s*bhk\\b");

    private static final Pattern PRICE_PATTERN =
        Pattern.compile("(lakh|lakhs|crore|cr)");

    public static boolean isRealEstateQuery(String message) {

        if (message == null || message.isBlank()) {
            return false;
        }

        String lower = message.toLowerCase();

        for (String word : PROPERTY_WORDS) {
            if (lower.contains(word)) return true;
        }

        if (BHK_PATTERN.matcher(lower).find()) return true;
        if (PRICE_PATTERN.matcher(lower).find()) return true;
        if (IndianCities.containsCity(lower)) return true;

        return false;
    }
}
