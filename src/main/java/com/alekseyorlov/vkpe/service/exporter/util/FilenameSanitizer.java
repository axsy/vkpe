package com.alekseyorlov.vkpe.service.exporter.util;

public final class FilenameSanitizer {
    public static String sanitize(String filename) {
        return filename
                .replaceAll("[^\\p{L}\\d-_]", "_") // remove invalid characters,
                .replaceAll("_{2,}", "_")          // shrink replacement symbols,
                .replaceAll("^_|_$", "");          // remove them at start and at end of string.
    }
}
