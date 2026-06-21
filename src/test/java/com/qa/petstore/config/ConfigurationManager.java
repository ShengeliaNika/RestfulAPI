package com.qa.petstore.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class ConfigurationManager {

    private static final ConfigurationManager INSTANCE = new ConfigurationManager();
    private static final String CONFIG_FILE = "config.properties";

    private final Properties properties = new Properties();

    private ConfigurationManager() {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream(CONFIG_FILE)) {
            if (input == null) {
                throw new IllegalStateException(CONFIG_FILE + " was not found on the classpath");
            }
            properties.load(input);
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to load " + CONFIG_FILE, exception);
        }
    }

    public static ConfigurationManager getInstance() {
        return INSTANCE;
    }

    public String getBaseUrl() {
        return properties.getProperty("base.url");
    }
}
