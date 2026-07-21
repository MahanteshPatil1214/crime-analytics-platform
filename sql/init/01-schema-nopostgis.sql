-- Karnataka Police FIR System - Full Schema
-- Based on hackathon ER diagram

-- Extensions
CREATE EXTENSION IF NOT EXISTS pg_trgm;
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- ============================================================
-- LOOKUP / MASTER TABLES
-- ============================================================

CREATE TABLE state (
    state_id SERIAL PRIMARY KEY,
    state_name VARCHAR(100) NOT NULL,
    nationality_id INT,
    active BIT NOT NULL DEFAULT '1'
);

CREATE TABLE district (
    district_id SERIAL PRIMARY KEY,
    district_name VARCHAR(100) NOT NULL,
    state_id INT NOT NULL REFERENCES state(state_id),
    active BIT NOT NULL DEFAULT '1'
);

CREATE TABLE unit_type (
    unit_type_id SERIAL PRIMARY KEY,
    unit_type_name VARCHAR(100) NOT NULL,
    city_dist_state VARCHAR(20),
    hierarchy INT,
    active BIT NOT NULL DEFAULT '1'
);

CREATE TABLE unit (
    unit_id SERIAL PRIMARY KEY,
    unit_name VARCHAR(200) NOT NULL,
    type_id INT NOT NULL REFERENCES unit_type(unit_type_id),
    parent_unit INT REFERENCES unit(unit_id),
    nationality_id INT,
    state_id INT REFERENCES state(state_id),
    district_id INT REFERENCES district(district_id),
    active BIT NOT NULL DEFAULT '1'
);

CREATE TABLE rank (
    rank_id SERIAL PRIMARY KEY,
    rank_name VARCHAR(100) NOT NULL,
    hierarchy INT,
    active BIT NOT NULL DEFAULT '1'
);

CREATE TABLE designation (
    designation_id SERIAL PRIMARY KEY,
    designation_name VARCHAR(100) NOT NULL,
    active BIT NOT NULL DEFAULT '1',
    sort_order INT
);

CREATE TABLE case_category (
    case_category_id SERIAL PRIMARY KEY,
    lookup_value VARCHAR(50) NOT NULL
);

CREATE TABLE gravity_offence (
    gravity_offence_id SERIAL PRIMARY KEY,
    lookup_value VARCHAR(100) NOT NULL
);

CREATE TABLE case_status_master (
    case_status_id SERIAL PRIMARY KEY,
    case_status_name VARCHAR(100) NOT NULL
);

CREATE TABLE caste_master (
    caste_master_id SERIAL PRIMARY KEY,
    caste_master_name VARCHAR(100) NOT NULL
);

CREATE TABLE religion_master (
    religion_id SERIAL PRIMARY KEY,
    religion_name VARCHAR(100) NOT NULL
);

CREATE TABLE occupation_master (
    occupation_id SERIAL PRIMARY KEY,
    occupation_name VARCHAR(100) NOT NULL
);

CREATE TABLE court (
    court_id SERIAL PRIMARY KEY,
    court_name VARCHAR(200) NOT NULL,
    district_id INT REFERENCES district(district_id),
    state_id INT REFERENCES state(state_id),
    active BIT NOT NULL DEFAULT '1'
);

CREATE TABLE act (
    act_code VARCHAR(20) PRIMARY KEY,
    act_description VARCHAR(500) NOT NULL,
    short_name VARCHAR(50),
    active BIT NOT NULL DEFAULT '1'
);

CREATE TABLE section (
    act_code VARCHAR(20) NOT NULL REFERENCES act(act_code),
    section_code VARCHAR(20) NOT NULL,
    section_description VARCHAR(500),
    active BIT NOT NULL DEFAULT '1',
    PRIMARY KEY (act_code, section_code)
);

CREATE TABLE crime_head (
    crime_head_id SERIAL PRIMARY KEY,
    crime_group_name VARCHAR(200) NOT NULL,
    active BIT NOT NULL DEFAULT '1'
);

CREATE TABLE crime_sub_head (
    crime_sub_head_id SERIAL PRIMARY KEY,
    crime_head_id INT NOT NULL REFERENCES crime_head(crime_head_id),
    crime_head_name VARCHAR(200) NOT NULL,
    seq_id INT
);

CREATE TABLE crime_head_act_section (
    crime_head_id INT NOT NULL REFERENCES crime_head(crime_head_id),
    act_code VARCHAR(20) NOT NULL REFERENCES act(act_code),
    section_code VARCHAR(20) NOT NULL,
    PRIMARY KEY (crime_head_id, act_code, section_code)
);

-- ============================================================
-- EMPLOYEE
-- ============================================================

CREATE TABLE employee (
    employee_id SERIAL PRIMARY KEY,
    district_id INT REFERENCES district(district_id),
    unit_id INT REFERENCES unit(unit_id),
    rank_id INT REFERENCES rank(rank_id),
    designation_id INT REFERENCES designation(designation_id),
    kgid VARCHAR(30) UNIQUE,
    first_name VARCHAR(100) NOT NULL,
    employee_dob DATE,
    gender_id INT,
    blood_group_id INT,
    physically_challenged BIT DEFAULT '0',
    appointment_date DATE
);

-- ============================================================
-- CASE MASTER (Core FIR table)
-- ============================================================

