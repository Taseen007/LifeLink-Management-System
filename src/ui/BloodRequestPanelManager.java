package ui;

import models.BloodRequest;
import services.interfaces.IBloodRequestService;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.List;

class BloodRequestPanelManager {
    private final JFrame parentFrame;
    private JTable requestTable;
    private DefaultTableModel requestTableModel;
    private IBloodRequestService bloodRequestService;

    public BloodRequestPanelManager(JFrame parentFrame) {
        this.parentFrame = parentFrame;
    }

    public void setBloodRequestService(IBloodRequestService bloodRequestService) {
        if (bloodRequestService == null) {
            throw new IllegalArgumentException("BloodRequestService cannot be null");
        }
        this.bloodRequestService = bloodRequestService;
    }

    private void checkServiceInitialization() {
        if (bloodRequestService == null) {
            throw new IllegalStateException("BloodRequestService has not been initialized");
        }
    }

    public void updateRequestTable(List<BloodRequest> requests) {
        checkServiceInitialization();
        requestTableModel.setRowCount(0);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        for (BloodRequest request : requests) {
            requestTableModel.addRow(new Object[]{
                request.getRequestId(),
                request.getPatientName(),
                request.getBloodType(),
                request.getUnitsNeeded(),
                request.getUrgency(),
                request.getHospital(),
                sdf.format(request.getRequestDate()),
                sdf.format(request.getRequiredDate()),
                request.getStatus()
            });
        }
    }

    public boolean isRequestSelected() {
        int selectedRow = requestTable.getSelectedRow();
        return selectedRow != -1;  // Returns true if a row is selected, false otherwise
    }

    public String getSelectedRequestId() {
        int selectedRow = requestTable.getSelectedRow();
        if (selectedRow != -1) {
            return requestTable.getValueAt(selectedRow, 0).toString();
        }
        return null;
    }

    public String getSelectedRequestStatus() {
        if (!isRequestSelected()) {  // Changed from isRequestSelected() to !isRequestSelected()
            throw new IllegalStateException("No request selected");
        }
        int selectedRow = requestTable.getSelectedRow();
        return (String) requestTable.getValueAt(selectedRow, 8);
    }

    public JTable getTable() {
        return requestTable;
    }

    public DefaultTableModel getTableModel() {
        return requestTableModel;
    }

    public JPanel createRequestPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        requestTableModel = createRequestTableModel();
        requestTable = new JTable(requestTableModel);
        requestTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        requestTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        requestTable.getTableHeader().setReorderingAllowed(false);

        JScrollPane scrollPane = new JScrollPane(requestTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonsPanel = createRequestButtonsPanel();
        panel.add(buttonsPanel, BorderLayout.SOUTH);

        return panel;
    }

    private DefaultTableModel createRequestTableModel() {
        DefaultTableModel model = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
            
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 0) return String.class;  // Request ID
                if (columnIndex == 3) return Integer.class; // Units needed
                return String.class;
            }
        };

        model.addColumn("Request ID");
        model.addColumn("Patient Name");
        model.addColumn("Blood Type");
        model.addColumn("Units Needed");
        model.addColumn("Urgency");
        model.addColumn("Hospital");
        model.addColumn("Request Date");
        model.addColumn("Required Date");
        model.addColumn("Status");

        return model;
    }

    private JPanel createRequestButtonsPanel() {
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonsPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        JButton addRequestBtn = new JButton("New Request");
        JButton updateStatusBtn = new JButton("Update Status");
        JButton deleteRequestBtn = new JButton("Delete Request");

        addRequestBtn.addActionListener(e -> ((BloodBankUI)parentFrame).showAddRequestDialog());
        updateStatusBtn.addActionListener(e -> ((BloodBankUI)parentFrame).showUpdateRequestStatusDialog());
        deleteRequestBtn.addActionListener(e -> ((BloodBankUI)parentFrame).deleteRequest());

        buttonsPanel.add(addRequestBtn);
        buttonsPanel.add(updateStatusBtn);
        buttonsPanel.add(deleteRequestBtn);

        return buttonsPanel;
    }

    public void refreshRequestTable() {
        List<BloodRequest> requests = bloodRequestService.getAllRequests();
        updateRequestTable(requests);
    }

    public void updateRequestStatus(int requestId, String newStatus) {
        try {
            bloodRequestService.updateRequestStatus(requestId, newStatus);
            refreshRequestTable();
        } catch (Exception e) {
            DialogUtils.showError(parentFrame, "Error", "Failed to update request status: " + e.getMessage());
        }
    }
}