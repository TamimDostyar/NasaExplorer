package com.nasa.controller;

import com.nasa.model.NasaModel;
import com.nasa.view.NasaView;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;
import javax.swing.ImageIcon;
import java.net.URL;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import io.github.cdimascio.dotenv.Dotenv;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.io.File;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import java.util.logging.Level;
import java.util.logging.Logger;

public class NasaController {
    private static final Logger LOGGER = Logger.getLogger(NasaController.class.getName());
    private final NasaModel model;
    private final NasaView view;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private Dotenv dotenv;
    private String apiKey;
    
    // NASA API endpoints
    private static final String APOD_URL = "https://api.nasa.gov/planetary/apod";
    private static final String MARS_ROVER_URL = "https://api.nasa.gov/mars-photos/api/v1/rovers/curiosity/photos";
    private static final String NEO_URL = "https://api.nasa.gov/neo/rest/v1/feed";
    private static final String IMAGE_LIBRARY_URL = "https://images-api.nasa.gov/search";
    private static final String EPIC_URL = "https://epic.gsfc.nasa.gov/api/natural";

    private boolean isApiKeyValid() {
        if (apiKey == null || apiKey.trim().isEmpty()) {
            return false;
        }
        return true;
    }

    private String loadApiKey() {
        try {
            // WARNING: This is a personal-use fallback API key. 
            // DO NOT distribute this code or make it public.
            // Remove this key before sharing the code.
            final String PERSONAL_API_KEY = "DEMO_KEY";

            // Try loading from environment variable first
            String apiKey = System.getenv("NASA_API_KEY");
            if (apiKey != null && !apiKey.isEmpty()) {
                return apiKey;
            }

            // List of possible .env file locations
            String[] possibleLocations = {
                System.getProperty("user.dir") + "/.env",
                System.getProperty("user.home") + "/.env",
                new File(NasaController.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParent() + "/.env",
                "./app/.env"
            };

            // Try each location
            for (String location : possibleLocations) {
                File envFile = new File(location);
                if (envFile.exists() && envFile.canRead()) {
                    LOGGER.info("Found .env file at: " + location);
                    dotenv = Dotenv.configure()
                        .directory(envFile.getParent())
                        .filename(envFile.getName())
                        .load();
                    apiKey = dotenv.get("NASA_API_KEY");
                    if (apiKey != null && !apiKey.isEmpty()) {
                        return apiKey;
                    }
                }
            }

            // Return the personal fallback API key if no other key is found
            LOGGER.info("Using personal fallback API key");
            return PERSONAL_API_KEY;

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error loading API key", e);
            throw new RuntimeException("Failed to load API key: " + e.getMessage());
        }
    }
    
    public NasaController(NasaModel model, NasaView view) {
        this.model = model;
        this.view = view;
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
        this.apiKey = loadApiKey();
        
        if (!isApiKeyValid()) {
            throw new RuntimeException("Invalid NASA API key");
        }
        
        setupEventListeners();
    }

    private void setupEventListeners() {
        view.addAPODListener(e -> fetchAPOD());
        view.addMarsRoverListener(e -> fetchMarsRoverPhotos());
        view.addNEOListener(e -> fetchNearEarthObjects());
        view.addImageLibraryListener(e -> searchImageLibrary());
        view.addEPICListener(e -> fetchEPICImages());
    }

    private void fetchAPOD() {
        try {
            String url = APOD_URL + "?api_key=" + apiKey;
            
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

            httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenApply(this::parseAPODResponse)
                .thenAccept(view::displayData)
                .exceptionally(e -> {
                    view.displayData("Error fetching APOD: " + e.getMessage());
                    return null;
                });
        } catch (Exception e) {
            view.displayData("Error: " + e.getMessage());
        }
    }

    private String parseAPODResponse(String response) {
        try {
            JsonNode root = objectMapper.readTree(response);
            StringBuilder result = new StringBuilder();

            // Check if we got an error response
            if (root.has("error")) {
                return "API Error: " + root.get("error").get("message").asText();
            }

            // Get URL (required field)
            JsonNode urlNode = root.get("url");
            if (urlNode == null) {
                return "Error: No media URL found in the response";
            }
            String mediaUrl = urlNode.asText();

            // Get title (optional)
            JsonNode titleNode = root.get("title");
            String title = titleNode != null ? titleNode.asText() : "No title available";
            result.append("Title: ").append(title).append("\n\n");

            // Get explanation (optional)
            JsonNode explanationNode = root.get("explanation");
            String explanation = explanationNode != null ? explanationNode.asText() : "No explanation available";
            result.append("Explanation: ").append(explanation);

            // Get media type (optional, default to image)
            JsonNode mediaTypeNode = root.get("media_type");
            String mediaType = mediaTypeNode != null ? mediaTypeNode.asText() : "image";
            
            // Handle different media types
            boolean isVideo = "video".equals(mediaType);
            view.displayMedia(mediaUrl, isVideo);
            
            // Add date if available
            JsonNode dateNode = root.get("date");
            if (dateNode != null) {
                result.append("\n\nDate: ").append(dateNode.asText());
            }

            return result.toString();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error parsing APOD response", e);
            return "Error parsing APOD response: " + e.getMessage() + "\nResponse: " + response;
        }
    }

    private void fetchMarsRoverPhotos() {
        try {
            String url = MARS_ROVER_URL + "?sol=1000&api_key=" + apiKey;
            view.displayData("Fetching Mars Rover photos from: " + url);
            
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

            httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenApply(this::parseMarsRoverResponse)
                .thenAccept(view::displayData)
                .exceptionally(e -> {
                    view.displayData("Error fetching Mars Rover photos: " + e.getMessage());
                    return null;
                });
        } catch (Exception e) {
            view.displayData("Error: " + e.getMessage());
        }
    }

    private String parseMarsRoverResponse(String response) {
        try {
            JsonNode root = objectMapper.readTree(response);
            JsonNode photos = root.get("photos");
            if (photos != null && photos.size() > 0) {
                JsonNode firstPhoto = photos.get(0);
                String imageUrl = firstPhoto.get("img_src").asText();
                
                // Ensure HTTPS usage
                if (imageUrl.startsWith("http://")) {
                    imageUrl = "https://" + imageUrl.substring(7);
                }
                
                view.displayData("Attempting to load image from URL: " + imageUrl);
                
                try {
                    // Encode the URL
                    imageUrl = encodeUrl(imageUrl);
                    view.displayData("Encoded image URL: " + imageUrl);
                    
                    // Configure client to follow redirects
                    HttpClient.Builder clientBuilder = HttpClient.newBuilder()
                        .followRedirects(HttpClient.Redirect.NORMAL);
                    HttpClient redirectClient = clientBuilder.build();
                    
                    // Verify URL is valid
                    HttpRequest imageRequest = HttpRequest.newBuilder()
                        .uri(URI.create(imageUrl))
                        .header("User-Agent", "NASA-App/1.0") // Add User-Agent header
                        .GET()
                        .build();
                    
                    // Test if image is accessible
                    HttpResponse<byte[]> imageResponse = redirectClient.send(imageRequest, HttpResponse.BodyHandlers.ofByteArray());
                    int statusCode = imageResponse.statusCode();
                    view.displayData("Image response status code: " + statusCode);
                    
                    if (statusCode == 200) {
                        view.displayMedia(imageUrl, false);
                        return String.format("Showing photo taken by %s rover on sol %s",
                            firstPhoto.get("rover").get("name").asText(),
                            firstPhoto.get("sol").asText());
                    } else {
                        return String.format("Error: Unable to access image (HTTP %d) - Final URL: %s", 
                            statusCode, 
                            imageResponse.uri().toString());
                    }
                } catch (Exception e) {
                    view.displayData("Error loading image: " + e.getMessage());
                    return "Error loading image: " + e.getMessage();
                }
            }
            return "No photos found in the response";
        } catch (Exception e) {
            return "Error parsing Mars Rover response: " + e.getMessage();
        }
    }

    private void fetchNearEarthObjects() {
        try {
            String url = NEO_URL + "?api_key=" + apiKey;
            
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

            httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenApply(this::parseNEOResponse)
                .thenAccept(view::displayData)
                .exceptionally(e -> {
                    view.displayData("Error fetching NEO data: " + e.getMessage());
                    return null;
                });
        } catch (Exception e) {
            view.displayData("Error: " + e.getMessage());
        }
    }

    private String parseNEOResponse(String response) {
        try {
            JsonNode root = objectMapper.readTree(response);
            
            // Check if we got an error response
            if (root.has("error")) {
                return "API Error: " + root.get("error").get("message").asText();
            }
            
            // Get near earth objects
            JsonNode nearEarthObjects = root.get("near_earth_objects");
            if (nearEarthObjects == null) {
                return "No near earth objects data found in the response";
            }

            StringBuilder result = new StringBuilder("Near Earth Objects:\n\n");
            
            // If no fields, check if it's an array
            if (!nearEarthObjects.isObject()) {
                return "Unexpected NEO data format in the response";
            }

            nearEarthObjects.fields().forEachRemaining(entry -> {
                String date = entry.getKey();
                JsonNode objects = entry.getValue();
                if (objects != null && objects.isArray()) {
                    result.append(String.format("Date: %s - Found %d objects\n", date, objects.size()));
                    
                    // Add details for each object
                    for (int i = 0; i < objects.size(); i++) {
                        JsonNode neo = objects.get(i);
                        String name = neo.get("name") != null ? neo.get("name").asText() : "Unknown";
                        JsonNode diameter = neo.path("estimated_diameter").path("meters").path("estimated_diameter_max");
                        String size = diameter.isMissingNode() ? "Unknown" : String.format("%.2f meters", diameter.asDouble());
                        JsonNode hazardous = neo.get("is_potentially_hazardous_asteroid");
                        boolean isHazardous = hazardous != null && hazardous.asBoolean();
                        
                        result.append(String.format("  - %s (Size: %s)%s\n", 
                            name, 
                            size,
                            isHazardous ? " ⚠️ Potentially Hazardous" : ""));
                    }
                    result.append("\n");
                }
            });
            
            return result.toString();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error parsing NEO response", e);
            return "Error parsing NEO response: " + e.getMessage() + "\nResponse: " + response;
        }
    }

    private void searchImageLibrary() {
        try {
            String query = view.getSearchQuery();
            String url = IMAGE_LIBRARY_URL + "?q=" + query;
            
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

            httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenApply(this::parseImageLibraryResponse)
                .exceptionally(e -> "Error searching NASA Image Library: " + e.getMessage());
        } catch (Exception e) {
            view.displayData("Error: " + e.getMessage());
        }
    }

    private String encodeUrl(String url) {
        try {
            // Split the URL into parts to encode each part separately
            String[] parts = url.split("/");
            for (int i = 3; i < parts.length; i++) { // Start from index 3 to skip protocol and domain
                parts[i] = URLEncoder.encode(parts[i], StandardCharsets.UTF_8.toString())
                    .replace("+", "%20"); // Replace + with %20 for spaces
            }
            return String.join("/", parts);
        } catch (Exception e) {
            return url; // Return original URL if encoding fails
        }
    }

    private String parseImageLibraryResponse(String response) {
        try {
            JsonNode root = objectMapper.readTree(response);
            JsonNode items = root.get("collection").get("items");
            if (items.size() > 0) {
                StringBuilder result = new StringBuilder("Search Results:\n\n");
                for (int i = 0; i < Math.min(5, items.size()); i++) {
                    JsonNode item = items.get(i);
                    JsonNode data = item.get("data").get(0);
                    String title = data.get("title").asText();
                    String mediaType = data.get("media_type").asText();
                    
                    // Get the media URL
                    String mediaUrl = null;
                    if ("video".equals(mediaType)) {
                        JsonNode links = item.get("links");
                        if (links != null && links.size() > 0) {
                            mediaUrl = encodeUrl(links.get(0).get("href").asText());
                        }
                    } else {
                        // For images
                        JsonNode links = item.get("links");
                        if (links != null && links.size() > 0) {
                            mediaUrl = encodeUrl(links.get(0).get("href").asText());
                        }
                    }
                    
                    if (mediaUrl != null) {
                        view.displayMedia(mediaUrl, "video".equals(mediaType));
                        result.append(String.format("%d. %s (%s)\n", i + 1, title, mediaType));
                        break; // Display the first available media
                    }
                }
                return result.toString();
            }
            return "No images found";
        } catch (Exception e) {
            return "Error parsing Image Library response: " + e.getMessage();
        }
    }

    private void fetchEPICImages() {
        try {
            String url = EPIC_URL;
            
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Accept", "application/json")
                .GET()
                .build();

            httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenApply(this::parseEPICResponse)
                .thenAccept(view::displayData)
                .exceptionally(e -> {
                    view.displayData("Error fetching EPIC images: " + e.getMessage());
                    return null;
                });
        } catch (Exception e) {
            view.displayData("Error: " + e.getMessage());
        }
    }

    private String parseEPICResponse(String response) {
        try {
            JsonNode root = objectMapper.readTree(response);
            if (root.size() > 0) {
                JsonNode firstImage = root.get(0);
                String date = firstImage.get("date").asText().split(" ")[0];
                String[] dateParts = date.split("-");
                String imageId = firstImage.get("image").asText();
                
                // Construct the actual image URL using the date components
                String imageUrl = String.format(
                    "https://epic.gsfc.nasa.gov/archive/natural/%s/%s/%s/png/%s.png",
                    dateParts[0],  // year
                    dateParts[1],  // month
                    dateParts[2],  // day
                    imageId
                );
                
                view.displayMedia(imageUrl, false); // EPIC images are always images
                
                return String.format("Showing EPIC image from %s", date);
            }
            return "No EPIC images found";
        } catch (Exception e) {
            return "Error parsing EPIC response: " + e.getMessage();
        }
    }

    public void start() {
        view.setVisible(true);
    }
} 