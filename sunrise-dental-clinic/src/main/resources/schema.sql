-- Sunrise Dental Clinic — schema (mirrors docs/ERD.md)
CREATE DATABASE IF NOT EXISTS sunrise_dental_clinic;
USE sunrise_dental_clinic;

CREATE TABLE user (
    user_id        VARCHAR(10) PRIMARY KEY,
    username       VARCHAR(50) NOT NULL UNIQUE,
    password_hash  VARCHAR(64) NOT NULL,
    role           VARCHAR(20) NOT NULL,
    is_active      BOOLEAN NOT NULL DEFAULT TRUE,
    last_login     DATETIME,
    email          VARCHAR(100)
);

CREATE TABLE staff (
    staff_id     VARCHAR(10) PRIMARY KEY,
    user_id      VARCHAR(10) NOT NULL UNIQUE,
    staff_name   VARCHAR(100) NOT NULL,
    designation  VARCHAR(50),
    contact_no   VARCHAR(15),
    email        VARCHAR(100),
    FOREIGN KEY (user_id) REFERENCES user(user_id)
);

CREATE TABLE dentist (
    dentist_id        VARCHAR(10) PRIMARY KEY,
    user_id           VARCHAR(10) NOT NULL UNIQUE,
    dentist_name      VARCHAR(100) NOT NULL,
    specialization    VARCHAR(100),
    contact_no        VARCHAR(15),
    consultation_fee  DOUBLE NOT NULL DEFAULT 0,
    available_days    VARCHAR(100),
    FOREIGN KEY (user_id) REFERENCES user(user_id)
);

CREATE TABLE patient (
    patient_id       VARCHAR(10) PRIMARY KEY,
    patient_name     VARCHAR(100) NOT NULL,
    address          VARCHAR(200),
    contact_no       VARCHAR(15),
    nic              VARCHAR(12) NOT NULL UNIQUE,
    date_of_birth    DATE NOT NULL,
    gender           VARCHAR(10) NOT NULL,
    registered_date  DATE NOT NULL,
    email            VARCHAR(100)
);

CREATE TABLE treatment_type (
    treatment_code    VARCHAR(30) PRIMARY KEY,
    treatment_name    VARCHAR(100) NOT NULL,
    unit_cost         DOUBLE NOT NULL,
    is_per_tooth      BOOLEAN NOT NULL DEFAULT FALSE,
    duration_minutes  INT NOT NULL
);

CREATE TABLE appointment (
    appointment_no      VARCHAR(10) PRIMARY KEY,
    patient_id          VARCHAR(10) NOT NULL,
    dentist_id          VARCHAR(10) NOT NULL,
    booked_by_staff_id  VARCHAR(10),
    treatment_code      VARCHAR(20) NOT NULL,
    no_tooth            INT NOT NULL DEFAULT 1,
    appointment_date    DATE NOT NULL,
    appointment_time    TIME NOT NULL,
    status              VARCHAR(20) NOT NULL,
    remarks             VARCHAR(255),
    FOREIGN KEY (patient_id) REFERENCES patient(patient_id),
    FOREIGN KEY (dentist_id) REFERENCES dentist(dentist_id),
    FOREIGN KEY (treatment_code) REFERENCES treatment_type(treatment_code)
);

CREATE TABLE payment (
    payment_id         VARCHAR(10) PRIMARY KEY,
    appointment_no     VARCHAR(10) NOT NULL UNIQUE,
    consultation_fee   DOUBLE NOT NULL,
    unit_cost_charged  DOUBLE NOT NULL,
    no_tooth_billed    INT NOT NULL,
    treatment_cost     DOUBLE NOT NULL,
    discount           DOUBLE NOT NULL DEFAULT 0,
    payment_method     VARCHAR(20) NOT NULL,
    payment_status     VARCHAR(20) NOT NULL,
    payment_date       DATE NOT NULL,
    FOREIGN KEY (appointment_no) REFERENCES appointment(appointment_no)
);

-- Seed manager account so the application can be logged into on a fresh database.
-- username: admin / password: admin123 (SHA-256 hashed, see util.PasswordUtil)
INSERT INTO user (user_id, username, password_hash, role, is_active, last_login)
VALUES ('USR0001', 'admin', '240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9', 'MANAGER', TRUE, NULL);

-- Seed the fixed price list that used to live in the TreatmentType enum, so existing
-- appointment.treatment_code values keep resolving with no data migration needed.
INSERT INTO treatment_type (treatment_code, treatment_name, unit_cost, is_per_tooth, duration_minutes) VALUES
    ('CHECKING', 'Checking', 0.00, FALSE, 15),
    ('SCALING', 'Scaling', 3500.00, FALSE, 30),
    ('FILLING', 'Filling', 6000.00, TRUE, 45),
    ('EXTRACTION', 'Extraction', 5000.00, TRUE, 30),
    ('ROOT_CANAL', 'Root Canal', 15000.00, TRUE, 60),
    ('CROWN', 'Crown', 20000.00, TRUE, 60),
    ('DENTURE', 'Denture', 25000.00, FALSE, 90),
    ('WHITENING', 'Whitening', 12000.00, FALSE, 45),
    ('BRACES_REVIEW', 'Braces Review', 2500.00, FALSE, 20);




