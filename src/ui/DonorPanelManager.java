package ui;

import models.Donor;
import services.interfaces.IDonorService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.List;  // Add this import

public class DonorPanelManager {
    private final JFrame parentFrame;
    private JTable donorTable;
    private DefaultTableModel donorTableModel;
    private IDonorService donorService;

    public DonorPanelManager(JFrame parentFrame) {
        this.parentFrame = parentFrame;
    }

    public void setDonorService(IDonorService donorService) {
        if (donorService == null) {
            throw new IllegalArgumentException("DonorService cannot be null");
        }
        this.donorService = donorService;
    }

    private void checkServiceInitialization() {
        if (donorService == null) {
            throw new IllegalStateException("DonorService has not been initialized");
        }
    }

    public void updateDonorTable(List<Donor> donors) {
        checkServiceInitialization();
        donorTableModel.setRowCount(0);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        for (Donor donor : donors) {
            String lastDonation = donor.getLastDonationDate() != null ?
                    sdf.format(donor.getLastDonationDate()) : "Never";

            donorTableModel.addRow(new Object[]{
                    donor.getDonorId(),
                    donor.getName(),
                    donor.getContact(),
                    donor.getEmail(),
                    donor.getGender(),
                    donor.getAge(),
                    donor.getBloodType(),
                    donor.getLocation(),
                    donor.getRegistrationDate() != null ? sdf.format(donor.getRegistrationDate()) : "N/A",
                    lastDonation,
                    donor.isEligible() ? "Eligible" : "Not Eligible"
            });
        }
    }

    public boolean isDonorSelected() {
        return donorTable == null || donorTable.getSelectedRow() < 0;
    }

    public int getSelectedDonorId() {
        if (donorTable == null || isDonorSelected()) {
            throw new IllegalStateException("No donor selected");
        }
        int selectedRow = donorTable.getSelectedRow();
        return (int) donorTable.getValueAt(selectedRow, 0);
    }

    public String getSelectedDonorName() {
        if (donorTable == null || isDonorSelected()) {
            throw new IllegalStateException("No donor selected");
        }
        int selectedRow = donorTable.getSelectedRow();
        return (String) donorTable.getValueAt(selectedRow, 1);
    }

    public String getSelectedDonorBloodType() {
        if (donorTable == null || isDonorSelected()) {
            throw new IllegalStateException("No donor selected");
        }
        int selectedRow = donorTable.getSelectedRow();
        return (String) donorTable.getValueAt(selectedRow, 6);
    }

    public JTable getTable() {
        return donorTable;
    }

    public DefaultTableModel getTableModel() {
        return donorTableModel;
    }

    public JPanel createDonorPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        donorTableModel = createDonorTableModel();
        donorTable = new JTable(donorTableModel);
        donorTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        donorTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        donorTable.getTableHeader().setReorderingAllowed(false);

        JScrollPane scrollPane = new JScrollPane(donorTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonsPanel = createDonorButtonsPanel();
        panel.add(buttonsPanel, BorderLayout.SOUTH);

        return panel;
    }

    private DefaultTableModel createDonorTableModel() {
        DefaultTableModel model = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        model.addColumn("Donor ID");
        model.addColumn("Name");
        model.addColumn("Contact");
        model.addColumn("Email");
        model.addColumn("Gender");
        model.addColumn("Age");
        model.addColumn("Blood Type");
        model.addColumn("Location");
        model.addColumn("Registration Date");
        model.addColumn("Last Donation");
        model.addColumn("Eligible");

        return model;
    }

    public void refreshDonorTable() {
        List<Donor> donors = donorService.getAllDonors();
        updateDonorTable(donors);
    }

    public void deleteDonor(int donorId) {
        try {
            donorService.deleteDonor(donorId);
            refreshDonorTable();
            DialogUtils.showSuccess(parentFrame, "Donor deleted successfully");
        } catch (Exception e) {
            DialogUtils.showError(parentFrame, "Error", "Failed to delete donor: " + e.getMessage());
        }
    }

    // Add this method after createDonorTableModel()
    private JPanel createDonorButtonsPanel() {
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        JButton addButton = new JButton("Add Donor");
        JButton editButton = new JButton("Edit Donor");
        JButton deleteButton = new JButton("Delete Donor");
        JButton recordDonationButton = new JButton("Record Donation");
        
        addButton.addActionListener(e -> ((BloodBankUI) parentFrame).showAddDonorDialog());
        editButton.addActionListener(e -> ((BloodBankUI) parentFrame).showEditDonorDialog());
        deleteButton.addActionListener(e -> {
            if (!isDonorSelected()) {
                deleteDonor(getSelectedDonorId());
            }
        });
        recordDonationButton.addActionListener(e -> ((BloodBankUI) parentFrame).showRecordDonationDialog());
        
        buttonsPanel.add(addButton);
        buttonsPanel.add(editButton);
        buttonsPanel.add(deleteButton);
        buttonsPanel.add(recordDonationButton);
        
        return buttonsPanel;
    }
}
