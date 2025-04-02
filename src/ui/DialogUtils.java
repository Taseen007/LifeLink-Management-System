package ui;

import javax.swing.*;

class DialogUtils {
    public static void showSelectionWarning(JFrame parent, String itemType) {
        JOptionPane.showMessageDialog(parent,
                "Please select a " + itemType + " to perform this action",
                "No Selection",
                JOptionPane.WARNING_MESSAGE);
    }

    public static void showError(JFrame parent, String title, String message) {
        JOptionPane.showMessageDialog(parent,
                message,
                title,
                JOptionPane.ERROR_MESSAGE);
    }

    public static void showWarning(JFrame parent, String title, String message) {
        JOptionPane.showMessageDialog(parent,
                message,
                title,
                JOptionPane.WARNING_MESSAGE);
    }

    public static void showSuccess(JFrame parent, String message) {
        JOptionPane.showMessageDialog(parent,
                message,
                "Success",
                JOptionPane.INFORMATION_MESSAGE);
    }
}
