package com.efeakif.intibak_control.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum Grade {
    AA, BA, BB, CB, CC, DC, DD, FD, FF, S, U, M, ET,;

    @JsonCreator
    public static Grade mapGrades(String text) {
        return switch (text.trim().toUpperCase()) {
            case "AA", "A+", "A" -> AA;
            case "BA", "A-", "B+" -> BA;
            case "BB", "B" -> BB;
            case "CB", "B-", "C+" -> CB;
            case "CC", "C" -> CC;
            case "DC", "C-", "D+" -> DC;
            case "DD", "D", "D-" -> DD;
            case "FD" -> FD;
            case "FF", "F", "FX", "NA", "DZ", "W", "I", "U" -> FF;
            case "S", "P", "BAŞARILI", "BASARILI" -> S;
            case "M", "EX", "MUAF" -> M;
            case "ET" -> ET;
            default -> FF;
        };
    }

    public boolean isPassing() {
        return switch (this) {
            case AA, BA, BB, CB, CC, S, M, ET -> true;
            case DC, DD, FD, FF, U -> false;
        };
    }

}
