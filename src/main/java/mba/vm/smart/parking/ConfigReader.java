package mba.vm.smart.parking;

import jakarta.servlet.ServletContext;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * SmartParking - mba.vm.smart.parking
 *
 * @author VictorModi
 * @description TODO: coming soon.
 * @email victormodi@outlook.com
 * @date 2024/6/2 上午1:20
 */

/**
 * ConfigReader类，用于读取和管理配置文件中的属性。
 */
public class ConfigReader {
    private static final String CONFIG_FILE_PATH = "/WEB-INF/config.properties";
    private static final Properties properties = new Properties();

    public static void init(ServletContext servletContext) {
        try (InputStream inputStream = servletContext.getResourceAsStream(CONFIG_FILE_PATH)) {
            properties.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getProperty(String key) {
        return properties.getProperty(key);
    }
}
