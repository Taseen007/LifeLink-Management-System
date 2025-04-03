package ui;

import models.BloodInventory;
import models.BloodRequest;
import models.DonationHistory;
import models.Donor;
import services.interfaces.*;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.List;

// Add this import at the top with other imports


class DialogManager {
    // Remove this field if it exists
     // Remove this line
    
    // The rest of your fields remain the same
    private final JFrame parentFrame;
    private IDonorService donorService;
    private IInventoryService inventoryService;
    private IBloodRequestService bloodRequestService;
    private IDonationHistoryService donationHistoryService;
    private static final String[] GENDER_OPTIONS = {"Male", "Female", "Other"};
    private static final String[] BLOOD_TYPES = {"A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"};

    public DialogManager(JFrame parentFrame) {
        this.parentFrame = parentFrame;
    }

    public void setServices(IDonorService donorService, 
                          IInventoryService inventoryService,
                          IBloodRequestService bloodRequestService,
                          IDonationHistoryService donationHistoryService) {  // Removed medicalProfileService
        this.donorService = donorService;
        this.inventoryService = inventoryService;
        this.bloodRequestService = bloodRequestService;
        this.donationHistoryService = donationHistoryService;
    }

    // Remove medical profile service import if it exists at the top
    // Remove the medical profile field (already done)
    
    // Fix the showAddDonorDialog method by removing static keyword
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
        DonationHistoryFormPanel formPanel = new DonationHistoryFormPanel();
        formPanel.setDonorId(donorId);
        formPanel.setBloodType(bloodType);
        
        JPanel buttonPanel = createButtonPanel(
            formPanel,
            "Save",
            () -> {
                try {
                    DonationHistory history = formPanel.createDonationHistory();
                    ((BloodBankUI) parentFrame).getDonationHistoryPanelManager().addDonationHistory(history);

                    // Update inventory
                    BloodInventory inventory = new BloodInventory(
                        bloodType,
                        history.getUnits(),
                        "Available"
                    );
                    inventoryService.addInventory(inventory);
                    DialogUtils.showSuccess(parentFrame, "Donation recorded successfully");
                } catch (Exception ex) {
                    DialogUtils.showError(parentFrame, "Error", "Error recording donation: " + ex.getMessage());
                }
            },
            dialog
        );

        dialog.setLayout(new BorderLayout());
        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.pack();
        dialog.setLocationRelativeTo(parentFrame);
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

    // Add new field at the top with other services


    // Add these new methods at the end of the class
    public void showAddRequestDialog() {
        JDialog dialog = new JDialog(parentFrame, "New Blood Request", true);
        BloodRequestFormPanel formPanel = new BloodRequestFormPanel();
        
        JPanel buttonPanel = createButtonPanel(
            formPanel,
            "Save",
            () -> {
                try {
                    BloodRequest request = formPanel.createRequest();
                    bloodRequestService.createRequest(request);
                    ((BloodBankUI)parentFrame).loadData();
                    DialogUtils.showSuccess(parentFrame, "Blood request created successfully");
                } catch (Exception ex) {
                    DialogUtils.showError(parentFrame, "Error", "Failed to create blood request: " + ex.getMessage());
                }
            },
            dialog
        );

        dialog.setLayout(new BorderLayout());
        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.pack();
        dialog.setLocationRelativeTo(parentFrame);
        dialog.setVisible(true);
    }

    public void showUpdateRequestStatusDialog(String requestId, String currentStatus) {
        String[] statusOptions = {"Pending", "Approved", "Fulfilled", "Cancelled"};
        JComboBox<String> statusCombo = new JComboBox<>(statusOptions);
        statusCombo.setSelectedItem(currentStatus);

        Object[] message = {
            "Select new status:",
            statusCombo
        };

        int option = JOptionPane.showConfirmDialog(parentFrame, message, "Update Request Status",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);

        if (option == JOptionPane.OK_OPTION) {
            try {
                String newStatus = (String) statusCombo.getSelectedItem();
                bloodRequestService.updateRequestStatus(Integer.parseInt(requestId), newStatus);
                ((BloodBankUI)parentFrame).loadData();
                DialogUtils.showSuccess(parentFrame, "Request status updated successfully");
            } catch (Exception e) {
                DialogUtils.showError(parentFrame, "Error", "Failed to update request status: " + e.getMessage());
            }
        }
    }

