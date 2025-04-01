package models;

import java.util.Date;
import java.util.Arrays;

public class Donor {
    private int donorId;
    private String name;
    private String contact;
    private String email;
    private String gender;
    private int age;
    private String bloodType;
    private String location;
    private Date registrationDate;
    private Date lastDonationDate;
    private boolean eligible;

    private static final String[] VALID_BLOOD_TYPES = {"A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"};
    private static final String[] VALID_GENDERS = {"Male", "Female", "Other"};
    private static final int MIN_DONATION_INTERVAL_DAYS = 56; // 56 days (8 weeks) between donations

    public Donor(String name, String contact, String email, String gender, int age, String bloodType, String location) {
        validateName(name);
        validateContact(contact);
        validateEmail(email);
        validateGender(gender);
        validateAge(age);
        validateBloodType(bloodType);
        validateLocation(location);

        this.name = name;
        this.contact = contact;
        this.email = email;
        this.gender = gender;
        this.age = age;
        this.bloodType = bloodType.toUpperCase();
        this.location = location;
        this.registrationDate = new Date();
        this.lastDonationDate = null;
        this.eligible = true; // New donors are always eligible
    }

    private void validateName(String name) {
        if (name == null || name.trim().length() < 2 || name.trim().length() > 100) {
            throw new IllegalArgumentException("Name must be between 2 and 100 characters");
        }
    }

    private void validateContact(String contact) {
        // Allow Bangladeshi phone numbers (starts with 01)
        if (contact == null || !contact.matches("^01[0-9]{8,9}$")) {
            throw new IllegalArgumentException("Invalid contact number format. Must be a valid Bangladeshi number starting with '01' followed by 8-9 digits");
        }
    }

    private void validateEmail(String email) {
        if (email != null && !email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new IllegalArgumentException("Invalid email format");
        }
    }

    private void validateGender(String gender) {
        if (gender == null || !Arrays.asList(VALID_GENDERS).contains(gender)) {
            throw new IllegalArgumentException("Gender must be 'Male', 'Female', or 'Other'");
        }
    }

    private void validateAge(int age) {
        if (age < 18 || age > 65) {
            throw new IllegalArgumentException("Age must be between 18 and 65");
        }
    }

    private void validateBloodType(String bloodType) {
        if (bloodType == null || !Arrays.asList(VALID_BLOOD_TYPES).contains(bloodType.toUpperCase())) {
            throw new IllegalArgumentException("Invalid blood type: " + bloodType);
        }
    }

    private void validateLocation(String location) {
        if (location == null || location.trim().isEmpty()) {
            throw new IllegalArgumentException("Location cannot be empty");
        }
    }

    public void recordDonation() {
        this.lastDonationDate = new Date();
        updateEligibility(); // This will set eligible based on the time interval
    }

    public void updateEligibility() {
        if (lastDonationDate == null) {
            this.eligible = true; // No donations yet, so eligible
            return;
        }

        // Check if enough time has passed since last donation
        long daysSinceLastDonation = (new Date().getTime() - lastDonationDate.getTime()) / (1000 * 60 * 60 * 24);
        this.eligible = daysSinceLastDonation >= MIN_DONATION_INTERVAL_DAYS;
    }

    // Getters and Setters
    public int getDonorId() { return donorId; }
    public void setDonorId(int donorId) { 
        if (donorId <= 0) {
            throw new IllegalArgumentException("Invalid donor ID");
        }
        this.donorId = donorId; 
    }
    
    public String getName() { return name; }
    public void setName(String name) {
        validateName(name);
        this.name = name;
    }
    
    public String getContact() { return contact; }
    public void setContact(String contact) {
        validateContact(contact);
        this.contact = contact;
    }
    
    public String getEmail() { return email; }
    public void setEmail(String email) {
        validateEmail(email);
        this.email = email;
    }
    
    public String getGender() { return gender; }
    public void setGender(String gender) {
        validateGender(gender);
        this.gender = gender;
    }
    
    public int getAge() { return age; }
    public void setAge(int age) {
        validateAge(age);
        this.age = age;
    }
    
    public String getBloodType() { return bloodType; }
    public void setBloodType(String bloodType) {
        validateBloodType(bloodType);
        this.bloodType = bloodType.toUpperCase();
    }
    
    public String getLocation() { return location; }
    public void setLocation(String location) {
        validateLocation(location);
        this.location = location;
    }
    
    public Date getRegistrationDate() { return registrationDate; }
    public void setRegistrationDate(Date registrationDate) { this.registrationDate = registrationDate; }
    
    public Date getLastDonationDate() { return lastDonationDate; }
    public void setLastDonationDate(Date lastDonationDate) { 
        this.lastDonationDate = lastDonationDate; 
        updateEligibility();
    }
    
    public boolean isEligible() { 
        updateEligibility(); // Always check current eligibility
        return eligible; 
    }
    public void setEligible(boolean eligible) { this.eligible = eligible; }

    @Override
    public String toString() {
        return String.format("Donor [ID: %d, Name: %s, Contact: %s, Email: %s, " +
                           "Gender: %s, Age: %d, Blood Type: %s, Location: %s, " +
                           "Registered: %s, Last Donation Date: %s, Eligible: %b]",
                           donorId, name, contact, email != null ? email : "N/A",
                           gender != null ? gender : "N/A", age,
                           bloodType != null ? bloodType : "N/A",
                           location != null ? location : "N/A",
                           registrationDate != null ? 
                           new java.text.SimpleDateFormat("yyyy-MM-dd").format(registrationDate) : "N/A",
                           lastDonationDate != null ? 
                           new java.text.SimpleDateFormat("yyyy-MM-dd").format(lastDonationDate) : "N/A",
                           eligible);
    }
}