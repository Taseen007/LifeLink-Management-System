-- Drop existing tables in correct order
DROP TABLE IF EXISTS donation_history;
DROP TABLE IF EXISTS blood_inventory;
DROP TABLE IF EXISTS donors;

-- Create donors table with all necessary fields
CREATE TABLE donors (
    donor_id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    contact VARCHAR(15) NOT NULL,
    email VARCHAR(100),
    gender VARCHAR(10) NOT NULL,
    age INT NOT NULL,
    blood_type VARCHAR(5) NOT NULL,
    location VARCHAR(100) NOT NULL,
    registration_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_donation_date TIMESTAMP NULL DEFAULT NULL,
    eligible BOOLEAN NOT NULL DEFAULT true,
    CONSTRAINT chk_gender CHECK (gender IN ('Male', 'Female', 'Other')),
    CONSTRAINT chk_age CHECK (age >= 18 AND age <= 65),
    CONSTRAINT chk_blood_type CHECK (blood_type IN ('A+', 'A-', 'B+', 'B-', 'AB+', 'AB-', 'O+', 'O-'))
);

-- Create blood_inventory table
CREATE TABLE blood_inventory (
    inventory_id INT PRIMARY KEY AUTO_INCREMENT,
    blood_type VARCHAR(5) NOT NULL,
    units INT NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'Available',
    location VARCHAR(100),
    last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT chk_blood_type_inv CHECK (blood_type IN ('A+', 'A-', 'B+', 'B-', 'AB+', 'AB-', 'O+', 'O-')),
    CONSTRAINT chk_units CHECK (units >= 0)
);

-- Create donation_history table
CREATE TABLE donation_history (
    donation_id INT PRIMARY KEY AUTO_INCREMENT,
    donor_id INT NOT NULL,
    donation_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    blood_type VARCHAR(5) NOT NULL,
    units INT NOT NULL DEFAULT 1,
    location VARCHAR(100),
    CONSTRAINT fk_donor_donation FOREIGN KEY (donor_id)
        REFERENCES donors(donor_id) ON DELETE CASCADE
);

-- Add indexes for better performance
CREATE INDEX idx_blood_type ON blood_inventory(blood_type);
CREATE INDEX idx_donor_blood_type ON donors(blood_type);
CREATE INDEX idx_donation_date ON donation_history(donation_date);
CREATE INDEX idx_donor_location ON donors(location);
CREATE INDEX idx_inventory_status ON blood_inventory(status);

-- Insert some initial blood types if needed
INSERT INTO blood_inventory (blood_type, units, status, location, last_updated)
VALUES 
('A+', 0, 'Available', 'Main Storage', CURRENT_TIMESTAMP),
('A-', 0, 'Available', 'Main Storage', CURRENT_TIMESTAMP),
('B+', 0, 'Available', 'Main Storage', CURRENT_TIMESTAMP),
('B-', 0, 'Available', 'Main Storage', CURRENT_TIMESTAMP),
('AB+', 0, 'Available', 'Main Storage', CURRENT_TIMESTAMP),
('AB-', 0, 'Available', 'Main Storage', CURRENT_TIMESTAMP),
('O+', 0, 'Available', 'Main Storage', CURRENT_TIMESTAMP),
('O-', 0, 'Available', 'Main Storage', CURRENT_TIMESTAMP);

-- Create blood_requests table
CREATE TABLE blood_requests (
    request_id INT PRIMARY KEY AUTO_INCREMENT,
    patient_name VARCHAR(100) NOT NULL,
    blood_type VARCHAR(5) NOT NULL,
    units_needed INT NOT NULL,
    urgency VARCHAR(20) NOT NULL,
    hospital VARCHAR(100) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'Pending',
    request_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    required_date TIMESTAMP NOT NULL,
    CONSTRAINT chk_blood_type_req CHECK (blood_type IN ('A+', 'A-', 'B+', 'B-', 'AB+', 'AB-', 'O+', 'O-')),
    CONSTRAINT chk_urgency CHECK (urgency IN ('Emergency', 'High', 'Medium', 'Low')),
    CONSTRAINT chk_request_status CHECK (status IN ('Pending', 'Approved', 'Fulfilled', 'Rejected'))
);

-- Add index for better performance
CREATE INDEX idx_request_status ON blood_requests(status);
CREATE INDEX idx_request_urgency ON blood_requests(urgency);
CREATE INDEX idx_request_blood_type ON blood_requests(blood_type);