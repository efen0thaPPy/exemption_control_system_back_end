package com.efeakif.intibak_control.entity;

import jakarta.persistence.Embeddable;
import lombok.Data;

@Embeddable
@Data
public class Student {

    private String firstName;

    private String lastName;

    private String university;

}
