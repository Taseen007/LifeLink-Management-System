package ui;

import models.BloodInventory;

import javax.swing.*;
import java.awt.*;

class InventoryFormPanel extends JPanel {
    private static final String[] BLOOD_TYPES = {"A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"};
    
    private JComboBox<String> bloodTypeCombo;
    private JSpinner unitsSpinner;
    private JTextField statusField;

    public InventoryFormPanel() {
        setLayout(new GridLayout(3, 2, 5, 5));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        initializeComponents();
    }

    private void initializeComponents() {
        bloodTypeCombo = new JComboBox<>(BLOOD_TYPES);
        bloodTypeCombo.setPrototypeDisplayValue("Select Blood Type"); // Better dropdown width
        unitsSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1));
        statusField = new JTextField("Available");

        addComponentsToPanel();
    }

    private void addComponentsToPanel() {
        add(new JLabel("Blood Type:"));
        add(bloodTypeCombo);
        add(new JLabel("Units:"));
        add(unitsSpinner);
        add(new JLabel("Status:"));
        add(statusField);
    }

    public BloodInventory createInventory() {
        return new BloodInventory(
                (String) bloodTypeCombo.getSelectedItem(),
                (Integer) unitsSpinner.getValue(),
                statusField.getText()
        );
    }
}
