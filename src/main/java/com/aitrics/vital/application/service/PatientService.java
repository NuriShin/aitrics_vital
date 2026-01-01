package com.aitrics.vital.application.service;

import com.aitrics.vital.api.dto.request.PatientCreateRequest;
import com.aitrics.vital.api.dto.request.PatientUpdateRequest;
import com.aitrics.vital.api.dto.response.PatientResponse;
import com.aitrics.vital.domain.exception.ConflictException;
import com.aitrics.vital.domain.exception.NotFoundException;
import com.aitrics.vital.domain.model.Patient;
import com.aitrics.vital.infra.repository.PatientRepository;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class PatientService {
    
    private final PatientRepository patientRepository;
    
    public PatientService(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }
    
    @Transactional
    public PatientResponse createPatient(PatientCreateRequest request) {
        if (patientRepository.existsById(request.patientId())) {
            throw new ConflictException("Patient with ID " + request.patientId() + " already exists");
        }
        
        Patient patient = new Patient(
            request.patientId(),
            request.name(),
            request.gender(),
            request.birthDate()
        );
        
        Patient savedPatient = patientRepository.save(patient);
        return PatientResponse.from(savedPatient);
    }
    
    @Transactional
    public PatientResponse updatePatient(String patientId, PatientUpdateRequest request) {
        Patient patient = patientRepository.findById(patientId)
            .orElseThrow(() -> new NotFoundException("Patient not found with ID: " + patientId));
        
        if (!patient.getVersion().equals(request.version())) {
            throw new ConflictException("Version mismatch. Current version: " + patient.getVersion());
        }
        
        try {
            patient.updateInfo(request.name(), request.gender(), request.birthDate());
            Patient savedPatient = patientRepository.save(patient);
            return PatientResponse.from(savedPatient);
        } catch (OptimisticLockingFailureException e) {
            throw new ConflictException("Version conflict occurred while updating patient");
        }
    }
    
    public PatientResponse getPatient(String patientId) {
        Patient patient = patientRepository.findById(patientId)
            .orElseThrow(() -> new NotFoundException("Patient not found with ID: " + patientId));
        return PatientResponse.from(patient);
    }
    
    public boolean existsById(String patientId) {
        return patientRepository.existsById(patientId);
    }
}