CREATE TABLE case_master (
    case_master_id SERIAL PRIMARY KEY,
    crime_no VARCHAR(30) UNIQUE NOT NULL,
    case_no VARCHAR(20),
    crime_registered_date DATE NOT NULL,
    police_person_id INT REFERENCES employee(employee_id),
    police_station_id INT REFERENCES unit(unit_id),
    case_category_id INT REFERENCES case_category(case_category_id),
    gravity_offence_id INT REFERENCES gravity_offence(gravity_offence_id),
    crime_major_head_id INT REFERENCES crime_head(crime_head_id),
    crime_minor_head_id INT REFERENCES crime_sub_head(crime_sub_head_id),
    case_status_id INT REFERENCES case_status_master(case_status_id),
    court_id INT REFERENCES court(court_id),
    incident_from_date TIMESTAMP,
    incident_to_date TIMESTAMP,
    info_received_ps_date TIMESTAMP,
    latitude DECIMAL(10, 7),
    longitude DECIMAL(10, 7),
    brief_facts TEXT
);

CREATE INDEX idx_cm_crime_no ON case_master(crime_no);
CREATE INDEX idx_cm_registered ON case_master(crime_registered_date);
CREATE INDEX idx_cm_station ON case_master(police_station_id);

-- ============================================================
-- INV_OCCURANCE_TIME (1:1 with CaseMaster)
-- ============================================================

CREATE TABLE inv_occurance_time (
    case_master_id INT PRIMARY KEY REFERENCES case_master(case_master_id),
    occurrence_from TIMESTAMP,
    occurrence_to TIMESTAMP,
    latitude DECIMAL(10, 7),
    longitude DECIMAL(10, 7)
);

-- ============================================================
-- COMPLAINANT DETAILS
-- ============================================================

CREATE TABLE complainant_details (
    complainant_id SERIAL PRIMARY KEY,
    case_master_id INT NOT NULL REFERENCES case_master(case_master_id),
    complainant_name VARCHAR(200) NOT NULL,
    age_year INT,
    occupation_id INT REFERENCES occupation_master(occupation_id),
    religion_id INT REFERENCES religion_master(religion_id),
    caste_id INT REFERENCES caste_master(caste_master_id),
    gender_id INT
);

CREATE INDEX idx_cd_case ON complainant_details(case_master_id);

-- ============================================================
-- VICTIM
-- ============================================================

CREATE TABLE victim (
    victim_master_id SERIAL PRIMARY KEY,
    case_master_id INT NOT NULL REFERENCES case_master(case_master_id),
    victim_name VARCHAR(200) NOT NULL,
    age_year INT,
    gender_id INT,
    victim_police VARCHAR(5) DEFAULT '0'
);

CREATE INDEX idx_v_case ON victim(case_master_id);

-- ============================================================
-- ACCUSED
-- ============================================================

CREATE TABLE accused (
    accused_master_id SERIAL PRIMARY KEY,
    case_master_id INT NOT NULL REFERENCES case_master(case_master_id),
    accused_name VARCHAR(200) NOT NULL,
    age_year INT,
    gender_id INT,
    person_id VARCHAR(10)
);

CREATE INDEX idx_a_case ON accused(case_master_id);

-- ============================================================
-- ACT SECTION ASSOCIATION
-- ============================================================

CREATE TABLE act_section_association (
    case_master_id INT NOT NULL REFERENCES case_master(case_master_id),
    act_code VARCHAR(20) NOT NULL REFERENCES act(act_code),
    section_code VARCHAR(20) NOT NULL,
    act_order_id INT,
    section_order_id INT,
    PRIMARY KEY (case_master_id, act_code, section_code)
);

-- ============================================================
-- ARREST / SURRENDER
-- ============================================================

CREATE TABLE arrest_surrender (
    arrest_surrender_id SERIAL PRIMARY KEY,
    case_master_id INT NOT NULL REFERENCES case_master(case_master_id),
    arrest_surrender_type_id INT,
    arrest_surrender_date DATE,
    arrest_surrender_state_id INT REFERENCES state(state_id),
    arrest_surrender_district_id INT REFERENCES district(district_id),
    police_station_id INT REFERENCES unit(unit_id),
    io_id INT REFERENCES employee(employee_id),
    court_id INT REFERENCES court(court_id)
);

CREATE INDEX idx_as_case ON arrest_surrender(case_master_id);

-- Junction table: one arrest event can link multiple accused
CREATE TABLE inv_arrest_surrender_accused (
    arrest_surrender_id INT NOT NULL REFERENCES arrest_surrender(arrest_surrender_id),
    accused_master_id INT NOT NULL REFERENCES accused(accused_master_id),
    is_accused BIT DEFAULT '1',
    is_complainant_accused BIT DEFAULT '0',
    PRIMARY KEY (arrest_surrender_id, accused_master_id)
);

-- ============================================================
-- CHARGESHEET DETAILS
-- ============================================================

CREATE TABLE chargesheet_details (
    cs_id SERIAL PRIMARY KEY,
    case_master_id INT NOT NULL REFERENCES case_master(case_master_id),
    cs_date TIMESTAMP,
    cs_type CHAR(1) CHECK (cs_type IN ('A', 'B', 'C')),
    police_person_id INT REFERENCES employee(employee_id)
);

CREATE INDEX idx_cs_case ON chargesheet_details(case_master_id);

-- ============================================================
-- OUR ADDITIONAL ANALYTICS TABLES (kept from original)
-- ============================================================

-- Note: financial_transactions, property_records, audit_logs, chat_history
-- are created by 01-schema.sql (runs first alphabetically)
