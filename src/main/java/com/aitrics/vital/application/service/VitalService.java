package com.aitrics.vital.application.service;

import com.aitrics.vital.api.dto.request.VitalCreateRequest;
import com.aitrics.vital.api.dto.request.VitalUpdateRequest;
import com.aitrics.vital.api.dto.response.VitalQueryResponse;
import com.aitrics.vital.api.dto.response.VitalResponse;
import com.aitrics.vital.domain.enumtype.VitalType;
import com.aitrics.vital.domain.exception.ConflictException;
import com.aitrics.vital.domain.exception.NotFoundException;
import com.aitrics.vital.domain.model.Vital;
import com.aitrics.vital.infra.repository.VitalRepository;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class VitalService {
    
    private final VitalRepository vitalRepository;
    private final PatientService patientService;
    
    public VitalService(VitalRepository vitalRepository, PatientService patientService) {
        this.vitalRepository = vitalRepository;
        this.patientService = patientService;
    }
    
    @Transactional
    public VitalResponse createVital(VitalCreateRequest request) {
        if (!patientService.existsById(request.patientId())) {
            throw new NotFoundException("Patient not found with ID: " + request.patientId());
        }
        
        Vital vital = new Vital(
            request.patientId(),
            request.recordedAt(),
            request.vitalType(),
            request.value()
        );
        
        Vital savedVital = vitalRepository.save(vital);
        return VitalResponse.from(savedVital);
    }
    
    public VitalQueryResponse getVitals(String patientId, LocalDateTime from, LocalDateTime to, VitalType vitalType) {
        if (!patientService.existsById(patientId)) {
            throw new NotFoundException("Patient not found with ID: " + patientId);
        }
        
        List<Vital> vitals;
        if (vitalType != null) {
            vitals = vitalRepository.findByPatientIdAndVitalTypeAndRecordedAtBetweenOrderByRecordedAt(
                patientId, vitalType, from, to);
        } else {
            vitals = vitalRepository.findByPatientIdAndRecordedAtBetweenOrderByRecordedAt(
                patientId, from, to);
            
            if (!vitals.isEmpty()) {
                vitalType = vitals.get(0).getVitalType();
            }
        }
        
        return VitalQueryResponse.from(patientId, vitalType, vitals);
    }
    
    @Transactional
    public VitalResponse updateVital(Long vitalId, VitalUpdateRequest request) {
        Vital vital = vitalRepository.findById(vitalId)
            .orElseThrow(() -> new NotFoundException("Vital not found with ID: " + vitalId));
        
        if (!vital.getVersion().equals(request.version())) {
            throw new ConflictException("Version mismatch. Current version: " + vital.getVersion());
        }
        
        try {
            vital.updateValue(request.value());
            Vital savedVital = vitalRepository.save(vital);
            return VitalResponse.from(savedVital);
        } catch (OptimisticLockingFailureException e) {
            throw new ConflictException("Version conflict occurred while updating vital");
        }
    }
}