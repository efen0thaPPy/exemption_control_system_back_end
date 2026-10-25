package com.efeakif.intibak_control.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.efeakif.intibak_control.entity.LLMExemptionReport;

public interface ExemptionRepo extends JpaRepository<LLMExemptionReport, Integer> {

}
