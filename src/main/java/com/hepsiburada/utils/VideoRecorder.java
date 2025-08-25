package com.hepsiburada.utils;

import com.hepsiburada.config.ConfigurationManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.monte.media.Format;
import org.monte.media.Registry;
import org.monte.media.math.Rational;
import org.monte.screenrecorder.ScreenRecorder;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.monte.media.AudioFormatKeys.*;
import static org.monte.media.VideoFormatKeys.*;

/**
 * Video Recorder utility class for recording test execution
 * Uses Monte Screen Recorder for screen recording functionality
 * 
 * @author Hepsiburada Test Automation Team
 */
public class VideoRecorder {
    
    private static final Logger logger = LogManager.getLogger(VideoRecorder.class);
    private static final DateTimeFormatter TIMESTAMP_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
    
    private ScreenRecorder screenRecorder;
    private final String testName;
    private String videoFilePath;
    
    /**
     * Constructor
     * @param testName Name of the test being recorded
     */
    public VideoRecorder(String testName) {
        this.testName = sanitizeFileName(testName);
        logger.info("VideoRecorder initialized for test: {}", this.testName);
    }
    
    /**
     * Start video recording
     * @throws Exception if recording fails to start
     */
    public void startRecording() throws Exception {
        logger.info("Starting video recording for test: {}", testName);
        
        try {
            // Ensure video directory exists
            ensureVideoDirectoryExists();
            
            // Create screen recorder with custom configuration
            createScreenRecorder();
            
            // Start recording
            screenRecorder.start();
            
            logger.info("Video recording started successfully for test: {}", testName);
            
        } catch (Exception e) {
            logger.error("Failed to start video recording for test: {}", testName, e);
            throw new RuntimeException("Failed to start video recording", e);
        }
    }
    
    /**
     * Stop video recording
     * @return Path to the recorded video file
     * @throws Exception if recording fails to stop
     */
    public String stopRecording() throws Exception {
        logger.info("Stopping video recording for test: {}", testName);
        
        try {
            if (screenRecorder != null) {
                screenRecorder.stop();
                
                // Get the actual file path from the recorder
                videoFilePath = getRecordedFilePath();
                
                logger.info("Video recording stopped successfully for test: {}. File saved at: {}", 
                           testName, videoFilePath);
                
                return videoFilePath;
            } else {
                logger.warn("Screen recorder is null - cannot stop recording");
                return "";
            }
            
        } catch (Exception e) {
            logger.error("Failed to stop video recording for test: {}", testName, e);
            throw new RuntimeException("Failed to stop video recording", e);
        } finally {
            screenRecorder = null;
        }
    }
    
    /**
     * Create screen recorder with custom settings
     * @throws IOException if recorder creation fails
     * @throws AWTException if AWT components fail
     */
    private void createScreenRecorder() throws IOException, AWTException {
        logger.debug("Creating screen recorder with custom settings");
        
        // Get screen dimensions
        GraphicsConfiguration gc = GraphicsEnvironment.getLocalGraphicsEnvironment()
                .getDefaultScreenDevice().getDefaultConfiguration();
        Rectangle captureArea = gc.getBounds();
        
        // Video directory
        File videoDir = new File(ConfigurationManager.getFrameworkConfig().getVideoRecordingPath());
        
        // Video format settings
        Format fileFormat = Registry.getInstance().getFormatByName("MP4");
        Format screenFormat = new Format(
            MediaTypeKey, MediaType.FILE,
            MimeTypeKey, MIME_MP4
        );
        
        Format videoFormat = new Format(
            MediaTypeKey, MediaType.VIDEO,
            EncodingKey, ENCODING_MP4_H264,
            CompressorNameKey, "h264",
            WidthKey, captureArea.width,
            HeightKey, captureArea.height,
            DepthKey, 24,
            FrameRateKey, Rational.valueOf(15),
            QualityKey, 0.7f,
            KeyFrameIntervalKey, 15 * 60 // Every 60 seconds
        );
        
        Format mouseFormat = new Format(
            MediaTypeKey, MediaType.VIDEO,
            EncodingKey, "black",
            FrameRateKey, Rational.valueOf(30)
        );
        
        Format audioFormat = new Format(
            MediaTypeKey, MediaType.AUDIO,
            EncodingKey, ENCODING_PCM_SIGNED,
            SampleRateKey, Rational.valueOf(44100),
            SampleSizeInBitsKey, 16,
            ChannelsKey, 2,
            FrameSizeKey, 4,
            ByteOrderKey, ByteOrder.LITTLE_ENDIAN,
            SignedKey, true
        );
        
        // Create custom screen recorder
        screenRecorder = new CustomScreenRecorder(
            gc, captureArea,
            fileFormat,
            screenFormat,
            videoFormat,
            mouseFormat,
            audioFormat,
            videoDir,
            generateVideoFileName()
        );
        
        logger.debug("Screen recorder created successfully");
    }
    
