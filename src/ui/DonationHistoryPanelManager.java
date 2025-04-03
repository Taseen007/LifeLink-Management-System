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
        
        // Create toolbar with buttons
        JToolBar toolbar = new JToolBar();
        toolbar.setFloatable(false);
        
        JButton viewDetailsButton = new JButton("View Details");
        JButton exportButton = new JButton("Export History");
        
        viewDetailsButton.addActionListener(e -> ((BloodBankUI)parentFrame).showDonationDetailsDialog());
        exportButton.addActionListener(e -> ((BloodBankUI)parentFrame).exportDonationHistory());
        
        toolbar.add(viewDetailsButton);
        toolbar.add(exportButton);
        
        // Add components to panel
        panel.add(toolbar, BorderLayout.NORTH);
        panel.add(new JScrollPane(historyTable), BorderLayout.CENTER);
        
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

    // Add after your existing methods
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