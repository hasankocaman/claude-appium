package com.hepsiburada.utils;

import com.hepsiburada.config.ConfigurationManager;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Stream;

/**
 * File Management Utilities class for handling file and directory operations
 * Provides comprehensive file management functionality with thread-safe operations
 * 
 * @author Hepsiburada Test Automation Team
 */
public final class FileUtils {
    
    private static final Logger logger = LogManager.getLogger(FileUtils.class);
    private static final DateTimeFormatter TIMESTAMP_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
    private static final ReentrantLock FILE_OPERATION_LOCK = new ReentrantLock();
    
    // Directory constants
    private static final String SCREENSHOTS_DIR = "screenshots";
    private static final String VIDEOS_DIR = "videos";
    private static final String REPORTS_DIR = "reports";
    private static final String LOGS_DIR = "logs";
    private static final String TEST_DATA_DIR = "test-data";
    private static final String TEMP_DIR = "temp";
    
    private FileUtils() {
        // Utility class should not be instantiated
    }
    
    /**
     * Create all necessary directories for the test execution
     * Creates directories for screenshots, videos, reports, logs, test data, and temp files
     */
    public static void createDirectories() {
        logger.info("Creating necessary directories for test execution");
        
        try {
            createDirectory(getScreenshotsDirectory());
            createDirectory(getVideosDirectory());
            createDirectory(getReportsDirectory());
            createDirectory(getLogsDirectory());
            createDirectory(getTestDataDirectory());
            createDirectory(getTempDirectory());
            
            // Create subdirectories for reports
            createDirectory(getReportsDirectory() + File.separator + "extent");
            createDirectory(getReportsDirectory() + File.separator + "allure-results");
            createDirectory(getReportsDirectory() + File.separator + "cucumber");
            
            logger.info("All necessary directories created successfully");
            
        } catch (Exception e) {
            logger.error("Failed to create necessary directories", e);
            throw new RuntimeException("Failed to create directories", e);
        }
    }
    
    /**
     * Create a single directory if it doesn't exist
     * @param directoryPath Path of the directory to create
     * @return true if directory exists or was created successfully
     */
    public static boolean createDirectory(String directoryPath) {
        if (directoryPath == null || directoryPath.trim().isEmpty()) {
            logger.error("Directory path cannot be null or empty");
            return false;
        }
        
        FILE_OPERATION_LOCK.lock();
        try {
            Path path = Paths.get(directoryPath);
            
            if (Files.exists(path)) {
                logger.debug("Directory already exists: {}", directoryPath);
                return true;
            }
            
            Files.createDirectories(path);
            logger.info("Directory created successfully: {}", directoryPath);
            return true;
            
        } catch (Exception e) {
            logger.error("Failed to create directory: {}", directoryPath, e);
            return false;
        } finally {
            FILE_OPERATION_LOCK.unlock();
        }
    }
    
    /**
     * Delete a file if it exists
     * @param filePath Path of the file to delete
     * @return true if file was deleted or doesn't exist
     */
    public static boolean deleteFile(String filePath) {
        if (filePath == null || filePath.trim().isEmpty()) {
            logger.error("File path cannot be null or empty");
            return false;
        }
        
        FILE_OPERATION_LOCK.lock();
        try {
            Path path = Paths.get(filePath);
            
            if (!Files.exists(path)) {
                logger.debug("File doesn't exist: {}", filePath);
                return true;
            }
            
            Files.delete(path);
            logger.info("File deleted successfully: {}", filePath);
            return true;
            
        } catch (Exception e) {
            logger.error("Failed to delete file: {}", filePath, e);
            return false;
        } finally {
            FILE_OPERATION_LOCK.unlock();
        }
    }
    