    public void showDeleteRequestConfirmation(String requestId) {
        int option = JOptionPane.showConfirmDialog(parentFrame,
                "Are you sure you want to delete this blood request?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (option == JOptionPane.YES_OPTION) {
            try {
                bloodRequestService.deleteRequest(Integer.parseInt(requestId));
                ((BloodBankUI)parentFrame).loadData();
                DialogUtils.showSuccess(parentFrame, "Blood request deleted successfully");
            } catch (Exception e) {
                DialogUtils.showError(parentFrame, "Error", "Failed to delete blood request: " + e.getMessage());
            }
        }
    }

    // Add these new methods at the end of the class
    public void showDonationDetailsDialog(int historyId) {
        try {
            DonationHistory history = donationHistoryService.getDonationHistoryById(historyId);
            if (history != null) {
                JDialog dialog = new JDialog(parentFrame, "Donation Details", true);
                DonationHistoryFormPanel formPanel = new DonationHistoryFormPanel(history);
                
                JPanel buttonPanel = createButtonPanel(
                    formPanel,
                    "Update",
                    () -> {
                        try {
                            DonationHistory updatedHistory = formPanel.getUpdatedHistory(history);
                            ((BloodBankUI) parentFrame).getDonationHistoryPanelManager().updateDonationHistory(updatedHistory);
                            DialogUtils.showSuccess(parentFrame, "Donation history updated successfully");
                        } catch (Exception ex) {
                            DialogUtils.showError(parentFrame, "Error", "Failed to update donation history: " + ex.getMessage());
                        }
                    },
                    dialog
                );

                dialog.setLayout(new BorderLayout());
                dialog.add(formPanel, BorderLayout.CENTER);
                dialog.add(buttonPanel, BorderLayout.SOUTH);
                dialog.pack();
                dialog.setLocationRelativeTo(parentFrame);
                dialog.setVisible(true);
            }
        } catch (Exception e) {
            DialogUtils.showError(parentFrame, "Error", "Failed to load donation details: " + e.getMessage());
        }
    }

    // Remove this unused method
    /*
    private String formatDonationRecords(List<String> records) {
        StringBuilder sb = new StringBuilder();
        for (String record : records) {
            sb.append(record).append("\n");
        }
        return sb.toString();
    }
    */

    public void showExportHistoryDialog() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Export Donation History");
        fileChooser.setSelectedFile(new File("donation_history.csv"));

        if (fileChooser.showSaveDialog(parentFrame) == JFileChooser.APPROVE_OPTION) {
            try {
                List<DonationHistory> histories = donationHistoryService.getAllDonationHistory();
                exportToCSV(histories, fileChooser.getSelectedFile());
                DialogUtils.showSuccess(parentFrame, "History exported successfully");
            } catch (Exception e) {
                DialogUtils.showError(parentFrame, "Export Error", "Failed to export history: " + e.getMessage());
            }
        }
    }

    private void exportToCSV(List<DonationHistory> histories, File file) throws IOException {
        try (PrintWriter writer = new PrintWriter(file)) {
            writer.println("Donation ID,Donor ID,Blood Type,Units,Location,Donation Date");
            for (DonationHistory history : histories) {
                writer.printf("%d,%d,%s,%d,%s,%s%n",
                    history.getDonationId(),
                    history.getDonorId(),
                    history.getBloodType(),
                    history.getUnits(),
                    history.getLocation(),
                    history.getDonationDate()
                );
            }
        }
    }
}