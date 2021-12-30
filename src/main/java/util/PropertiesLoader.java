package util;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class PropertiesLoader {

    public static Properties loadPropertiesFile(String propertiesFile) throws IOException {

        try(FileInputStream inStream = new FileInputStream(propertiesFile)) {
            Properties properties = new Properties();
            properties.load(inStream);
            return properties;
        } catch(IOException e) {
            throw new IOException("property file '" + propertiesFile + "' init failed");
        }
    }
}