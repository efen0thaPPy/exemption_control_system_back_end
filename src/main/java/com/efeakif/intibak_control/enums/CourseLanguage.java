package com.efeakif.intibak_control.enums;

import java.util.Locale;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum CourseLanguage {
    TURKISH("Türkçe"), ENGLISH("İngilizce");

    private final String turkishLabel;

    CourseLanguage(String turkishLabel) {
        this.turkishLabel = turkishLabel;
    }

    public String getTurkishLabel() {
        return turkishLabel;
    }

    @JsonCreator
    public static CourseLanguage fromString(String text) {
        String filteredText = text.trim().toLowerCase(Locale.forLanguageTag("tr"));

        return filteredText.contains("tr") || filteredText.contains("tur") || filteredText.contains("tür") ? TURKISH
                : ENGLISH;

    }

}
