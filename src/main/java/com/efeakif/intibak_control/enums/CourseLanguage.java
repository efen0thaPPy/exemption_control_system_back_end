package com.efeakif.intibak_control.enums;

import java.util.Locale;

public enum CourseLanguage {
    TURKISH("Türkçe"), ENGLISH("İngilizce");

    private final String turkishLabel;

    CourseLanguage(String turkishLabel) {
        this.turkishLabel = turkishLabel;
    }

    public String getTurkishLabel() {
        return turkishLabel;
    }

    public CourseLanguage fromString(String text) {
        String filteredText = text.trim().toLowerCase(Locale.forLanguageTag("tr"));

        return filteredText.contains("tr") ? TURKISH : ENGLISH;

    }

}
