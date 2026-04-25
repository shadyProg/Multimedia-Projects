# AudioProject

AudioProject is a Java-based audio utility project that provides tools for loading, detecting, and controlling audio playback.

## Overview

- **Language:** Java
- **Build tool:** Maven
- **Target runtime:** Java SE 21

## Key components

- `App.java` — application entry point
- `AudioFormatDetector.java` — detects audio formats and helps identify supported sound files
- `_File_Sound.java` & `_File_Sound_control.java` — handles audio file management and playback control

## Running the project

1. Build the project:
   ```bash
   mvn package
   ```
2. Run the main class with Java:
   ```bash
   java -cp target/classes com.audio.App
   ```

## Testing

- Unit tests are located at `src/test/java/com/audio/AppTest.java`

## Notes

- Compiled output is generated in `target/classes/com/audio/`
- Further improvements can include extended playback features, a CLI, or GUI controls.

