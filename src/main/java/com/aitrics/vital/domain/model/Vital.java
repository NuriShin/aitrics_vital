package com.aitrics.vital.domain.model;

import com.aitrics.vital.domain.enumtype.VitalType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table(name = "vitals")
public class Vital {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "vital_id")
    private Long vitalId;
    
    @Column(name = "patient_id", length = 20, nullable = false)
    @NotBlank
    private String patientId;
    
    @Column(name = "recorded_at", nullable = false)
    @NotNull
    private LocalDateTime recordedAt;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "vital_type", length = 10, nullable = false)
    @NotNull
    private VitalType vitalType;
    
    @Column(name = "value", nullable = false)
    @NotNull
    private Double value;
    
    @Version
    @Column(name = "version", nullable = false)
    private Long version = 0L;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    protected Vital() {}
    
    public Vital(String patientId, LocalDateTime recordedAt, VitalType vitalType, Double value) {
        this.patientId = patientId;
        this.recordedAt = recordedAt;
        this.vitalType = vitalType;
        this.value = value;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    public void updateValue(Double value) {
        this.value = value;
        this.updatedAt = LocalDateTime.now();
    }
    
    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (updatedAt == null) {
            updatedAt = LocalDateTime.now();
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public Long getVitalId() { return vitalId; }
    public String getPatientId() { return patientId; }
    public LocalDateTime getRecordedAt() { return recordedAt; }
    public VitalType getVitalType() { return vitalType; }
    public Double getValue() { return value; }
    public Long getVersion() { return version; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}