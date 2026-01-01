-- =========================
-- PATIENTS TABLE
-- =========================
create table patients (
                          patient_id varchar(20) primary key,
                          name varchar(50) not null,
                          gender char(1) not null,
                          birth_date date not null,
                          version bigint not null default 0,
                          created_at timestamp not null default current_timestamp,
                          updated_at timestamp not null default current_timestamp
);

-- =========================
-- VITALS TABLE
-- =========================
create table vitals (
                        vital_id bigint auto_increment primary key,
                        patient_id varchar(20) not null,
                        recorded_at timestamp not null,
                        vital_type varchar(10) not null,
                        vital_value double not null,
                        version bigint not null default 0,
                        created_at timestamp not null default current_timestamp,
                        updated_at timestamp not null default current_timestamp,
                        constraint fk_vitals_patient
                            foreign key (patient_id) references patients(patient_id)
);

-- =========================
-- INDEXES
-- =========================
create index idx_vitals_patient_time
    on vitals(patient_id, recorded_at);

create index idx_vitals_patient_type_time
    on vitals(patient_id, vital_type, recorded_at);

-- ======================================================
-- TEST DATA INSERT
-- ======================================================

-- -------------------------
-- PATIENT TEST DATA
-- -------------------------
insert into patients (patient_id, name, gender, birth_date, version)
values
    ('P00001234', '홍길동', 'M', '1975-03-01', 0),
    ('P00005678', '김영희', 'F', '1982-07-12', 0);

-- -------------------------
-- VITAL TEST DATA (Patient: P00001234)
-- -------------------------
insert into vitals (patient_id, recorded_at, vital_type, vital_value, version)
values
    ('P00001234', '2025-12-01 10:00:00', 'HR', 110.0, 0),
    ('P00001234', '2025-12-01 10:00:00', 'SBP', 85.0, 0),
    ('P00001234', '2025-12-01 10:00:00', 'SpO2', 89.0, 0),

    ('P00001234', '2025-12-01 10:05:00', 'HR', 130.0, 0),
    ('P00001234', '2025-12-01 10:05:00', 'SBP', 82.0, 0),
    ('P00001234', '2025-12-01 10:05:00', 'SpO2', 88.0, 0);

-- -------------------------
-- VITAL TEST DATA (Patient: P00005678)
-- -------------------------
insert into vitals (patient_id, recorded_at, vital_type, vital_value, version)
values
    ('P00005678', '2025-12-01 09:30:00', 'HR', 72.0, 0),
    ('P00005678', '2025-12-01 09:30:00', 'SBP', 120.0, 0),
    ('P00005678', '2025-12-01 09:30:00', 'SpO2', 98.0, 0);