    /**
     * Custom ScreenRecorder to override file naming
     */
    private class CustomScreenRecorder extends ScreenRecorder {
        private final String fileName;
        
        public CustomScreenRecorder(GraphicsConfiguration cfg, Rectangle captureArea,
                                  Format fileFormat, Format screenFormat, Format videoFormat,
                                  Format mouseFormat, Format audioFormat,
                                  File movieFolder, String fileName) throws IOException, AWTException {
            super(cfg, captureArea, fileFormat, screenFormat, videoFormat, mouseFormat, audioFormat, movieFolder);
            this.fileName = fileName;
        }
        
        @Override
        protected File createMovieFile(Format fileFormat) throws IOException {
            if (!movieFolder.exists()) {
                movieFolder.mkdirs();
            }
            return new File(movieFolder, fileName);
        }
    }
    
    /**
     * Generate video file name with timestamp
     * @return Video file name
     */
    private String generateVideoFileName() {
        String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMAT);
        return String.format("%s_%s.mp4", testName, timestamp);
    }
    
    /**
     * Get the path of the recorded video file
     * @return Video file path
     */
    private String getRecordedFilePath() {
        try {
            File videoDir = new File(ConfigurationManager.getFrameworkConfig().getVideoRecordingPath());
            String fileName = generateVideoFileName();
            return new File(videoDir, fileName).getAbsolutePath();
        } catch (Exception e) {
            logger.error("Failed to get recorded file path", e);
            return "";
        }
    }
    
    /**
     * Sanitize file name by removing invalid characters
     * @param fileName Original file name
     * @return Sanitized file name
     */
    private String sanitizeFileName(String fileName) {
        if (fileName == null) {
            return "unknown_test";
        }
        
        // Replace invalid characters with underscore and limit length
        String sanitized = fileName.replaceAll("[^a-zA-Z0-9._-]", "_");
        if (sanitized.length() > 50) {
            sanitized = sanitized.substring(0, 50);
        }
        
        return sanitized;
    }
    
    /**
     * Ensure video directory exists
     */
    private void ensureVideoDirectoryExists() {
        try {
            String videoDir = ConfigurationManager.getFrameworkConfig().getVideoRecordingPath();
            File directory = new File(videoDir);
            
            if (!directory.exists()) {
                boolean created = directory.mkdirs();
                if (created) {
                    logger.info("Video directory created: {}", videoDir);
                } else {
                    logger.error("Failed to create video directory: {}", videoDir);
                    throw new RuntimeException("Failed to create video directory: " + videoDir);
                }
            }
            
        } catch (Exception e) {
            logger.error("Error ensuring video directory exists", e);
            throw new RuntimeException("Error with video directory", e);
        }
    }
    
    /**
     * Check if recording is in progress
     * @return true if recording is active
     */
    public boolean isRecording() {
        return screenRecorder != null && screenRecorder.getState() == ScreenRecorder.State.RECORDING;
    }
    
    /**
     * Get video file path (available after recording stops)
     * @return Video file path
     */
    public String getVideoFilePath() {
        return videoFilePath;
    }
    
    /**
     * Clean up old video files
     * @param daysOld Number of days old files to clean up
     */
    public static void cleanupOldVideos(int daysOld) {
        logger.info("Cleaning up video files older than {} days", daysOld);
        
        try {
            String videoDir = ConfigurationManager.getFrameworkConfig().getVideoRecordingPath();
            File directory = new File(videoDir);
            
            if (!directory.exists()) {
                logger.debug("Video directory does not exist: {}", videoDir);
                return;
            }
            
            File[] files = directory.listFiles();
            if (files == null) {
                logger.debug("No files found in video directory: {}", videoDir);
                return;
            }
            
            long cutoffTime = System.currentTimeMillis() - (daysOld * 24L * 60L * 60L * 1000L);
            int deletedCount = 0;
            
            for (File file : files) {
                if (file.isFile() && file.getName().endsWith(".mp4") && file.lastModified() < cutoffTime) {
                    if (file.delete()) {
                        deletedCount++;
                        logger.debug("Deleted old video: {}", file.getName());
                    } else {
                        logger.warn("Failed to delete old video: {}", file.getName());
                    }
                }
            }
            
            logger.info("Video cleanup completed - deleted {} old video files", deletedCount);
            
        } catch (Exception e) {
            logger.error("Failed to cleanup old videos", e);
        }
    }
    
    /**
     * Get video recording directory path
     * @return Video directory path
     */
    public static String getVideoDirectory() {
        return ConfigurationManager.getFrameworkConfig().getVideoRecordingPath();
    }
}