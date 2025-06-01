# NASA Space Explorer

A desktop application that provides an interactive interface to explore various NASA APIs, including the Astronomy Picture of the Day (APOD), Mars Rover photos, Near Earth Objects (NEO), NASA Image Library, and EPIC Earth images.

## Features

- **Astronomy Picture of the Day (APOD)**: View NASA's daily featured space photograph or video with detailed explanations
- **Mars Rover Photos**: Browse through photos taken by various Mars rovers (Curiosity, Perseverance, Opportunity, Spirit)
- **Near Earth Objects (NEO)**: Track asteroids and comets that pass near Earth
- **NASA Image Library**: Search through NASA's vast collection of space-related images and videos
- **EPIC Earth Images**: View recent photographs of Earth taken by DSCOVR's Earth Polychromatic Imaging Camera

## Prerequisites

- Java 17 or higher
- Maven 3.6 or higher
- NASA API Key (obtain from [NASA API Portal](https://api.nasa.gov/))

## Installation

1. Clone the repository:
   ```bash
   git clone https://github.com/yourusername/nasa-explorer.git
   cd nasa-explorer
   ```

2. Create a `.env` file in one of the following locations:
   - Current directory
   - User's home directory
   - Application directory
   - ./app/ directory

   Add your NASA API key:
   ```
   NASA_API_KEY=your_api_key_here
   ```

3. Build the application:
   ```bash
   mvn clean package
   ```

4. Run the application:
   ```bash
   java -jar target/nasa-explorer-1.0-SNAPSHOT.jar
   ```

## Building the DMG Installer (macOS)

To create a DMG installer for macOS:

```bash
mvn clean package
mvn jpackage:jpackage
```

The installer will be available in the `target/dist` directory.

## Application Structure

- **NasaController**: Handles API requests and business logic
- **NasaView**: Manages the GUI interface
- **NasaModel**: Maintains application state and data

## Logging

The application logs are stored in:
```
~/Library/Logs/NasaExplorer/nasa_explorer.log
```

## Error Handling

- The application will display user-friendly error messages for common issues
- Detailed error logs are available in the log file
- API key validation is performed at startup

## Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Acknowledgments

- NASA APIs: https://api.nasa.gov/
- Icons and resources from NASA public domain content
- Built with Java Swing and Maven 