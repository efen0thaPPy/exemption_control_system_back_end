package com.efeakif.intibak_control.enums;

import java.util.Locale;

public enum CourseType {
    COMMON_MANDATORY("Ortak Zorunlu Ders"), MANDATORY("Zorunlu"), ELECTIVE("Seçmeli");

    private String turkishLabel;

    CourseType(String turkishLabel) {
        this.turkishLabel = turkishLabel;
    }

    public String getTurkishLabel() {
        return turkishLabel;
    }

    public static CourseType fromWeb(String text) {
        if (text == null)
            return MANDATORY;

        String[] courseCodeSplit = text.split(" ");

        if (courseCodeSplit == null)
            return MANDATORY;

        String courseCode = courseCodeSplit[0];

        String cleaned = courseCode.trim().toLowerCase(Locale.forLanguageTag("tr"));

        if (cleaned.contains("bz"))
            return MANDATORY;
        else if (cleaned.contains("bs"))
            return ELECTIVE;
        else
            return COMMON_MANDATORY;

    }

    // public static CourseType fromTranscript(String text) {
    // if (text == null)
    // return MANDATORY;

    // String[] courseCodeSplit = text.split("_ ");

    // if (courseCodeSplit == null) {
    // return MANDATORY;
    // }
    // String courseCode = courseCodeSplit[1];

    // String cleaned = courseCode.trim().toLowerCase(Locale.forLanguageTag("tr"));

    // if (cleaned.contains("10305"))
    // return COMMON_MANDATORY;

    // else if (cleaned.contains("bs"))
    // return ELECTIVE;

    // else
    // return MANDATORY;

    // }

}
