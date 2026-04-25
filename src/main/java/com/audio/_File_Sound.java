package com.audio;
import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
public class _File_Sound {
Logger logger = Logger.getLogger(_File_Sound.class.getName());
    // ─── File attributes 
    private final File file;
    private final String fileName;
    private final String filePath;
    private final long fileSizeBytes;

    //  Audio attributes 
    private final AudioFormat audioFormat;
    private final float sampleRate;
    private final int bitDepth;
    private final int channels;
    private final String encoding;
    private final double durationSeconds;

    //  NEW: detected format from magic bytes 
    private final AudioFormatDetector.AudioFormat detectedFormat;

    //  Constructor 
    public _File_Sound(File file)
            throws UnsupportedAudioFileException, IOException {

        if (file == null)    throw new IllegalArgumentException("File must not be null.");
        if (!file.exists())  throw new IllegalArgumentException("File does not exist: " + file.getAbsolutePath());
        if (!file.isFile())  throw new IllegalArgumentException("Path does not point to a file: " + file.getAbsolutePath());
        if (!file.canRead()) throw new IllegalArgumentException("File is not readable: " + file.getAbsolutePath());

        this.file          = file;
        this.fileName      = file.getName();
        this.filePath      = file.getPath();
        this.fileSizeBytes = file.length();

        //  NEW: detect true format from header before opening stream 
        this.detectedFormat = AudioFormatDetector.detect(file);

        //  Extract audio metadata 
        try (AudioInputStream audioStream = AudioSystem.getAudioInputStream(file)) {
            this.audioFormat     = audioStream.getFormat();
            this.sampleRate      = audioFormat.getSampleRate();
            this.bitDepth        = audioFormat.getSampleSizeInBits();
            this.channels        = audioFormat.getChannels();
            this.encoding        = audioFormat.getEncoding().toString();

            long frameLength     = audioStream.getFrameLength();
            float frameRate      = audioFormat.getFrameRate();
            this.durationSeconds = frameLength / frameRate;
        }
    }

    //  Convenience factory 
    public static _File_Sound fromPath(String path)
            throws UnsupportedAudioFileException, IOException {
        if (path == null || path.isBlank())
            throw new IllegalArgumentException("Path must not be null or blank.");
        return new _File_Sound(new File(path));
    }

    //  Discrete getters (file) 
    public File   getFile()          { return file; }
    public String getFileName()      { return fileName; }
    public String getFilePath()      { return filePath; }
    public long   getFileSizeBytes() { return fileSizeBytes; }

    public double getFileSizeKB() {
        return Math.round((fileSizeBytes / 1024.0) * 100.0) / 100.0;
    }

    //  Discrete getters (audio) 
    public AudioFormat getAudioFormat()   { return audioFormat; }
    public float       getSampleRate()    { return sampleRate; }
    public int         getBitDepth()      { return bitDepth; }
    public int         getChannels()      { return channels; }
    public String      getEncoding()      { return encoding; }
    public double      getDurationSeconds() { return durationSeconds; }

    //  NEW: detected format getters 
    public AudioFormatDetector.AudioFormat getDetectedFormat() { return detectedFormat; }

    public boolean isWav()  { return detectedFormat == AudioFormatDetector.AudioFormat.WAV;  }
    public boolean isMp3()  { return detectedFormat == AudioFormatDetector.AudioFormat.MP3;  }
    public boolean isFlac() { return detectedFormat == AudioFormatDetector.AudioFormat.FLAC; }
    public boolean isOgg()  { return detectedFormat == AudioFormatDetector.AudioFormat.OGG;  }
    public boolean isAiff() { return detectedFormat == AudioFormatDetector.AudioFormat.AIFF; }
    public boolean isAac()  { return detectedFormat == AudioFormatDetector.AudioFormat.AAC;  }

    public String getChannelLabel() {
        switch (channels) {
            case 1:
                return "Mono";
            case 2:
                return "Stereo";
            default:
                return channels + "-channel";
        }
    }

    //  Aggregated info 
    public String getInfo() {
        String duration = durationSeconds >= 0
            ? String.format("%.2f seconds", durationSeconds)
            : "Unknown";

        return String.format(
            "╔══════════════════════════════════════╗\n" +
            "║           _File_Sound Info             ║\n" +
            "╠══════════════════════════════════════╣\n" +
            "║ File Name    : %s\n" +
            "║ File Path    : %s\n" +
            "║ File Size    : %.2f KB\n" +
            "╠══════════════════════════════════════╣\n" +
            "║ True Format  : %s\n" +
            "║ Is WAV       : %s\n" +
            "║ Encoding     : %s\n" +
            "╠═══════════════════════════════════\n" +
            "║ Sample Rate  : %.1f Hz\n" +
            "║ Bit Depth    : %d bit\n" +
            "║ Channels     : %s\n" +
            "║ Duration     : %s\n" +
            "╚══════════════════════════════════════╝",
            fileName,
            filePath,
            getFileSizeKB(),
            detectedFormat,
            isWav() ? "✓ Yes" : "✗ No",
            encoding,
            sampleRate,
            bitDepth,
            getChannelLabel(),
            duration
        );
    }

    //  toString 
    @Override
    public String toString() {
        return String.format("_File_Sound[name=%s, trueFormat=%s, %.1fHz, %s]",
            fileName, detectedFormat, sampleRate, getChannelLabel());
    }
}