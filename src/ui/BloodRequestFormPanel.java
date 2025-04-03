package ui;

import models.BloodRequest;
import javax.swing.*;
import java.awt.*;
import java.util.Date;

public class BloodRequestFormPanel extends JPanel {  // Make class public
    private static final String[] BLOOD_TYPES = {"A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"};
    private static final String[] URGENCY_LEVELS = {"Emergency", "High", "Medium", "Low"};
    
    private JTextField patientNameField;
    private JComboBox<String> bloodTypeCombo;
    private JSpinner unitsSpinner;
    private JComboBox<String> urgencyCombo;
    private JTextField hospitalField;
    private JSpinner requiredDateSpinner;

    public BloodRequestFormPanel(BloodRequest request) {
        setLayout(new GridLayout(6, 2, 5, 5));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        initializeComponents(request);
    }

    public BloodRequestFormPanel() {
        this(null);
    }

    private void initializeComponents(BloodRequest request) {
        patientNameField = new JTextField(20);
        bloodTypeCombo = new JComboBox<>(BLOOD_TYPES);
        bloodTypeCombo.setPrototypeDisplayValue("Select Blood Type");
        unitsSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 10, 1));
        urgencyCombo = new JComboBox<>(URGENCY_LEVELS);
        urgencyCombo.setPrototypeDisplayValue("Select Urgency");
        hospitalField = new JTextField(20);
        
        SpinnerDateModel dateModel = new SpinnerDateModel();
        requiredDateSpinner = new JSpinner(dateModel);
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(requiredDateSpinner, "yyyy-MM-dd HH:mm");
        requiredDateSpinner.setEditor(dateEditor);
        requiredDateSpinner.setValue(new Date());

        if (request != null) {
            patientNameField.setText(request.getPatientName());
            bloodTypeCombo.setSelectedItem(request.getBloodType());
            unitsSpinner.setValue(request.getUnitsNeeded());
            urgencyCombo.setSelectedItem(request.getUrgency());
            hospitalField.setText(request.getHospital());
            requiredDateSpinner.setValue(request.getRequiredDate());
        }

        addComponentsToPanel();
    }

    private void addComponentsToPanel() {
        add(new JLabel("Patient Name:"));
        add(patientNameField);
        add(new JLabel("Blood Type:"));
        add(bloodTypeCombo);
        add(new JLabel("Units Needed:"));
        add(unitsSpinner);
        add(new JLabel("Urgency:"));
        add(urgencyCombo);
        add(new JLabel("Hospital:"));
        add(hospitalField);
        add(new JLabel("Required Date:"));
        add(requiredDateSpinner);
    }

    public void validateFields() throws IllegalArgumentException {
        if (patientNameField.getText().trim().isEmpty() ||
                hospitalField.getText().trim().isEmpty()) {
            throw new IllegalArgumentException("All fields are required");
        }
    }

    public BloodRequest createRequest() {
        validateFields();
        BloodRequest request = new BloodRequest(
            patientNameField.getText().trim(),
            (String) bloodTypeCombo.getSelectedItem(),
            (Integer) unitsSpinner.getValue(),
            (String) urgencyCombo.getSelectedItem(),
            hospitalField.getText().trim(),
            (Date) requiredDateSpinner.getValue()
        );
        return request;
    }

    public void updateRequest(BloodRequest request) {
        try {
            validateFields();
            if (request != null) {
                request.setPatientName(patientNameField.getText().trim());
                request.setBloodType((String) bloodTypeCombo.getSelectedItem());
                request.setUnitsNeeded((Integer) unitsSpinner.getValue());
                request.setUrgency((String) urgencyCombo.getSelectedItem());
                request.setHospital(hospitalField.getText().trim());
                request.setRequiredDate((Date) requiredDateSpinner.getValue());
            } else {
                throw new IllegalArgumentException("Request object cannot be null");
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Error updating request: " + e.getMessage());
        }
    }

    public void setFieldsEnabled(boolean enabled) {
        patientNameField.setEnabled(enabled);
        bloodTypeCombo.setEnabled(enabled);
        unitsSpinner.setEnabled(enabled);
        urgencyCombo.setEnabled(enabled);
        hospitalField.setEnabled(enabled);
        requiredDateSpinner.setEnabled(enabled);
    }
}