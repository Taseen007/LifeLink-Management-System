package ui;

import models.Donor;
import services.interfaces.IDonorService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.List;  // Add this import

class DonorPanelManager {
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

    // Add a method to check if service is initialized
    private void checkServiceInitialization() {
        if (donorService == null) {
            throw new IllegalStateException("DonorService has not been initialized");
        }
    }

    // Update methods that use donorService to check initialization
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

        // Initialize table with proper settings
        donorTableModel = createDonorTableModel();
        donorTable = new JTable(donorTableModel);
        donorTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        donorTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        donorTable.getTableHeader().setReorderingAllowed(false);

        JScrollPane scrollPane = new JScrollPane(donorTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Add buttons panel
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
            
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 0) return Integer.class; // ID column
                if (columnIndex == 5) return Integer.class; // Age column
                return String.class;
            }
        };

        model.addColumn("ID");
        model.addColumn("Name");
        model.addColumn("Contact");
        model.addColumn("Email");
        model.addColumn("Gender");
        model.addColumn("Age");
        model.addColumn("Blood Type");
        model.addColumn("Location");
        model.addColumn("Last Donation");
        model.addColumn("Status");

        return model;
    }

    private JPanel createDonorButtonsPanel() {
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonsPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        JButton addDonorBtn = new JButton("Add Donor");
        JButton editDonorBtn = new JButton("Edit Donor");
        JButton deleteDonorBtn = new JButton("Delete Donor");
        JButton recordDonationBtn = new JButton("Record Donation");

        addDonorBtn.addActionListener(e -> ((BloodBankUI)parentFrame).showAddDonorDialog());
        editDonorBtn.addActionListener(e -> ((BloodBankUI)parentFrame).showEditDonorDialog());
        deleteDonorBtn.addActionListener(e -> ((BloodBankUI)parentFrame).deleteDonor());
        recordDonationBtn.addActionListener(e -> ((BloodBankUI)parentFrame).showRecordDonationDialog());

        buttonsPanel.add(addDonorBtn);
        buttonsPanel.add(editDonorBtn);
        buttonsPanel.add(deleteDonorBtn);
        buttonsPanel.add(recordDonationBtn);

        return buttonsPanel;
    }
}
