package vn.axonactive.authentication.domain.utils;

import java.io.InputStream;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import vn.axonactive.authentication.domain.validation.Assert;

public class ConfigPropertiesUtils {

    private static final Logger logger = LoggerFactory.getLogger(ConfigPropertiesUtils.class);

    private static Map<String, Properties> propertiesFileMap = new HashMap<>();
    
    private ConfigPropertiesUtils() {
        // private constructor
    }
    
    private static void load(String fileName) {
        Properties properties = new Properties();
        
        try {
            InputStream is = ConfigPropertiesUtils.class.getClassLoader().getResourceAsStream(fileName);
            properties.load(is);
            is.close();
        }
        catch (Exception e) {
            logger.error("Could not load the properties file " + e);
            throw new IllegalStateException("Could not load the properties file", e);
        }
        
        propertiesFileMap.put(fileName, properties);
    }
    
    public static String getProperty(String fileName, String propertiesKey) {
        if (!propertiesFileMap.containsKey(fileName)) {
            load(fileName);
        }
        String propertyValue = propertiesFileMap.get(fileName).getProperty(propertiesKey);
        Assert.assertNotEmpty(propertyValue, propertiesKey + " in " + fileName + " should not be empty");
        return propertyValue.trim();
    }
    
    public static String getProperty(String fileName, String propertiesKey, Object[] params) {
        String propertyValue = getProperty(fileName, propertiesKey);
        return MessageFormat.format(propertyValue, params).trim();
    }
}
