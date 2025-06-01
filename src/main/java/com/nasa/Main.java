package com.nasa;

import com.nasa.controller.NasaController;
import com.nasa.model.NasaModel;
import com.nasa.view.NasaView;
import java.io.File;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class Main {
    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) {
        setupLogging();
        LOGGER.info("Starting NASA Explorer application");
        
        try {
            // Log working directory and .env file location
            String workingDir = System.getProperty("user.dir");
            LOGGER.info("Working directory: " + workingDir);
            File envFile = new File(workingDir + "/.env");
            LOGGER.info(".env file exists: " + envFile.exists());
            if (envFile.exists()) {
                LOGGER.info(".env file path: " + envFile.getAbsolutePath());
                LOGGER.info(".env file can read: " + envFile.canRead());
            }

            // Set system look and feel
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            LOGGER.info("Set system look and feel");

            // Launch the application
            SwingUtilities.invokeLater(() -> {
                try {
                    LOGGER.info("Creating application components");
                    NasaModel model = new NasaModel();
                    NasaView view = new NasaView();
                    new NasaController(model, view);
                    LOGGER.info("Application components created successfully");
                    
                    view.setVisible(true);
                    LOGGER.info("Application window is now visible");
                } catch (Exception e) {
                    LOGGER.log(Level.SEVERE, "Error creating application components", e);
                }
            });
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error during application startup", e);
        }
    }

    private static void setupLogging() {
        try {
            // Get the user's home directory
            String userHome = System.getProperty("user.home");
            String logDir = userHome + "/Library/Logs/NasaExplorer";
            
            // Create the log directory if it doesn't exist
            new File(logDir).mkdirs();
            
            // Create FileHandler
            FileHandler fileHandler = new FileHandler(logDir + "/nasa_explorer.log", true);
            fileHandler.setFormatter(new SimpleFormatter());
            
            // Create ConsoleHandler
            ConsoleHandler consoleHandler = new ConsoleHandler();
            consoleHandler.setFormatter(new SimpleFormatter());
            
            // Configure the root logger
            Logger rootLogger = Logger.getLogger("");
            rootLogger.addHandler(fileHandler);
            rootLogger.addHandler(consoleHandler);
            rootLogger.setLevel(Level.INFO);
            
            LOGGER.info("Logging setup completed. Log file: " + logDir + "/nasa_explorer.log");
        } catch (Exception e) {
            System.err.println("Failed to setup logging: " + e.getMessage());
            e.printStackTrace();
        }
    }
} 