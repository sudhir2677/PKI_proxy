package org.example.utils;

public class PathUtil {

    // Method to get the absolute path of the current working directory
    public static String getCurrentFilePath() {
        // Get the OS-specific file separator
        String separator = FileSeparator.getOSSeparator().getSeparator();

        // Get the current working directory
        String currentDir = System.getProperty("user.dir");

        // You can concatenate the path with file names if needed (e.g., add a file name)
        // For this example, we are just returning the current directory path.
        return currentDir + separator + "src" + separator + "main" + separator + "java" + separator + "org" + separator + "example" + separator;
    }

    // Method to get the path of a specific file from a directory
    public static String getFilePathFromCurrentDir(String fileName) {
        String separator = FileSeparator.getOSSeparator().getSeparator();
        String currentDir = System.getProperty("user.dir");
        return currentDir + separator + fileName;
    }
}