    /**
     * Delete a directory and all its contents
     * @param directoryPath Path of the directory to delete
     * @return true if directory was deleted successfully
     */
    public static boolean deleteDirectory(String directoryPath) {
        if (directoryPath == null || directoryPath.trim().isEmpty()) {
            logger.error("Directory path cannot be null or empty");
            return false;
        }
        
        FILE_OPERATION_LOCK.lock();
        try {
            Path path = Paths.get(directoryPath);
            
            if (!Files.exists(path)) {
                logger.debug("Directory doesn't exist: {}", directoryPath);
                return true;
            }
            
            Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    Files.delete(file);
                    return FileVisitResult.CONTINUE;
                }
                
                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    Files.delete(dir);
                    return FileVisitResult.CONTINUE;
                }
            });
            
            logger.info("Directory deleted successfully: {}", directoryPath);
            return true;
            
        } catch (Exception e) {
            logger.error("Failed to delete directory: {}", directoryPath, e);
            return false;
        } finally {
            FILE_OPERATION_LOCK.unlock();
        }
    }
    
    /**
     * Read file content as string
     * @param filePath Path of the file to read
     * @return File content as string
     */
    public static String readFileAsString(String filePath) {
        if (filePath == null || filePath.trim().isEmpty()) {
            logger.error("File path cannot be null or empty");
            return "";
        }
        
        try {
            Path path = Paths.get(filePath);
            
            if (!Files.exists(path)) {
                logger.error("File doesn't exist: {}", filePath);
                return "";
            }
            
            String content = Files.readString(path, StandardCharsets.UTF_8);
            logger.debug("File read successfully: {} (length: {})", filePath, content.length());
            return content;
            
        } catch (Exception e) {
            logger.error("Failed to read file: {}", filePath, e);
            return "";
        }
    }
    
    /**
     * Read file content as byte array
     * @param filePath Path of the file to read
     * @return File content as byte array
     */
    public static byte[] readFileAsBytes(String filePath) {
        if (filePath == null || filePath.trim().isEmpty()) {
            logger.error("File path cannot be null or empty");
            return new byte[0];
        }
        
        try {
            Path path = Paths.get(filePath);
            
            if (!Files.exists(path)) {
                logger.error("File doesn't exist: {}", filePath);
                return new byte[0];
            }
            
            byte[] bytes = Files.readAllBytes(path);
            logger.debug("File read successfully as bytes: {} (size: {} bytes)", filePath, bytes.length);
            return bytes;
            
        } catch (Exception e) {
            logger.error("Failed to read file as bytes: {}", filePath, e);
            return new byte[0];
        }
    }
    
    /**
     * Write string content to file
     * @param filePath Path of the file to write
     * @param content Content to write
     * @return true if file was written successfully
     */
    public static boolean writeStringToFile(String filePath, String content) {
        if (filePath == null || filePath.trim().isEmpty()) {
            logger.error("File path cannot be null or empty");
            return false;
        }
        
        if (content == null) {
            content = "";
        }
        
        FILE_OPERATION_LOCK.lock();
        try {
            Path path = Paths.get(filePath);
            
            // Create parent directories if they don't exist
            createDirectory(path.getParent().toString());
            
            Files.write(path, content.getBytes(StandardCharsets.UTF_8), 
                       StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            
            logger.info("String content written successfully to file: {} (length: {})", filePath, content.length());
            return true;
            
        } catch (Exception e) {
            logger.error("Failed to write string to file: {}", filePath, e);
            return false;
        } finally {
            FILE_OPERATION_LOCK.unlock();
        }
    }
    
    /**
     * Write byte array to file
     * @param filePath Path of the file to write
     * @param bytes Byte array to write
     * @return true if file was written successfully
     */
    public static boolean writeBytesToFile(String filePath, byte[] bytes) {
        if (filePath == null || filePath.trim().isEmpty()) {
            logger.error("File path cannot be null or empty");
            return false;
        }
        
        if (bytes == null) {
            bytes = new byte[0];
        }
        
        FILE_OPERATION_LOCK.lock();
        try {
            Path path = Paths.get(filePath);
            
            // Create parent directories if they don't exist
            createDirectory(path.getParent().toString());
            
            Files.write(path, bytes, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            
            logger.info("Byte content written successfully to file: {} (size: {} bytes)", filePath, bytes.length);
            return true;
            
        } catch (Exception e) {
            logger.error("Failed to write bytes to file: {}", filePath, e);
            return false;
        } finally {
            FILE_OPERATION_LOCK.unlock();
        }
    }
    
    /**
     * Append string content to file
     * @param filePath Path of the file to append to
     * @param content Content to append
     * @return true if content was appended successfully
     */
    public static boolean appendStringToFile(String filePath, String content) {
        if (filePath == null || filePath.trim().isEmpty()) {
            logger.error("File path cannot be null or empty");
            return false;
        }
        
        if (content == null) {
            content = "";
        }
        
        FILE_OPERATION_LOCK.lock();
        try {
            Path path = Paths.get(filePath);
            
            // Create parent directories if they don't exist
            createDirectory(path.getParent().toString());
            
            Files.write(path, content.getBytes(StandardCharsets.UTF_8), 
                       StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            
            logger.debug("String content appended successfully to file: {} (length: {})", filePath, content.length());
            return true;
            
        } catch (Exception e) {
            logger.error("Failed to append string to file: {}", filePath, e);
            return false;
        } finally {
            FILE_OPERATION_LOCK.unlock();
        }
    }
    
    /**
     * Copy file from source to destination
     * @param sourcePath Source file path
     * @param destinationPath Destination file path
     * @return true if file was copied successfully
     */
    public static boolean copyFile(String sourcePath, String destinationPath) {
        if (sourcePath == null || sourcePath.trim().isEmpty() ||
            destinationPath == null || destinationPath.trim().isEmpty()) {
            logger.error("Source and destination paths cannot be null or empty");
            return false;
        }
        
        FILE_OPERATION_LOCK.lock();
        try {
            Path source = Paths.get(sourcePath);
            Path destination = Paths.get(destinationPath);
            
            if (!Files.exists(source)) {
                logger.error("Source file doesn't exist: {}", sourcePath);
                return false;
            }
            
            // Create destination directory if it doesn't exist
            createDirectory(destination.getParent().toString());
            
            Files.copy(source, destination, StandardCopyOption.REPLACE_EXISTING);
            
            logger.info("File copied successfully from {} to {}", sourcePath, destinationPath);
            return true;
            
        } catch (Exception e) {
            logger.error("Failed to copy file from {} to {}", sourcePath, destinationPath, e);
            return false;
        } finally {
            FILE_OPERATION_LOCK.unlock();
        }
    }
    
    /**
     * Move/rename file from source to destination
     * @param sourcePath Source file path
     * @param destinationPath Destination file path
     * @return true if file was moved successfully
     */
    public static boolean moveFile(String sourcePath, String destinationPath) {
        if (sourcePath == null || sourcePath.trim().isEmpty() ||
            destinationPath == null || destinationPath.trim().isEmpty()) {
            logger.error("Source and destination paths cannot be null or empty");
            return false;
        }
        
        FILE_OPERATION_LOCK.lock();
        try {
            Path source = Paths.get(sourcePath);
            Path destination = Paths.get(destinationPath);
            
            if (!Files.exists(source)) {
                logger.error("Source file doesn't exist: {}", sourcePath);
                return false;
            }
            
            // Create destination directory if it doesn't exist
            createDirectory(destination.getParent().toString());
            
            Files.move(source, destination, StandardCopyOption.REPLACE_EXISTING);
            
            logger.info("File moved successfully from {} to {}", sourcePath, destinationPath);
            return true;
            
        } catch (Exception e) {
            logger.error("Failed to move file from {} to {}", sourcePath, destinationPath, e);
            return false;
        } finally {
            FILE_OPERATION_LOCK.unlock();
        }
    }
    
    /**
     * Check if file exists
     * @param filePath Path of the file to check
     * @return true if file exists
     */
    public static boolean fileExists(String filePath) {
        if (filePath == null || filePath.trim().isEmpty()) {
            return false;
        }
        
        try {
            return Files.exists(Paths.get(filePath));
        } catch (Exception e) {
            logger.error("Error checking if file exists: {}", filePath, e);
            return false;
        }
    }
    
    /**
     * Check if directory exists
     * @param directoryPath Path of the directory to check
     * @return true if directory exists
     */
    public static boolean directoryExists(String directoryPath) {
        if (directoryPath == null || directoryPath.trim().isEmpty()) {
            return false;
        }
        
        try {
            Path path = Paths.get(directoryPath);
            return Files.exists(path) && Files.isDirectory(path);
        } catch (Exception e) {
            logger.error("Error checking if directory exists: {}", directoryPath, e);
            return false;
        }
    }
    
    /**
     * Get file size in bytes
     * @param filePath Path of the file
     * @return File size in bytes, -1 if file doesn't exist
     */
    public static long getFileSize(String filePath) {
        if (filePath == null || filePath.trim().isEmpty()) {
            return -1;
        }
        
        try {
            Path path = Paths.get(filePath);
            if (!Files.exists(path)) {
                return -1;
            }
            return Files.size(path);
        } catch (Exception e) {
            logger.error("Error getting file size: {}", filePath, e);
            return -1;
        }
    }
    
    /**
     * List all files in directory with specified extension
     * @param directoryPath Directory path to search
     * @param extension File extension (without dot)
     * @return List of file paths
     */
    public static List<String> listFilesWithExtension(String directoryPath, String extension) {
        List<String> files = new ArrayList<>();
        
        if (directoryPath == null || directoryPath.trim().isEmpty() || 
            extension == null || extension.trim().isEmpty()) {
            logger.error("Directory path and extension cannot be null or empty");
            return files;
        }
        
        try {
            Path dir = Paths.get(directoryPath);
            
            if (!Files.exists(dir) || !Files.isDirectory(dir)) {
                logger.debug("Directory doesn't exist: {}", directoryPath);
                return files;
            }
            
            try (Stream<Path> stream = Files.walk(dir)) {
                stream.filter(Files::isRegularFile)
                     .filter(path -> path.toString().toLowerCase().endsWith("." + extension.toLowerCase()))
                     .forEach(path -> files.add(path.toString()));
            }
            
            logger.debug("Found {} files with extension '{}' in directory: {}", files.size(), extension, directoryPath);
            
        } catch (Exception e) {
            logger.error("Error listing files in directory: {} with extension: {}", directoryPath, extension, e);
        }
        
        return files;
    }
    
    /**
     * Clean up files older than specified days
     * @param directoryPath Directory to clean
     * @param daysOld Files older than this many days will be deleted
     * @return Number of files deleted
     */
    public static int cleanupOldFiles(String directoryPath, int daysOld) {
        if (directoryPath == null || directoryPath.trim().isEmpty() || daysOld < 0) {
            logger.error("Invalid parameters for cleanup: directory={}, daysOld={}", directoryPath, daysOld);
            return 0;
        }
        
        logger.info("Cleaning up files older than {} days in directory: {}", daysOld, directoryPath);
        
        int deletedCount = 0;
        
        try {
            Path dir = Paths.get(directoryPath);
            
            if (!Files.exists(dir) || !Files.isDirectory(dir)) {
                logger.debug("Directory doesn't exist for cleanup: {}", directoryPath);
                return 0;
            }
            
            long cutoffTime = System.currentTimeMillis() - (daysOld * 24L * 60L * 60L * 1000L);
            
            try (Stream<Path> stream = Files.walk(dir)) {
                deletedCount = (int) stream.filter(Files::isRegularFile)
                                          .filter(path -> {
                                              try {
                                                  return Files.getLastModifiedTime(path).toMillis() < cutoffTime;
                                              } catch (IOException e) {
                                                  logger.error("Error getting last modified time for: {}", path, e);
                                                  return false;
                                              }
                                          })
                                          .mapToInt(path -> {
                                              try {
                                                  Files.delete(path);
                                                  logger.debug("Deleted old file: {}", path);
                                                  return 1;
                                              } catch (IOException e) {
                                                  logger.error("Failed to delete old file: {}", path, e);
                                                  return 0;
                                              }
                                          })
                                          .sum();
            }
            
            logger.info("Cleanup completed - deleted {} old files from: {}", deletedCount, directoryPath);
            
        } catch (Exception e) {
            logger.error("Error during cleanup of directory: {}", directoryPath, e);
        }
        
        return deletedCount;
    }
    
    /**
     * Clean up previous test runs by deleting old files
     */
    public static void cleanupPreviousRuns() {
        logger.info("Cleaning up previous test run artifacts");
        
        try {
            int screenshotsDeleted = cleanupOldFiles(getScreenshotsDirectory(), 7);
            int videosDeleted = cleanupOldFiles(getVideosDirectory(), 7);
            int logsDeleted = cleanupOldFiles(getLogsDirectory(), 30);
            int tempDeleted = cleanupOldFiles(getTempDirectory(), 1);
            
            logger.info("Cleanup summary - Screenshots: {}, Videos: {}, Logs: {}, Temp: {}", 
                       screenshotsDeleted, videosDeleted, logsDeleted, tempDeleted);
            
        } catch (Exception e) {
            logger.error("Error during previous runs cleanup", e);
        }
    }
    
    /**
     * Generate unique file name with timestamp
     * @param baseName Base name for the file
     * @param extension File extension (with or without dot)
     * @return Unique file name with timestamp
     */
    public static String generateUniqueFileName(String baseName, String extension) {
        if (baseName == null) baseName = "file";
        if (extension == null) extension = "txt";
        
        // Ensure extension starts with dot
        if (!extension.startsWith(".")) {
            extension = "." + extension;
        }
        
        String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMAT);
        return sanitizeFileName(baseName) + "_" + timestamp + extension;
    }
    
    /**
     * Sanitize file name by removing invalid characters
     * @param fileName Original file name
     * @return Sanitized file name
     */
    public static String sanitizeFileName(String fileName) {
        if (fileName == null || fileName.trim().isEmpty()) {
            return "unknown";
        }
        
        // Replace invalid characters with underscore and limit length
        String sanitized = fileName.replaceAll("[^a-zA-Z0-9._-]", "_")
                                  .replaceAll("_{2,}", "_"); // Replace multiple underscores with single
        
        // Remove leading/trailing underscores
        sanitized = sanitized.replaceAll("^_+|_+$", "");
        
        // Limit length
        if (sanitized.length() > 100) {
            sanitized = sanitized.substring(0, 100);
        }
        
        return sanitized.isEmpty() ? "unknown" : sanitized;
    }
    
    /**
     * Copy resource file from classpath to destination
     * @param resourcePath Resource path in classpath
     * @param destinationPath Destination file path
     * @return true if resource was copied successfully
     */
    public static boolean copyResourceToFile(String resourcePath, String destinationPath) {
        if (resourcePath == null || resourcePath.trim().isEmpty() ||
            destinationPath == null || destinationPath.trim().isEmpty()) {
            logger.error("Resource path and destination path cannot be null or empty");
            return false;
        }
        
        FILE_OPERATION_LOCK.lock();
        try (InputStream inputStream = FileUtils.class.getClassLoader().getResourceAsStream(resourcePath);
             FileOutputStream outputStream = new FileOutputStream(destinationPath)) {
            
            if (inputStream == null) {
                logger.error("Resource not found: {}", resourcePath);
                return false;
            }
            
            // Create destination directory if it doesn't exist
            createDirectory(Paths.get(destinationPath).getParent().toString());
            
            IOUtils.copy(inputStream, outputStream);
            
            logger.info("Resource copied successfully from {} to {}", resourcePath, destinationPath);
            return true;
            
        } catch (Exception e) {
            logger.error("Failed to copy resource from {} to {}", resourcePath, destinationPath, e);
            return false;
        } finally {
            FILE_OPERATION_LOCK.unlock();
        }
    }
    
    // Directory getter methods
    
    /**
     * Get screenshots directory path
     * @return Screenshots directory path
     */
    public static String getScreenshotsDirectory() {
        return ConfigurationManager.getFrameworkConfig().getScreenshotPath();
    }
    
    /**
     * Get videos directory path
     * @return Videos directory path
     */
    public static String getVideosDirectory() {
        return ConfigurationManager.getFrameworkConfig().getVideoRecordingPath();
    }
    
    /**
     * Get reports directory path
     * @return Reports directory path
     */
    public static String getReportsDirectory() {
        return "target" + File.separator + REPORTS_DIR;
    }
    
    /**
     * Get logs directory path
     * @return Logs directory path
     */
    public static String getLogsDirectory() {
        return ConfigurationManager.getFrameworkConfig().getLoggingPath();
    }
    
    /**
     * Get test data directory path
     * @return Test data directory path
     */
    public static String getTestDataDirectory() {
        return "src" + File.separator + "test" + File.separator + "resources" + File.separator + TEST_DATA_DIR;
    }
    
    /**
     * Get temp directory path
     * @return Temp directory path
     */
    public static String getTempDirectory() {
        return "target" + File.separator + TEMP_DIR;
    }
    
    /**
     * Get Allure results directory path
     * @return Allure results directory path
     */
    public static String getAllureResultsDirectory() {
        return getReportsDirectory() + File.separator + "allure-results";
    }
    
    /**
     * Get ExtentReports directory path
     * @return ExtentReports directory path
     */
    public static String getExtentReportsDirectory() {
        return getReportsDirectory() + File.separator + "extent";
    }
}