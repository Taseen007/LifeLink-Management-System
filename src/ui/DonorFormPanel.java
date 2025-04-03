package ui;

import models.Donor;

import javax.swing.*;
import java.awt.*;

public class DonorFormPanel extends JPanel {
    private static final String[] GENDER_OPTIONS = {"Male", "Female", "Other"};
    private static final String[] BLOOD_TYPES = {"A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"};
    
    private JTextField nameField;
    private JTextField contactField;
    private JTextField emailField;
    private JComboBox<String> genderCombo;
    private JSpinner ageSpinner;
    private JComboBox<String> bloodTypeCombo;
    private JTextField locationField;

    private void initializeComponents(Donor donor) {
        nameField = new JTextField();
        contactField = new JTextField();
        emailField = new JTextField(20);
        genderCombo = new JComboBox<>(GENDER_OPTIONS);
        genderCombo.setPrototypeDisplayValue("Select Gender");
        ageSpinner = new JSpinner(new SpinnerNumberModel(18, 18, 65, 1));
        bloodTypeCombo = new JComboBox<>(BLOOD_TYPES);
        bloodTypeCombo.setPrototypeDisplayValue("Select Blood Type");
        locationField = new JTextField();

        if (donor != null) {
            nameField.setText(donor.getName());
            contactField.setText(donor.getContact());
            emailField.setText(donor.getEmail());
            genderCombo.setSelectedItem(donor.getGender());
            ageSpinner.setValue(donor.getAge());
            bloodTypeCombo.setSelectedItem(donor.getBloodType());
            locationField.setText(donor.getLocation());
        }

        addComponentsToPanel();
    }

    private void addComponentsToPanel() {
        add(new JLabel("Name:"));
        add(nameField);
        add(new JLabel("Contact:"));
        add(contactField);
        add(new JLabel("Email:"));
        add(emailField);
        add(new JLabel("Gender:"));
        add(genderCombo);
        add(new JLabel("Age:"));
        add(ageSpinner);
        add(new JLabel("Blood Type:"));
        add(bloodTypeCombo);
        add(new JLabel("Location:"));
        add(locationField);
    }

    public void validateFields() throws IllegalArgumentException {
        if (nameField.getText().trim().isEmpty() ||
                contactField.getText().trim().isEmpty() ||
                emailField.getText().trim().isEmpty() ||
                locationField.getText().trim().isEmpty()) {
            throw new IllegalArgumentException("All fields are required");
        }
    }

    public Donor createDonor() {
        return new Donor(
                nameField.getText().trim(),
                contactField.getText().trim(),
                emailField.getText().trim(),
                (String) genderCombo.getSelectedItem(),
                (Integer) ageSpinner.getValue(),
                (String) bloodTypeCombo.getSelectedItem(),
                locationField.getText().trim()
        );
    }

    public void updateDonor(Donor donor) {
        donor.setName(nameField.getText().trim());
        donor.setContact(contactField.getText().trim());
        donor.setEmail(emailField.getText().trim());
        donor.setGender((String) genderCombo.getSelectedItem());
        donor.setAge((Integer) ageSpinner.getValue());
        donor.setBloodType((String) bloodTypeCombo.getSelectedItem());
        donor.setLocation(locationField.getText().trim());
    }

    public DonorFormPanel(Donor donor) {
        setLayout(new GridLayout(7, 2, 5, 5));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        initializeComponents(donor);
    }

    public DonorFormPanel() {
        this(null);
    }
}
