package ui;

import models.DonationHistory;
import services.interfaces.IDonationHistoryService;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class DonationHistoryPanelManager {
    private final JFrame parentFrame;
    private IDonationHistoryService donationHistoryService;
    private JTable historyTable;
    private DefaultTableModel tableModel;

    public DonationHistoryPanelManager(JFrame parentFrame) {
        this.parentFrame = parentFrame;
        initializeTable();
    }

    public void setDonationHistoryService(IDonationHistoryService service) {
        this.donationHistoryService = service;
    }

    private void initializeTable() {
        String[] columns = {"Donation ID", "Donor ID", "Donation Date", "Blood Type", "Units", "Location"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        historyTable = new JTable(tableModel);
        historyTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

    public void updateHistoryTable(List<DonationHistory> histories) {
        tableModel.setRowCount(0);
        for (DonationHistory history : histories) {
            Object[] row = {
                history.getDonationId(),
                history.getDonorId(),
                history.getDonationDate(),
                history.getBloodType(),
                history.getUnits(),
                history.getLocation()
            };
            tableModel.addRow(row);
        }
    }

    public JPanel createHistoryPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // Create table with the existing model
        historyTable = new JTable(tableModel);
        historyTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(historyTable);
        
        // Create button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton updateButton = new JButton("Update");
        JButton deleteButton = new JButton("Delete");
        JButton exportButton = new JButton("Export");
        
        // Add action listeners with proper casting
        updateButton.addActionListener(e -> ((BloodBankUI)parentFrame).showDonationDetailsDialog());
        deleteButton.addActionListener(e -> ((BloodBankUI)parentFrame).deleteDonationHistory());
        exportButton.addActionListener(e -> ((BloodBankUI)parentFrame).exportDonationHistory());
        
        // Add buttons to panel
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(exportButton);
        
        // Add components to main panel
        panel.add(buttonPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }


    public boolean isHistorySelected() {
        return historyTable.getSelectedRow() != -1;
    }

    public int getSelectedHistoryId() {
        int row = historyTable.getSelectedRow();
        return (int) historyTable.getValueAt(row, 0);
    }

    public DefaultTableModel getTableModel() {
        return tableModel;
    }

    public JTable getTable() {
        return historyTable;
    }

    // Keep these methods as they are the responsibility of this class
        public void addDonationHistory(DonationHistory history) {
            try {
                donationHistoryService.addDonationHistory(history);
                refreshTable();
            } catch (Exception e) {
                DialogUtils.showError(parentFrame, "Error", "Failed to add donation: " + e.getMessage());
            }
        }
    
        public void updateDonationHistory(DonationHistory history) {
            try {
                donationHistoryService.updateDonationHistory(history);
                refreshTable();
            } catch (Exception e) {
                DialogUtils.showError(parentFrame, "Error", "Failed to update donation: " + e.getMessage());
            }
        }
    
        public void deleteDonationHistory(int donationId) {
            try {
                donationHistoryService.deleteDonationHistory(donationId);
                refreshTable();
            } catch (Exception e) {
                DialogUtils.showError(parentFrame, "Error", "Failed to delete donation: " + e.getMessage());
            }
        }
    
        private void refreshTable() {
            List<DonationHistory> histories = donationHistoryService.getAllDonationHistory();
            updateHistoryTable(histories);
        }
    
        public List<DonationHistory> getDonorHistory(int donorId) {
            return donationHistoryService.getDonationHistoryByDonorId(donorId);
        }
}