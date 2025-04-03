package ui;

import models.BloodInventory;
import services.interfaces.IInventoryService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.List;

class InventoryPanelManager {
    private final JFrame parentFrame;
    private JTable inventoryTable;
    private DefaultTableModel inventoryTableModel;
    private IInventoryService inventoryService;

    public InventoryPanelManager(JFrame parentFrame) {
        this.parentFrame = parentFrame;
    }

    public void setInventoryService(IInventoryService inventoryService) {
        if (inventoryService == null) {
            throw new IllegalArgumentException("InventoryService cannot be null");
        }
        this.inventoryService = inventoryService;
    }

    private void checkServiceInitialization() {
        if (inventoryService == null) {
            throw new IllegalStateException("InventoryService has not been initialized");
        }
    }

    // Update methods that use inventoryService to check initialization
    public void updateInventoryTable(List<BloodInventory> inventory) {
        checkServiceInitialization();
        inventoryTableModel.setRowCount(0);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        for (BloodInventory item : inventory) {
            inventoryTableModel.addRow(new Object[]{
                    item.getInventoryId(),
                    item.getBloodType(),
                    item.getUnits(),
                    item.getStatus(),
                    sdf.format(item.getLastUpdated())
            });
        }
    }

    public boolean isInventorySelected() {
        if (inventoryTable == null) {
            return false;
        }
        return inventoryTable.getSelectedRow() >= 0;
    }

    public int getSelectedInventoryId() {
        if (inventoryTable == null || !isInventorySelected()) {
            throw new IllegalStateException("No inventory selected");
        }
        int selectedRow = inventoryTable.getSelectedRow();
        return (int) inventoryTable.getValueAt(selectedRow, 0);
    }

    public String getSelectedInventoryStatus() {
        if (inventoryTable == null || !isInventorySelected()) {
            throw new IllegalStateException("No inventory selected");
        }
        int selectedRow = inventoryTable.getSelectedRow();
        return (String) inventoryTable.getValueAt(selectedRow, 3);
    }

    // Remove these unused methods:
    /*
    public JTable getTable() {
        return inventoryTable;
    }

    public DefaultTableModel getTableModel() {
        return inventoryTableModel;
    }
    */

    public JPanel createInventoryPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Initialize table with proper settings
        inventoryTableModel = createInventoryTableModel();
        inventoryTable = new JTable(inventoryTableModel);
        inventoryTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        inventoryTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        inventoryTable.getTableHeader().setReorderingAllowed(false);

        JScrollPane scrollPane = new JScrollPane(inventoryTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Add buttons panel
        JPanel buttonsPanel = createInventoryButtonsPanel();
        panel.add(buttonsPanel, BorderLayout.SOUTH);

        return panel;
    }

    private DefaultTableModel createInventoryTableModel() {
        DefaultTableModel model = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
            
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 0) return Integer.class; // ID column
                if (columnIndex == 2) return Integer.class; // Units column
                return String.class;
            }
        };

        model.addColumn("ID");
        model.addColumn("Blood Type");
        model.addColumn("Units");
        model.addColumn("Status");
        model.addColumn("Last Updated");

        return model;
    }

    private JPanel createInventoryButtonsPanel() {
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonsPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        JButton addInventoryBtn = new JButton("Add Inventory");
        JButton updateStatusBtn = new JButton("Update Status");
        JButton showStatusBtn = new JButton("Show Status");
        JButton removeInventoryBtn = new JButton("Remove");

        addInventoryBtn.addActionListener(e -> ((BloodBankUI)parentFrame).showAddInventoryDialog());
        updateStatusBtn.addActionListener(e -> ((BloodBankUI)parentFrame).showUpdateStatusDialog());
        showStatusBtn.addActionListener(e -> ((BloodBankUI)parentFrame).showInventoryStatus());
        removeInventoryBtn.addActionListener(e -> ((BloodBankUI)parentFrame).removeInventory());

        buttonsPanel.add(addInventoryBtn);
        buttonsPanel.add(updateStatusBtn);
        buttonsPanel.add(showStatusBtn);
        buttonsPanel.add(removeInventoryBtn);

        return buttonsPanel;
    }
}
