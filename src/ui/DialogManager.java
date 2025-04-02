package ui;

import models.BloodInventory;
import models.Donor;
import services.interfaces.IDonorService;
import services.interfaces.IInventoryService;

import javax.swing.*;
import java.awt.*;
import java.util.Date;
import java.util.List;

class DialogManager {
    private final JFrame parentFrame;
    private IDonorService donorService;
    private IInventoryService inventoryService;
    private static final String[] GENDER_OPTIONS = {"Male", "Female", "Other"};
    private static final String[] BLOOD_TYPES = {"A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"};

    public DialogManager(JFrame parentFrame) {
        this.parentFrame = parentFrame;
    }

    public void setServices(IDonorService donorService, IInventoryService inventoryService) {
        this.donorService = donorService;
        this.inventoryService = inventoryService;
    }

    public void showAddDonorDialog() {
        JDialog dialog = new JDialog(parentFrame, "Register as Donor", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(400, 500);
        dialog.setLocationRelativeTo(parentFrame);

        DonorFormPanel formPanel = new DonorFormPanel();
        JPanel buttonPanel = createButtonPanel(
                formPanel,
                "Register",
                () -> registerDonor(formPanel),
                dialog
        );

        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void registerDonor(DonorFormPanel formPanel) {
        try {
            // Validate fields
            formPanel.validateFields();

            Donor donor = formPanel.createDonor();
            donorService.registerDonor(donor);

            // Update UI
            ((BloodBankUI) parentFrame).loadData();

            // Show success message
            JOptionPane.showMessageDialog(parentFrame,
                    "Thank you for registering as a donor!\n" +
                            "Your donor ID is: " + donor.getDonorId() + "\n" +
                            "Please keep this ID for future reference.",
                    "Registration Successful",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            DialogUtils.showError(parentFrame, "Registration Error", ex.getMessage());
        }
    }

    public void showEditDonorDialog(int donorId) {
        Donor donor = donorService.getDonorById(donorId);
        if (donor == null) {
            DialogUtils.showError(parentFrame, "Error", "Error loading donor data");
            return;
        }

        JDialog dialog = new JDialog(parentFrame, "Edit Donor", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(400, 500);
        dialog.setLocationRelativeTo(parentFrame);

        DonorFormPanel formPanel = new DonorFormPanel(donor);
        JPanel buttonPanel = createButtonPanel(
                formPanel,
                "Save",
                () -> updateDonor(donor, formPanel),
                dialog
        );

        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void updateDonor(Donor donor, DonorFormPanel formPanel) {
        try {
            formPanel.updateDonor(donor);
            donorService.updateDonor(donor);

            // Update UI
            ((BloodBankUI) parentFrame).loadData();

            // Show success message
            DialogUtils.showSuccess(parentFrame, "Donor updated successfully");
        } catch (Exception ex) {
            DialogUtils.showError(parentFrame, "Error", "Error updating donor: " + ex.getMessage());
        }
    }

    public void showDeleteDonorConfirmation(int donorId, String donorName) {
        int confirm = JOptionPane.showConfirmDialog(parentFrame,
                "Are you sure you want to delete donor: " + donorName + "?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                donorService.deleteDonor(donorId);  // Make sure this method exists in your IDonorService

                // Update UI
                ((BloodBankUI)parentFrame).loadData();

                // Show success message
                DialogUtils.showSuccess(parentFrame, "Donor deleted successfully");
            } catch (Exception e) {
                DialogUtils.showError(parentFrame, "Delete Error", "Error deleting donor: " + e.getMessage());
            }
        }
    }

    public void showRecordDonationDialog(int donorId, String bloodType) {
        if (!donorService.isDonorEligibleForDonation(donorId)) {
            DialogUtils.showWarning(parentFrame, "Not Eligible",
                    "This donor is not eligible for donation at this time");
            return;
        }

        JDialog dialog = new JDialog(parentFrame, "Record Donation", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(300, 200);
        dialog.setLocationRelativeTo(parentFrame);

        JPanel formPanel = new JPanel(new GridLayout(3, 2, 5, 5));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JTextField locationField = new JTextField();
        JSpinner unitsSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 5, 1));

        formPanel.add(new JLabel("Location:"));
        formPanel.add(locationField);
        formPanel.add(new JLabel("Units:"));
        formPanel.add(unitsSpinner);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");

        saveButton.addActionListener(e -> {
            try {
                donorService.recordDonation(donorId, new Date(), locationField.getText());

                // Update inventory
                BloodInventory inventory = new BloodInventory(
                        bloodType,
                        (Integer) unitsSpinner.getValue(),
                        "Available"
                );
                inventoryService.addInventory(inventory);

                // Update UI
                ((BloodBankUI) parentFrame).loadData();
                dialog.dispose();

                // Show success message
                DialogUtils.showSuccess(parentFrame, "Donation recorded successfully");
            } catch (Exception ex) {
                DialogUtils.showError(parentFrame, "Error", "Error recording donation: " + ex.getMessage());
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    public void showAddInventoryDialog() {
        JDialog dialog = new JDialog(parentFrame, "Add Blood Inventory", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(300, 200);
        dialog.setLocationRelativeTo(parentFrame);

        InventoryFormPanel formPanel = new InventoryFormPanel();
        JPanel buttonPanel = createButtonPanel(
                formPanel,
                "Save",
                () -> addInventory(formPanel),
                dialog
        );

        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void addInventory(InventoryFormPanel formPanel) {
        try {
            BloodInventory inventory = formPanel.createInventory();
            inventoryService.addInventory(inventory);

            // Update UI
            ((BloodBankUI) parentFrame).loadData();

            // Show success message
            DialogUtils.showSuccess(parentFrame, "Inventory added successfully");
        } catch (Exception ex) {
            DialogUtils.showError(parentFrame, "Error", "Error adding inventory: " + ex.getMessage());
        }
    }

    public void showUpdateStatusDialog(int inventoryId, String currentStatus) {
        JDialog dialog = new JDialog(parentFrame, "Update Inventory Status", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(300, 150);
        dialog.setLocationRelativeTo(parentFrame);

        JPanel formPanel = new JPanel(new GridLayout(2, 2, 5, 5));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JTextField statusField = new JTextField(currentStatus);
        formPanel.add(new JLabel("New Status:"));
        formPanel.add(statusField);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");

        saveButton.addActionListener(e -> {
            try {
                inventoryService.updateStatus(inventoryId, statusField.getText());

                // Update UI
                ((BloodBankUI) parentFrame).loadData();
                dialog.dispose();

                // Show success message
                DialogUtils.showSuccess(parentFrame, "Status updated successfully");
            } catch (Exception ex) {
                DialogUtils.showError(parentFrame, "Error", "Error updating status: " + ex.getMessage());
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    public void showInventoryStatus() {
        try {
            List<BloodInventory> inventory = inventoryService.getAllInventory();
            StringBuilder status = new StringBuilder("Current Blood Inventory Status:\n\n");

            for (BloodInventory item : inventory) {
                status.append(String.format("Blood Type: %s\nUnits: %d\nStatus: %s\n\n",
                        item.getBloodType(), item.getUnits(), item.getStatus()));
            }

            JOptionPane.showMessageDialog(parentFrame,
                    status.toString(),
                    "Inventory Status",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            DialogUtils.showError(parentFrame, "Error", "Error getting inventory status: " + e.getMessage());
        }
    }

    public void showRemoveInventoryConfirmation(int inventoryId) {
        int confirm = JOptionPane.showConfirmDialog(parentFrame,
                "Are you sure you want to remove this inventory item?",
                "Confirm Remove",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                inventoryService.removeInventory(inventoryId);

                // Update UI
                ((BloodBankUI) parentFrame).loadData();

                // Show success message
                DialogUtils.showSuccess(parentFrame, "Inventory removed successfully");
            } catch (Exception e) {
                DialogUtils.showError(parentFrame, "Remove Error", "Error removing inventory: " + e.getMessage());
            }
        }
    }

    private JPanel createButtonPanel(JPanel formPanel, String saveButtonText, Runnable saveAction, JDialog dialog) {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton(saveButtonText);
        JButton cancelButton = new JButton("Cancel");

        saveButton.addActionListener(e -> {
            saveAction.run();
            dialog.dispose();
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        return buttonPanel;
    }

}