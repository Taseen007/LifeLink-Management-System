package utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Logger {
    private static final String LOG_DIR = "logs";
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    
    static {
        // Create logs directory if it doesn't exist
        File logDir = new File(LOG_DIR);
        if (!logDir.exists()) {
            logDir.mkdir();
        }
    }

    public static void info(String message) {
        log("INFO", message, null);
    }

    public static void error(String message, Exception e) {
        log("ERROR", message, e);
        // Also print to console for immediate visibility
        System.err.println(formatLogMessage("ERROR", message));
        if (e != null) {
            e.printStackTrace();
        }
    }

    private static void log(String level, String message, Exception e) {
        try {
            String logFile = LOG_DIR + File.separator + "application.log";
            try (PrintWriter writer = new PrintWriter(new FileWriter(logFile, true))) {
                writer.println(formatLogMessage(level, message));
                if (e != null) {
                    e.printStackTrace(writer);
                }
            }
        } catch (IOException ex) {
            // If we can't write to the log file, at least print to console
            System.err.println("Failed to write to log file: " + ex.getMessage());
            System.err.println(formatLogMessage(level, message));
            if (e != null) {
                e.printStackTrace();
            }
        }
    }

    private static String formatLogMessage(String level, String message) {
        return String.format("[%s] %s: %s",
            DATE_FORMAT.format(new Date()),
            level,
            message);
    }
}
