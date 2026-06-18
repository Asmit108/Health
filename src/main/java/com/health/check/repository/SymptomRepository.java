package com.health.check.repository;

import com.health.check.models.SymptomReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SymptomRepository extends JpaRepository<SymptomReport, Integer> {
}
