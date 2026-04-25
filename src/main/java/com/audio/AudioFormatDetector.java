package com.audio;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Utility class for detecting true audio file formats
 * by reading magic bytes from the file header.
 */
public class AudioFormatDetector {

    // ─── Magic byte signatures ─────────────────────────────────────────────
    private static final byte[] MAGIC_WAV  = {0x52, 0x49, 0x46, 0x46}; // "RIFF"
    private static final byte[] MAGIC_MP3  = {(byte)0xFF, (byte)0xFB}; // MPEG sync
    private static final byte[] MAGIC_MP3_ID3 = {0x49, 0x44, 0x33};    // "ID3"
    private static final byte[] MAGIC_FLAC = {0x66, 0x4C, 0x61, 0x43}; // "fLaC"
    private static final byte[] MAGIC_OGG  = {0x4F, 0x67, 0x67, 0x53}; // "OggS"
    private static final byte[] MAGIC_AIFF = {0x46, 0x4F, 0x52, 0x4D}; // "FORM"
    private static final byte[] MAGIC_AAC  = {(byte)0xFF, (byte)0xF1}; // ADTS AAC

    // ─── Detected format enum ──────────────────────────────────────────────
    public enum AudioFormat {
        WAV, MP3, FLAC, OGG, AIFF, AAC, UNKNOWN
    }

    // ─── Private constructor — utility class, no instantiation ────────────
    private AudioFormatDetector() {}

    // ─── Core detection method ────────────────────────────────────────────

    /**
     * Detects the true audio format by reading the file header.
     * Does NOT rely on file extension.
     *
     * @param file the audio file to inspect
     * @return detected AudioFormat enum value, or UNKNOWN if unrecognized
     * @throws IllegalArgumentException if file is null, missing, or unreadable
     * @throws IOException if the file cannot be read
     */
    public static AudioFormat detect(File file) throws IOException {
        validateFile(file);

        // Read first 4 bytes — enough for all signatures
        byte[] header = readHeader(file, 4);

        if (matches(header, MAGIC_WAV))      return AudioFormat.WAV;
        if (matches(header, MAGIC_FLAC))     return AudioFormat.FLAC;
        if (matches(header, MAGIC_OGG))      return AudioFormat.OGG;
        if (matches(header, MAGIC_AIFF))     return AudioFormat.AIFF;
        if (matches(header, MAGIC_MP3_ID3))  return AudioFormat.MP3;  // ID3 tag
        if (matches(header, MAGIC_MP3))      return AudioFormat.MP3;  // raw MPEG
        if (matches(header, MAGIC_AAC))      return AudioFormat.AAC;

        return AudioFormat.UNKNOWN;
    }

    // ─── Convenience check methods ────────────────────────────────────────

    /** Returns true only if the file is a genuine WAV file. */
    public static boolean isWav(File file) throws IOException {
        return detect(file) == AudioFormat.WAV;
    }

    public static boolean isMp3(File file) throws IOException {
        return detect(file) == AudioFormat.MP3;
    }

    public static boolean isFlac(File file) throws IOException {
        return detect(file) == AudioFormat.FLAC;
    }

    public static boolean isOgg(File file) throws IOException {
        return detect(file) == AudioFormat.OGG;
    }

    public static boolean isAiff(File file) throws IOException {
        return detect(file) == AudioFormat.AIFF;
    }

    public static boolean isAac(File file) throws IOException {
        return detect(file) == AudioFormat.AAC;
    }

    /** Returns true if format is recognized (not UNKNOWN). */
    public static boolean isSupportedAudio(File file) throws IOException {
        return detect(file) != AudioFormat.UNKNOWN;
    }

    // ─── Internal helpers ─────────────────────────────────────────────────

    /**
     * Reads the first N bytes from the file header.
     */
    private static byte[] readHeader(File file, int byteCount) throws IOException {
        byte[] buffer = new byte[byteCount];
        try (FileInputStream fis = new FileInputStream(file)) {
            int bytesRead = fis.read(buffer);
            if (bytesRead < byteCount) {
                throw new IOException(
                    "File too small to read header: " + file.getName()
                    + " (read " + bytesRead + " of " + byteCount + " bytes)"
                );
            }
        }
        return buffer;
    }

    /**
     * Checks if the header starts with the given magic byte sequence.
     */
    private static boolean matches(byte[] header, byte[] magic) {
        if (header.length < magic.length) return false;
        for (int i = 0; i < magic.length; i++) {
            if (header[i] != magic[i]) return false;
        }
        return true;
    }

    /**
     * Validates the file before any read operation.
     */
    private static void validateFile(File file) {
        if (file == null)        throw new IllegalArgumentException("File must not be null.");
        if (!file.exists())      throw new IllegalArgumentException("File does not exist: " + file.getAbsolutePath());
        if (!file.isFile())      throw new IllegalArgumentException("Path is not a file: " + file.getAbsolutePath());
        if (!file.canRead())     throw new IllegalArgumentException("File is not readable: " + file.getAbsolutePath());
    }
}