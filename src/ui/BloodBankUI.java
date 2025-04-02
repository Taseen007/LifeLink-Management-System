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

    public BloodBankUI() {
        initializeUI();
        initializeServices();
    }

    private void initializeUI() {
        configureFrame();
        createMenuBar();
        createTabbedPane();
        setupWindowListener();
    }

    private void configureFrame() {
        setTitle("LifeLink Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
    }

    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        JButton registerAsDonorBtn = new JButton("Register as Donor");
        registerAsDonorBtn.addActionListener(e -> showAddDonorDialog());
        menuBar.add(registerAsDonorBtn);
        setJMenuBar(menuBar);
    }

    private void createTabbedPane() {
        tabbedPane = new JTabbedPane();

        // Create panel managers
        donorPanelManager = new DonorPanelManager(this);
        inventoryPanelManager = new InventoryPanelManager(this);
        dialogManager = new DialogManager(this);

        // Setup donor panel
        JPanel donorPanel = donorPanelManager.createDonorPanel();
        tabbedPane.addTab("Donor Management", donorPanel);

        // Setup inventory panel
        JPanel inventoryPanel = inventoryPanelManager.createInventoryPanel();
        tabbedPane.addTab("Blood Inventory", inventoryPanel);

        add(tabbedPane);
    }

    private void setupWindowListener() {
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                cleanup();
            }
        });
    }

    private void initializeServices() {
        try {
            ServiceInitializer serviceInitializer = new ServiceInitializer();
            ServiceContainer services = serviceInitializer.initializeServices();

            // Get references to services
            this.dbConnection = services.getDbConnection();
            this.donorService = services.getDonorService();
            this.inventoryService = services.getInventoryService();

            // Initialize panel managers with services
            donorPanelManager.setDonorService(donorService);
            inventoryPanelManager.setInventoryService(inventoryService);
            dialogManager.setServices(donorService, inventoryService);

            // Set table references for the panel managers
            donorTableModel = donorPanelManager.getTableModel();
            donorTable = donorPanelManager.getTable();
            inventoryTableModel = inventoryPanelManager.getTableModel();
            inventoryTable = inventoryPanelManager.getTable();

            // Load initial data
            loadData();
        } catch (Exception e) {
            handleInitializationError(e);
        }
    }

    private void handleInitializationError(Exception e) {
        System.err.println("Error initializing services: " + e.getMessage());
        JOptionPane.showMessageDialog(this,
                "Error initializing services: " + e.getMessage(),
                "Initialization Error",
                JOptionPane.ERROR_MESSAGE);
        System.exit(1);
    }

    public void loadData() {
        try {
            List<Donor> donors = donorService.getAllDonors();
            updateDonorTable(donors);

            List<BloodInventory> inventory = inventoryService.getAllInventory();
            updateInventoryTable(inventory);
        } catch (Exception e) {
            handleDataLoadError(e);
        }
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
    public void showAddDonorDialog() {
        dialogManager.showAddDonorDialog();
    }

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

}