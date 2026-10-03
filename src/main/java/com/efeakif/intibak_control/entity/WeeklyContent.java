package com.efeakif.intibak_control.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;

import jakarta.persistence.Table;
import lombok.Data;

@Table(name = "weekly_contents")
@Entity
@Data
public class WeeklyContent {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "weekly_content_id")
    private int id;

    @ManyToOne
    @JoinColumn(name = "course_code")
    private Course course;

    @Column(nullable = false)
    private int weekNumber;

    @Column(columnDefinition = "TEXT")
    private String description;

}