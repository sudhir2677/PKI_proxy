package org.example.utils;

public enum FileSeparator {
    WINDOWS("\\"),
    MAC("/");

    private String separator;

    FileSeparator(String separator){
        this.separator = separator;
    }

    public String getSeparator() {
        return separator;
    }

    // A method to get the separator based on the OS
    public static FileSeparator getOSSeparator() {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) {
            return WINDOWS;
        } else if (os.contains("mac") || os.contains("nix") || os.contains("nux")) {
            return MAC;
        }
        throw new UnsupportedOperationException("Unsupported OS: " + os);
    }
}
