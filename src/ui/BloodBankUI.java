package ui;

import models.*;
import services.interfaces.*;
import utils.interfaces.IConnectionProvider;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

public class BloodBankUI extends JFrame {
    // Dependencies
    private IConnectionProvider dbConnection;
    private IDonorService donorService;
    private IInventoryService inventoryService;
    private IBloodRequestService bloodRequestService;
    private IDonationHistoryService donationHistoryService;
    // Remove field
     // Remove this line
    
    // UI Components
    private JTabbedPane tabbedPane;
    private JTable donorTable;
    private JTable inventoryTable;
    private DefaultTableModel donorTableModel;
    private DefaultTableModel inventoryTableModel;

    // Constants
    private static final String[] GENDER_OPTIONS = {"Male", "Female", "Other"};
    private static final String[] BLOOD_TYPES = {"A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"};

    // UI Component Managers
    private DonorPanelManager donorPanelManager;
    private InventoryPanelManager inventoryPanelManager;
    private DialogManager dialogManager;

    // Add this field with other UI Component Managers
    private BloodRequestPanelManager requestPanelManager;

    // Add this with other service fields
    

    // Add these fields with other UI Component Managers
    private DonationHistoryPanelManager donationHistoryPanelManager;
   

    public BloodBankUI() {
        super("Blood Bank Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        // Initialize managers
        donorPanelManager = new DonorPanelManager(this);
        inventoryPanelManager = new InventoryPanelManager(this);
        requestPanelManager = new BloodRequestPanelManager(this);
        donationHistoryPanelManager = new DonationHistoryPanelManager(this);  // Add this
        dialogManager = new DialogManager(this);

        // Create main panel with tabs
        tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Donors", donorPanelManager.createDonorPanel());
        tabbedPane.addTab("Inventory", inventoryPanelManager.createInventoryPanel());
        tabbedPane.addTab("Blood Requests", requestPanelManager.createRequestPanel());
        tabbedPane.addTab("Donation History", donationHistoryPanelManager.createHistoryPanel());  // Add this

        add(tabbedPane);
        initializeServices();
        setVisible(true);
    }

    private void initializeServices() {
        try {
            ServiceInitializer serviceInitializer = new ServiceInitializer();
            ServiceContainer services = serviceInitializer.initializeServices();

            // Get references to services
            this.dbConnection = services.getDbConnection();
            this.donorService = services.getDonorService();
            this.inventoryService = services.getInventoryService();
            this.bloodRequestService = services.getBloodRequestService();
            this.donationHistoryService = services.getDonationHistoryService();

            // Initialize medical profile service


            // Initialize panel managers with services
            donorPanelManager.setDonorService(donorService);

            inventoryPanelManager.setInventoryService(inventoryService);
            requestPanelManager.setBloodRequestService(bloodRequestService);
            donationHistoryPanelManager.setDonationHistoryService(donationHistoryService);
            
            // Set all services in dialog manager
            dialogManager.setServices(donorService, inventoryService, 
                bloodRequestService, donationHistoryService);  // removed medicalProfileService

            // Load initial data
            loadData();
        } catch (Exception e) {
            handleInitializationError(e);
        }
    }

    public void loadData() {
        try {
            List<Donor> donors = donorService.getAllDonors();
            updateDonorTable(donors);

            List<BloodInventory> inventory = inventoryService.getAllInventory();
            updateInventoryTable(inventory);

            List<BloodRequest> requests = bloodRequestService.getAllRequests();
            requestPanelManager.updateRequestTable(requests);

            List<DonationHistory> histories = donationHistoryService.getAllDonationHistory();  // Add this
            donationHistoryPanelManager.updateHistoryTable(histories);  // Add this
        } catch (Exception e) {
            handleDataLoadError(e);
        }
    }

    // Add these new public methods at the end of the class
    public void showDonationDetailsDialog() {
        if (!donationHistoryPanelManager.isHistorySelected()) {
            DialogUtils.showSelectionWarning(this, "donation history");
            return;
        }

        int historyId = donationHistoryPanelManager.getSelectedHistoryId();
        dialogManager.showDonationDetailsDialog(historyId);
    }

    public void exportDonationHistory() {
        dialogManager.showExportHistoryDialog();
    }

    private void handleInitializationError(Exception e) {
        System.err.println("Error initializing services: " + e.getMessage());
        JOptionPane.showMessageDialog(this,
                "Error initializing services: " + e.getMessage(),
                "Initialization Error",
                JOptionPane.ERROR_MESSAGE);
        System.exit(1);
    }

    private void handleDataLoadError(Exception e) {
        System.err.println("Error loading data: " + e.getMessage());
        JOptionPane.showMessageDialog(this,
                "Error loading data: " + e.getMessage(),
                "Data Error",
                JOptionPane.ERROR_MESSAGE);
    }

    private void updateDonorTable(List<Donor> donors) {
        donorPanelManager.updateDonorTable(donors);
    }

    private void updateInventoryTable(List<BloodInventory> inventory) {
        inventoryPanelManager.updateInventoryTable(inventory);
    }

    private void cleanup() {
        try {
            if (dbConnection != null) {
                dbConnection.shutdown();
            }
        } catch (Exception e) {
            System.err.println("Error during cleanup: " + e.getMessage());
        }
    }

    // Public methods to maintain the original interface

    public void showEditDonorDialog() {
        if (donorPanelManager.isDonorSelected()) {
            DialogUtils.showSelectionWarning(this, "donor");
            return;
        }

        int donorId = donorPanelManager.getSelectedDonorId();
        dialogManager.showEditDonorDialog(donorId);
    }

    public void deleteDonor() {
        if (donorPanelManager.isDonorSelected()) {
            DialogUtils.showSelectionWarning(this, "donor");
            return;
        }

        int donorId = donorPanelManager.getSelectedDonorId();
        String donorName = donorPanelManager.getSelectedDonorName();
        dialogManager.showDeleteDonorConfirmation(donorId, donorName);
    }

    public void showRecordDonationDialog() {
        if (donorPanelManager.isDonorSelected()) {
            DialogUtils.showSelectionWarning(this, "donor");
            return;
        }

        int donorId = donorPanelManager.getSelectedDonorId();
        String bloodType = donorPanelManager.getSelectedDonorBloodType();
        dialogManager.showRecordDonationDialog(donorId, bloodType);
    }

    public void showAddInventoryDialog() {
        dialogManager.showAddInventoryDialog();
    }

    public void showUpdateStatusDialog() {
        if (!inventoryPanelManager.isInventorySelected()) {
            DialogUtils.showSelectionWarning(this, "inventory item");
            return;
        }

        int inventoryId = inventoryPanelManager.getSelectedInventoryId();
        String currentStatus = inventoryPanelManager.getSelectedInventoryStatus();
        dialogManager.showUpdateStatusDialog(inventoryId, currentStatus);
    }

    public void showInventoryStatus() {
        dialogManager.showInventoryStatus();
    }

    public void removeInventory() {
        if (!inventoryPanelManager.isInventorySelected()) {
            DialogUtils.showSelectionWarning(this, "inventory item");
            return;
        }

        int inventoryId = inventoryPanelManager.getSelectedInventoryId();
        dialogManager.showRemoveInventoryConfirmation(inventoryId);
    }

    public void showAddRequestDialog() {
        dialogManager.showAddRequestDialog();
    }

    public void showUpdateRequestStatusDialog() {
        if (!requestPanelManager.isRequestSelected()) {
            DialogUtils.showSelectionWarning(this, "blood request");
            return;
        }

        String requestId = requestPanelManager.getSelectedRequestId();
        String currentStatus = requestPanelManager.getSelectedRequestStatus();
        dialogManager.showUpdateRequestStatusDialog(requestId, currentStatus);
    }

    public void deleteRequest() {
        if (!requestPanelManager.isRequestSelected()) {
            DialogUtils.showSelectionWarning(this, "blood request");
            return;
        }

        String requestId = requestPanelManager.getSelectedRequestId();
        dialogManager.showDeleteRequestConfirmation(requestId);
    }

    public void showUpdateRequestDialog() {
        if (!requestPanelManager.isRequestSelected()) {  // Changed condition from if to if !
            DialogUtils.showError(this, "Error", "Please select a request to update");
            return;
        }

        int requestId = Integer.parseInt(requestPanelManager.getSelectedRequestId());
        BloodRequest request = bloodRequestService.getRequestById(requestId);
        
        if (request != null) {
            BloodRequestFormPanel formPanel = new BloodRequestFormPanel(request);
            int result = JOptionPane.showConfirmDialog(
                this,
                formPanel,
                "Update Blood Request",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
            );

            if (result == JOptionPane.OK_OPTION) {
                try {
                    formPanel.updateRequest(request);
                    bloodRequestService.updateRequest(request);
                    requestPanelManager.refreshRequestTable();
                    DialogUtils.showInfo(this, "Success", "Request updated successfully");
                } catch (Exception e) {
                    DialogUtils.showError(this, "Error", "Failed to update request: " + e.getMessage());
                }
            }
        }
    }


    // Add this method before the last closing brace
    public void showAddDonorDialog() {
        dialogManager.showAddDonorDialog();
    }
}