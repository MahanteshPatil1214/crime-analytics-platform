CREATE EXTENSION IF NOT EXISTS postgis;
CREATE EXTENSION IF NOT EXISTS pg_trgm;
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE police_beats (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    beat_number VARCHAR(20) UNIQUE NOT NULL,
    name VARCHAR(200),
    district VARCHAR(100),
    boundary geometry(Polygon, 4326),
    center_point geometry(Point, 4326),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_beat_boundary ON police_beats USING GIST(boundary);

CREATE TABLE incidents (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    fir_number VARCHAR(32) UNIQUE NOT NULL,
    nibrs_code VARCHAR(5) NOT NULL,
    nibrs_description VARCHAR(200),
    severity VARCHAR(20) NOT NULL DEFAULT 'MEDIUM',
    title VARCHAR(500) NOT NULL,
    description TEXT,
    occurred_date DATE NOT NULL,
    occurred_time TIME,
    reported_date TIMESTAMPTZ NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'OPEN',
    location_point geometry(Point, 4326),
    address_text VARCHAR(500),
    district VARCHAR(50),
    city VARCHAR(100),
    state VARCHAR(50),
    zip_code VARCHAR(10),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ,
    created_by VARCHAR(64) NOT NULL,
    version BIGINT NOT NULL DEFAULT 1
);

CREATE INDEX idx_incident_nibrs ON incidents(nibrs_code);
CREATE INDEX idx_incident_date ON incidents(occurred_date);
CREATE INDEX idx_incident_location ON incidents USING GIST(location_point);
CREATE INDEX idx_incident_district ON incidents(district);

CREATE TABLE persons (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    biometric_id VARCHAR(64) UNIQUE,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    date_of_birth DATE,
    gender VARCHAR(20),
    nationality VARCHAR(50),
    address_hash VARCHAR(64),
    phone_hash VARCHAR(64),
    person_type VARCHAR(30) NOT NULL,
    conviction_count INTEGER NOT NULL DEFAULT 0,
    is_known_offender BOOLEAN NOT NULL DEFAULT FALSE,
    risk_score DOUBLE PRECISION,
    risk_score_updated_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ,
    version BIGINT NOT NULL DEFAULT 1
);

CREATE INDEX idx_person_name ON persons(last_name, first_name);
CREATE INDEX idx_person_biometric ON persons(biometric_id);

CREATE TABLE involvements (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    incident_id UUID NOT NULL REFERENCES incidents(id) ON DELETE CASCADE,
    person_id UUID NOT NULL REFERENCES persons(id) ON DELETE CASCADE,
    role VARCHAR(30) NOT NULL,
    role_description VARCHAR(200),
    arrest_date TIMESTAMPTZ,
    charges TEXT,
    disposition VARCHAR(200),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_inv_incident ON involvements(incident_id);
CREATE INDEX idx_inv_person ON involvements(person_id);
CREATE UNIQUE INDEX idx_inv_unique ON involvements(incident_id, person_id, role);

CREATE TABLE property_records (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    incident_id UUID NOT NULL REFERENCES incidents(id) ON DELETE CASCADE,
    property_type VARCHAR(30) NOT NULL,
    description VARCHAR(500),
    estimated_value NUMERIC(12,2),
    serial_number VARCHAR(100),
    is_recovered BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE financial_transactions (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    transaction_ref VARCHAR(64) UNIQUE NOT NULL,
    sender_account_id VARCHAR(64) NOT NULL,
    recipient_account_id VARCHAR(64) NOT NULL,
    amount NUMERIC(15,2) NOT NULL,
    currency VARCHAR(3) NOT NULL DEFAULT 'INR',
    transaction_date TIMESTAMPTZ NOT NULL,
    transaction_type VARCHAR(30) NOT NULL,
    is_flagged BOOLEAN NOT NULL DEFAULT FALSE,
    flag_reason VARCHAR(200),
    risk_score DOUBLE PRECISION,
    related_incident_id UUID REFERENCES incidents(id),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_ft_sender ON financial_transactions(sender_account_id);
CREATE INDEX idx_ft_recipient ON financial_transactions(recipient_account_id);
CREATE INDEX idx_ft_flagged ON financial_transactions(is_flagged) WHERE is_flagged = TRUE;

CREATE TABLE socio_demographics (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    census_tract VARCHAR(20) UNIQUE NOT NULL,
    region_name VARCHAR(200),
    boundary geometry(Polygon, 4326),
    population INTEGER,
    population_density DOUBLE PRECISION,
    median_income DOUBLE PRECISION,
    unemployment_rate DOUBLE PRECISION,
    poverty_rate DOUBLE PRECISION,
    education_below_highschool_rate DOUBLE PRECISION,
    housing_vacancy_rate DOUBLE PRECISION,
    single_parent_household_rate DOUBLE PRECISION,
    gini_index DOUBLE PRECISION,
    data_year INTEGER NOT NULL
);

CREATE INDEX idx_sd_boundary ON socio_demographics USING GIST(boundary);

CREATE TABLE audit_logs (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id VARCHAR(64) NOT NULL,
    user_role VARCHAR(50),
    action VARCHAR(50) NOT NULL,
    entity_type VARCHAR(100) NOT NULL,
    entity_id VARCHAR(64),
    description TEXT,
    ip_address VARCHAR(45),
    request_uri VARCHAR(500),
    old_value_hash VARCHAR(64),
    new_value_hash VARCHAR(64),
    action_timestamp TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    tamper_seal VARCHAR(128) NOT NULL
);

CREATE INDEX idx_audit_user ON audit_logs(user_id);
CREATE INDEX idx_audit_entity ON audit_logs(entity_type, entity_id);

CREATE TABLE chat_history (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    session_id VARCHAR(64) NOT NULL,
    user_id VARCHAR(64) NOT NULL,
    role VARCHAR(20) NOT NULL,
    content TEXT NOT NULL,
    language VARCHAR(10),
    intent VARCHAR(100),
    entities_json TEXT,
    confidence_score DOUBLE PRECISION,
    timestamp TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_chat_session ON chat_history(session_id);
CREATE INDEX idx_chat_user ON chat_history(user_id);
