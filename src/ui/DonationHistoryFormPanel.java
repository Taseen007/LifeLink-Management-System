package ui;

import models.DonationHistory;
import javax.swing.*;
import java.awt.*;
import java.util.Date;

public class DonationHistoryFormPanel extends JPanel {
    private JTextField donorIdField;
    private JTextField bloodTypeField;
    private JTextField locationField;
    private JSpinner unitsSpinner;

    public DonationHistoryFormPanel() {
        setLayout(new GridLayout(4, 2, 5, 5));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        donorIdField = new JTextField();
        bloodTypeField = new JTextField();
        locationField = new JTextField();
        unitsSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 5, 1));

        add(new JLabel("Donor ID:"));
        add(donorIdField);
        add(new JLabel("Blood Type:"));
        add(bloodTypeField);
        add(new JLabel("Location:"));
        add(locationField);
        add(new JLabel("Units:"));
        add(unitsSpinner);
    }

    public DonationHistory createDonationHistory() {
        int donorId = Integer.parseInt(donorIdField.getText().trim());
        String bloodType = bloodTypeField.getText().trim();
        String location = locationField.getText().trim();
        
        DonationHistory history = new DonationHistory(donorId, bloodType, location);
        history.setUnits((Integer) unitsSpinner.getValue());
        return history;
    }

    public void setDonorId(int donorId) {
        donorIdField.setText(String.valueOf(donorId));
    }

    public void setBloodType(String bloodType) {
        bloodTypeField.setText(bloodType);
    }

    // Add constructor for editing existing donation history
    public DonationHistoryFormPanel(DonationHistory history) {
        this();  // Call default constructor to set up the panel
        donorIdField.setText(String.valueOf(history.getDonorId()));
        bloodTypeField.setText(history.getBloodType());
        locationField.setText(history.getLocation());
        unitsSpinner.setValue(history.getUnits());
        
        // Make donor ID field read-only in edit mode
        donorIdField.setEditable(false);
    }

    // Add method to update existing donation history
    public DonationHistory getUpdatedHistory(DonationHistory original) {
        DonationHistory history = new DonationHistory(
            Integer.parseInt(donorIdField.getText().trim()),
            bloodTypeField.getText().trim(),
            locationField.getText().trim()
        );
        history.setDonationId(original.getDonationId());
        history.setDonationDate(original.getDonationDate());
        history.setUnits((Integer) unitsSpinner.getValue());
        return history;
    }
}