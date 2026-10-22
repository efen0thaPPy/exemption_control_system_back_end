package com.efeakif.intibak_control.dto.internal;

import com.efeakif.intibak_control.entity.SelectedCourse;
import com.efeakif.intibak_control.entity.Student;

public record StudentCourseDto(Student student, SelectedCourse selectedCourse) {
}
