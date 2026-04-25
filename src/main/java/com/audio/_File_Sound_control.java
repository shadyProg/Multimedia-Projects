package com.audio;
import java.io.File;
import java.io.IOException;

import javax.sound.sampled.UnsupportedAudioFileException;

/**
 * Controls comparison between two FileSound objects.
 * Produces a structured diff report with deltas for mismatches
 * and checkmarks for matches.
 */
public class _File_Sound_control {

    // ─── Fields ───────────────────────────────────────────────────────────────
    private final _File_Sound soundA;
    private final _File_Sound soundB;

    // ─── Constructor ──────────────────────────────────────────────────────────

    /**
     * @param soundA first FileSound — must not be null
     * @param soundB second FileSound — must not be null
     * @throws IllegalArgumentException if either sound is null
     */
    public _File_Sound_control(_File_Sound soundA, _File_Sound soundB) {
        if (soundA == null) throw new IllegalArgumentException("soundA must not be null.");
        if (soundB == null) throw new IllegalArgumentException("soundB must not be null.");
        this.soundA = soundA;
        this.soundB = soundB;
    }

    // ─── Convenience factory from file paths ──────────────────────────────────

    /**
     * Build a FileSoundControl directly from two file paths.
     */
    public static _File_Sound_control fromPaths(String pathA, String pathB)
            throws UnsupportedAudioFileException, IOException {
        return new _File_Sound_control(
            _File_Sound.fromPath(pathA),
            _File_Sound.fromPath(pathB)
        );
    }

    public static _File_Sound_control fromFiles(File fileA, File fileB)
            throws UnsupportedAudioFileException, IOException {
        return new _File_Sound_control(
            new _File_Sound(fileA),
            new _File_Sound(fileB)
        );
    }

    // ─── Core diff engine ─────────────────────────────────────────────────────

    /**
     * Runs the full diff and returns a structured DiffReport.
     */
    public DiffReport compare() {
        return new DiffReport(soundA, soundB);
    }

    // ─── DiffReport inner class ───────────────────────────────────────────────

    /**
     * Holds the full breakdown of differences between two FileSound objects.
     * Each attribute has its own DiffEntry — matched or mismatched with delta.
     */
    public static class DiffReport {

        public final DiffEntry<Float>   sampleRate;
        public final DiffEntry<Integer> bitDepth;
        public final DiffEntry<Integer> channels;
        public final DiffEntry<String>  encoding;
        public final DiffEntry<Double>  duration;
        public final DiffEntry<Long>    fileSize;

        // overall match: true only if every attribute matches
        public final boolean fullyMatches;

        private DiffReport(_File_Sound a, _File_Sound b) {
            this.sampleRate = DiffEntry.ofFloat  ("Sample Rate", "Hz",  a.getSampleRate(),      b.getSampleRate());
            this.bitDepth   = DiffEntry.ofInt    ("Bit Depth",   "bit", a.getBitDepth(),         b.getBitDepth());
            this.channels   = DiffEntry.ofInt    ("Channels",    "",    a.getChannels(),          b.getChannels());
            this.encoding   = DiffEntry.ofString ("Encoding",           a.getEncoding(),          b.getEncoding());
            this.duration   = DiffEntry.ofDouble ("Duration",    "s",   a.getDurationSeconds(),  b.getDurationSeconds());
            this.fileSize   = DiffEntry.ofLong   ("File Size",   "KB",  a.getFileSizeBytes(),    b.getFileSizeBytes());

            this.fullyMatches = sampleRate.matches && bitDepth.matches
                            && channels.matches   && encoding.matches
                            && duration.matches   && fileSize.matches;
        }

        /**
         * Returns the full formatted diff report as a printable string.
         */
        public String getSummary() {
            StringBuilder sb = new StringBuilder();
            sb.append("╔____________________________________╗\n");
            sb.append("|              FileSound Diff Report                  |\n");
            sb.append("╠____________________________________╣\n");
            sb.append(sampleRate.format()).append("\n");
            sb.append(bitDepth  .format()).append("\n");
            sb.append(channels  .format()).append("\n");
            sb.append(encoding  .format()).append("\n");
            sb.append(duration  .format()).append("\n");
            sb.append(fileSize  .format()).append("\n");
            sb.append("╠____________________________________╣\n");
            sb.append(fullyMatches
                ? "|  Result : T Files are identical in all attributes    |\n"
                : "|  Result : F Files differ in one or more attributes   |\n");
            sb.append("╚____________________________________╝\n");
            return sb.toString();
        }
    }

    // ─── DiffEntry generic ────────────────────────────────────────────────────

    /**
     * Represents the comparison result for a single attribute.
     * T must be comparable and support subtraction (handled per type via factories).
     */
    public static class DiffEntry<T> {

        public final String  label;
        public final T       valueA;
        public final T       valueB;
        public final boolean matches;
        public final String  deltaStr;   // human-readable delta, or "" if matched
        private final String unit;

        private DiffEntry(String label, String unit, T valueA, T valueB,
                        boolean matches, String deltaStr) {
            this.label    = label;
            this.unit     = unit;
            this.valueA   = valueA;
            this.valueB   = valueB;
            this.matches  = matches;
            this.deltaStr = deltaStr;
        }

        // ── Type-specific factories ──────────────────────────────────────────

        public static DiffEntry<Float> ofFloat(String label, String unit, float a, float b) {
            boolean match = Float.compare(a, b) == 0;
            String delta  = match ? "" : String.format("(Δ %.1f)", Math.abs(a - b));
            return new DiffEntry<>(label, unit, a, b, match, delta);
        }

        public static DiffEntry<Integer> ofInt(String label, String unit, int a, int b) {
            boolean match = a == b;
            String delta  = match ? "" : String.format("(Δ %d)", Math.abs(a - b));
            return new DiffEntry<>(label, unit, a, b, match, delta);
        }

        public static DiffEntry<Double> ofDouble(String label, String unit, double a, double b) {
            boolean match = Double.compare(a, b) == 0;
            String delta  = match ? "" : String.format("(Δ %.2f)", Math.abs(a - b));
            return new DiffEntry<>(label, unit, a, b, match, delta);
        }

        public static DiffEntry<Long> ofLong(String label, String unit, long a, long b) {
            boolean match = a == b;
            String delta  = match ? "" : String.format("(Δ %d)", Math.abs(a - b));
            return new DiffEntry<>(label, unit, a, b, match, delta);
        }

        public static DiffEntry<String> ofString(String label, String a, String b) {
            boolean match = a.equalsIgnoreCase(b);
            return new DiffEntry<>(label, "", a, b, match, "");
        }

        // ── Format one line of the report ────────────────────────────────────

        public String format() {
            if (matches) {
                return String.format("|  %-12s: %s %s  💚 (match)",
                    label, valueA, unit).stripTrailing();
            } else {
                return String.format("|  %-12s: %s %s  🟥  %s %s  %s",
                    label, valueA, unit, valueB, unit, deltaStr).stripTrailing();
            }
        }
    }

    // ─── toString ─────────────────────────────────────────────────────────────

    @Override
    public String toString() {
        return String.format("FileSoundControl[A=%s | B=%s]", soundA, soundB);
    }
}