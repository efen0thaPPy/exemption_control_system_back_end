package com.efeakif.intibak_control.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.efeakif.intibak_control.entity.ExemptionReport;

public interface ExemptionRepo extends JpaRepository<ExemptionReport, Integer> {

}
