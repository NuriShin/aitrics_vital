package com.vital.infra.repository;

import com.vital.domain.model.Vital;
import com.vital.domain.enumtype.VitalType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface VitalRepository extends JpaRepository<Vital, Long> {
    
    List<Vital> findByPatientIdAndRecordedAtBetweenOrderByRecordedAt(
        String patientId, LocalDateTime from, LocalDateTime to);
    
    List<Vital> findByPatientIdAndVitalTypeAndRecordedAtBetweenOrderByRecordedAt(
        String patientId, VitalType vitalType, LocalDateTime from, LocalDateTime to);
}