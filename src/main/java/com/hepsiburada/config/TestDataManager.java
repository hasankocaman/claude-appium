package com.hepsiburada.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Test Data Manager class to handle test data from various sources
 * Supports JSON files, Properties files, and environment variables
 * 
 * @author Hepsiburada Test Automation Team
 */
public final class TestDataManager {
    
    private static final Logger logger = LogManager.getLogger(TestDataManager.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final Map<String, JsonNode> jsonDataCache = new HashMap<>();
    private static final Map<String, Properties> propertiesCache = new HashMap<>();
    
    private TestDataManager() {
        // Utility class should not be instantiated
    }
    
    /**
     * Load JSON test data from resources
     * @param fileName JSON file name (without extension)
     * @return JsonNode containing the test data
     */
    public static JsonNode getJsonTestData(String fileName) {
        String fullFileName = fileName + ".json";
        
        if (jsonDataCache.containsKey(fullFileName)) {
            return jsonDataCache.get(fullFileName);
        }
        
        try (InputStream inputStream = TestDataManager.class.getClassLoader()
                .getResourceAsStream("testdata/" + fullFileName)) {
            
            if (inputStream == null) {
                logger.error("JSON test data file not found: {}", fullFileName);
                throw new RuntimeException("Test data file not found: " + fullFileName);
            }
            
            JsonNode jsonNode = objectMapper.readTree(inputStream);
            jsonDataCache.put(fullFileName, jsonNode);
            logger.info("JSON test data loaded successfully: {}", fullFileName);
            return jsonNode;
            
        } catch (IOException e) {
            logger.error("Error reading JSON test data file: {}", fullFileName, e);
            throw new RuntimeException("Error reading test data file: " + fullFileName, e);
        }
    }
    
    /**
     * Get specific test data from JSON file
     * @param fileName JSON file name (without extension)
     * @param path JSON path (e.g., "user.credentials.username")
     * @return String value from JSON
     */
    public static String getJsonTestDataValue(String fileName, String path) {
        JsonNode rootNode = getJsonTestData(fileName);
        JsonNode valueNode = rootNode;
        
        String[] pathParts = path.split("\\.");
        for (String part : pathParts) {
            valueNode = valueNode.get(part);
            if (valueNode == null) {
                logger.error("Path not found in JSON: {} in file: {}", path, fileName);
                throw new RuntimeException("Path not found: " + path + " in file: " + fileName);
            }
        }
        
        return valueNode.asText();
    }
    
    /**
     * Load Properties test data from resources
     * @param fileName Properties file name (without extension)
     * @return Properties object containing the test data
     */
    public static Properties getPropertiesTestData(String fileName) {
        String fullFileName = fileName + ".properties";
        
        if (propertiesCache.containsKey(fullFileName)) {
            return propertiesCache.get(fullFileName);
        }
        
        Properties properties = new Properties();
        try (InputStream inputStream = TestDataManager.class.getClassLoader()
                .getResourceAsStream("testdata/" + fullFileName)) {
            
            if (inputStream == null) {
                logger.error("Properties test data file not found: {}", fullFileName);
                throw new RuntimeException("Test data file not found: " + fullFileName);
            }
            
            properties.load(inputStream);
            propertiesCache.put(fullFileName, properties);
            logger.info("Properties test data loaded successfully: {}", fullFileName);
            return properties;
            
        } catch (IOException e) {
            logger.error("Error reading Properties test data file: {}", fullFileName, e);
            throw new RuntimeException("Error reading test data file: " + fullFileName, e);
        }
    }
    
    /**
     * Get specific property value from Properties file
     * @param fileName Properties file name (without extension)
     * @param key Property key
     * @return Property value
     */
    public static String getPropertiesTestDataValue(String fileName, String key) {
        Properties properties = getPropertiesTestData(fileName);
        String value = properties.getProperty(key);
        
        if (value == null) {
            logger.error("Property key not found: {} in file: {}", key, fileName);
            throw new RuntimeException("Property key not found: " + key + " in file: " + fileName);
        }
        
        return value;
    }
    
    /**
     * Get test data from environment variables with fallback to default
     * @param key Environment variable key
     * @param defaultValue Default value if environment variable is not set
     * @return Environment variable value or default value
     */
    public static String getEnvironmentVariable(String key, String defaultValue) {
        String value = System.getenv(key);
        if (value == null || value.isEmpty()) {
            value = System.getProperty(key, defaultValue);
        }
        
        logger.debug("Environment variable {} = {}", key, value);
        return value;
    }
    
    /**
     * Test Data Factory class to create test data objects
     */
    public static class TestDataFactory {
        
        /**
         * Create User credentials from test data
         * @return UserCredentials object
         */
        public static UserCredentials createUserCredentials() {
            return new UserCredentials(
                getJsonTestDataValue("user-data", "valid.username"),
                getJsonTestDataValue("user-data", "valid.password"),
                getJsonTestDataValue("user-data", "valid.email")
            );
        }
        
        /**
         * Create Search criteria from test data
         * @return SearchCriteria object
         */
        public static SearchCriteria createSearchCriteria() {
            return new SearchCriteria(
                getJsonTestDataValue("search-data", "macbook.keyword"),
                getJsonTestDataValue("search-data", "macbook.category"),
                getJsonTestDataValue("search-data", "macbook.sortBy")
            );
        }
        
        /**
         * Create Product information from test data
         * @return ProductInfo object
         */
        public static ProductInfo createProductInfo() {
            return new ProductInfo(
                getJsonTestDataValue("product-data", "macbook.name"),
                getJsonTestDataValue("product-data", "macbook.brand"),
                getJsonTestDataValue("product-data", "macbook.model")
            );
        }
    }
    
    /**
     * User Credentials data model
     */
    public static class UserCredentials {
        private final String username;
        private final String password;
        private final String email;
        
        public UserCredentials(String username, String password, String email) {
            this.username = username;
            this.password = password;
            this.email = email;
        }
        
        public String getUsername() { return username; }
        public String getPassword() { return password; }
        public String getEmail() { return email; }
    }
    
    /**
     * Search Criteria data model
     */
    public static class SearchCriteria {
        private final String keyword;
        private final String category;
        private final String sortBy;
        
        public SearchCriteria(String keyword, String category, String sortBy) {
            this.keyword = keyword;
            this.category = category;
            this.sortBy = sortBy;
        }
        
        public String getKeyword() { return keyword; }
        public String getCategory() { return category; }
        public String getSortBy() { return sortBy; }
    }
    
    /**
     * Product Information data model
     */
    public static class ProductInfo {
        private final String name;
        private final String brand;
        private final String model;
        
        public ProductInfo(String name, String brand, String model) {
            this.name = name;
            this.brand = brand;
            this.model = model;
        }
        
        public String getName() { return name; }
        public String getBrand() { return brand; }
        public String getModel() { return model; }
    }
}