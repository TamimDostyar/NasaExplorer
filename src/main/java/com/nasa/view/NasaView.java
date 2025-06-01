package com.nasa.view;

import java.awt.*;
import java.awt.event.ActionListener;
import java.net.URL;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class NasaView extends JFrame {
    private JTextArea resultArea;
    private JComboBox<String> dateSelector;
    private JButton apodButton;
    private JButton marsRoverButton;
    private JButton neoButton;
    private JButton imageLibraryButton; 
    private JButton epicButton;
    private JTextField searchField;
    private JPanel contentPanel;
    private CardLayout cardLayout;
    private JLabel imageLabel;
    private JPanel mediaPanel;

    public NasaView() {
        setupMainFrame();
        setupComponents();
        setupLayout();
    }

    private void setupMainFrame() {
        setTitle("NASA Explorer");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 800);
        setLocationRelativeTo(null);
    }

    private void setupComponents() {
        // Buttons Panel with Title
        JPanel topPanel = new JPanel(new BorderLayout());
        
        // Add title
        JLabel titleLabel = new JLabel("NASA Space Explorer", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setBorder(new EmptyBorder(15, 0, 15, 0));
        topPanel.add(titleLabel, BorderLayout.NORTH);
        
        // Buttons Panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        apodButton = createStyledButton("Astronomy Picture of the Day");
        apodButton.setForeground(Color.BLACK);
        marsRoverButton = createStyledButton("Mars Rover Photos");
        marsRoverButton.setForeground(Color.BLACK);
        neoButton = createStyledButton("Near Earth Objects");
        neoButton.setForeground(Color.BLACK);
        imageLibraryButton = createStyledButton("NASA Image Library");
        imageLibraryButton.setForeground(Color.BLACK);
        epicButton = createStyledButton("EPIC Earth Images");
        epicButton.setForeground(Color.BLACK);

        buttonsPanel.add(apodButton);
        buttonsPanel.add(marsRoverButton);
        buttonsPanel.add(neoButton);
        buttonsPanel.add(imageLibraryButton);
        buttonsPanel.add(epicButton);
        topPanel.add(buttonsPanel, BorderLayout.CENTER);

        // Content Panel with CardLayout
        contentPanel = new JPanel();
        cardLayout = new CardLayout();
        contentPanel.setLayout(cardLayout);

        // Media Panel for Images and Videos
        mediaPanel = new JPanel(new BorderLayout());
        mediaPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        imageLabel = new JLabel();
        imageLabel.setHorizontalAlignment(JLabel.CENTER);
        mediaPanel.add(imageLabel, BorderLayout.CENTER);

        // Setup different feature panels
        setupAPODPanel();
        setupMarsRoverPanel();
        setupNEOPanel();
        setupImageLibraryPanel();
        setupEPICPanel();

        // Result Area
        resultArea = new JTextArea(10, 80);
        resultArea.setEditable(false);
        resultArea.setWrapStyleWord(true);
        resultArea.setLineWrap(true);
        resultArea.setFont(new Font("Arial", Font.PLAIN, 14));
        resultArea.setBackground(new Color(240, 240, 240));
        resultArea.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        JScrollPane scrollPane = new JScrollPane(resultArea);
        scrollPane.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Main Layout
        setLayout(new BorderLayout());
        add(topPanel, BorderLayout.NORTH);
        add(mediaPanel, BorderLayout.CENTER);
        add(contentPanel, BorderLayout.EAST);
        add(scrollPane, BorderLayout.SOUTH);
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFocusPainted(false);
        button.setBackground(new Color(30, 30, 30));
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(50, 50, 50), 1),
            BorderFactory.createEmptyBorder(10, 20, 10, 20)
        ));
        
        // Hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(50, 50, 50));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(30, 30, 30));
            }
        });
        
        return button;
    }

    private void setupAPODPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        panel.add(new JLabel("Select Date: "));
        dateSelector = new JComboBox<>(new String[]{"Today", "Yesterday", "Custom Date..."});
        panel.add(dateSelector);
        contentPanel.add(panel, "APOD");
    }

    private void setupMarsRoverPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        panel.add(new JLabel("Rover: "));
        JComboBox<String> roverSelector = new JComboBox<>(new String[]{"Curiosity", "Perseverance", "Opportunity", "Spirit"});
        panel.add(roverSelector);
        contentPanel.add(panel, "MARS");
    }

    private void setupNEOPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        panel.add(new JLabel("Start Date: "));
        panel.add(new JTextField(10));
        panel.add(new JLabel("End Date: "));
        panel.add(new JTextField(10));
        contentPanel.add(panel, "NEO");
    }

    private void setupImageLibraryPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        panel.add(new JLabel("Search: "));
        searchField = new JTextField(30);
        panel.add(searchField);
        contentPanel.add(panel, "LIBRARY");
    }

    private void setupEPICPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        panel.add(new JLabel("View: "));
        JComboBox<String> viewSelector = new JComboBox<>(new String[]{"Natural", "Enhanced"});
        panel.add(viewSelector);
        contentPanel.add(panel, "EPIC");
    }

    private void setupLayout() {
        // Additional layout setup if needed
    }

    public void addAPODListener(ActionListener listener) {
        apodButton.addActionListener(e -> {
            cardLayout.show(contentPanel, "APOD");
            listener.actionPerformed(e);
        });
    }

    public void addMarsRoverListener(ActionListener listener) {
        marsRoverButton.addActionListener(e -> {
            cardLayout.show(contentPanel, "MARS");
            listener.actionPerformed(e);
        });
    }

    public void addNEOListener(ActionListener listener) {
        neoButton.addActionListener(e -> {
            cardLayout.show(contentPanel, "NEO");
            listener.actionPerformed(e);
        });
    }

    public void addImageLibraryListener(ActionListener listener) {
        imageLibraryButton.addActionListener(e -> {
            cardLayout.show(contentPanel, "LIBRARY");
            listener.actionPerformed(e);
        });
    }

    public void addEPICListener(ActionListener listener) {
        epicButton.addActionListener(e -> {
            cardLayout.show(contentPanel, "EPIC");
            listener.actionPerformed(e);
        });
    }

    public void displayMedia(String mediaUrl, boolean isVideo) {
        try {
            if (isVideo) {
                displayVideo(mediaUrl);
            } else {
                // Load image in a background thread
                SwingWorker<ImageIcon, Void> worker = new SwingWorker<>() {
                    @Override
                    protected ImageIcon doInBackground() {
                        try {
                            URL url = new URL(mediaUrl);
                            return new ImageIcon(url);
                        } catch (Exception e) {
                            displayData("Error loading image: " + e.getMessage());
                            return null;
                        }
                    }

                    @Override
                    protected void done() {
                        try {
                            ImageIcon icon = get();
                            if (icon != null) {
                                displayImage(icon);
                            }
                        } catch (Exception e) {
                            displayData("Error displaying image: " + e.getMessage());
                        }
                    }
                };
                worker.execute();
            }
        } catch (Exception e) {
            displayData("Error handling media: " + e.getMessage());
        }
    }

    public void displayVideo(String videoUrl) {
        // Clear any existing content
        mediaPanel.removeAll();
        
        // Create a clickable link to the video
        JEditorPane editorPane = new JEditorPane();
        editorPane.setContentType("text/html");
        editorPane.setEditable(false);
        editorPane.setText(String.format(
            "<html><body style='text-align: center;'>" +
            "<p>This is a video content. Click below to open in browser:</p>" +
            "<a href='%s'>Open Video</a>" +
            "</body></html>", 
            videoUrl
        ));
        
        editorPane.addHyperlinkListener(e -> {
            if (e.getEventType() == javax.swing.event.HyperlinkEvent.EventType.ACTIVATED) {
                try {
                    Desktop.getDesktop().browse(new java.net.URI(videoUrl));
                } catch (Exception ex) {
                    displayData("Error opening video: " + ex.getMessage());
                }
            }
        });
        
        mediaPanel.add(editorPane, BorderLayout.CENTER);
        mediaPanel.revalidate();
        mediaPanel.repaint();
    }

    public void displayImage(ImageIcon originalImage) {
        try {
            // Clear any existing content
            mediaPanel.removeAll();
            
            if (originalImage != null && originalImage.getIconWidth() > 0) {
                // Get the original image dimensions
                int imgWidth = originalImage.getIconWidth();
                int imgHeight = originalImage.getIconHeight();
                
                // Get the panel dimensions
                int panelWidth = mediaPanel.getWidth();
                int panelHeight = mediaPanel.getHeight();
                
                // Calculate scaling factors
                double scaleX = (double) panelWidth / imgWidth;
                double scaleY = (double) panelHeight / imgHeight;
                double scale = Math.min(scaleX, scaleY) * 0.9; // 90% of available space
                
                // Calculate new dimensions
                int scaledWidth = (int) (imgWidth * scale);
                int scaledHeight = (int) (imgHeight * scale);
                
                // Scale the image
                Image img = originalImage.getImage();
                Image scaledImg = img.getScaledInstance(scaledWidth, scaledHeight, Image.SCALE_SMOOTH);
                
                // Create and configure the image label
                if (imageLabel == null) {
                    imageLabel = new JLabel();
                }
                imageLabel.setIcon(new ImageIcon(scaledImg));
                imageLabel.setHorizontalAlignment(JLabel.CENTER);
                imageLabel.setVerticalAlignment(JLabel.CENTER);
                
                // Add the label to a scroll pane
                JScrollPane scrollPane = new JScrollPane(imageLabel);
                scrollPane.setBorder(null);
                scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
                scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
                
                mediaPanel.add(scrollPane, BorderLayout.CENTER);
            } else {
                if (imageLabel == null) {
                    imageLabel = new JLabel();
                }
                imageLabel.setIcon(null);
                imageLabel.setText("No image available");
                imageLabel.setHorizontalAlignment(JLabel.CENTER);
                mediaPanel.add(imageLabel, BorderLayout.CENTER);
            }
            
            mediaPanel.revalidate();
            mediaPanel.repaint();
        } catch (Exception e) {
            displayData("Error displaying image: " + e.getMessage());
        }
    }

    public void displayData(String data) {
        resultArea.setText(data);
    }

    public String getSearchQuery() {
        return searchField.getText().trim();
    }
}
