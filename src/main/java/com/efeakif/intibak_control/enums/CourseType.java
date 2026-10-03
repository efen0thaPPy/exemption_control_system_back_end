package com.efeakif.intibak_control.enums;

import java.util.Locale;


public enum CourseType {
    MANDATORY("Zorunlu"), ELECTIVE("Seçmeli");

    private final String turkishLabel;

    CourseType(String turkishLabel) {
        this.turkishLabel = turkishLabel;
    }

    public String getTurkishLabel() {
        return turkishLabel;
    }

    public static CourseType fromString(String text){
        if(text==null)
            return MANDATORY;

        String cleaned=text.trim().toLowerCase(Locale.forLanguageTag("tr"));

        if(cleaned.contains("seç")||cleaned.contains("s"))
            return ELECTIVE;

        return MANDATORY;


    }

}
