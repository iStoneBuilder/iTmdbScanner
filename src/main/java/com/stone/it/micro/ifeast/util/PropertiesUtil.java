package com.stone.it.micro.ifeast.util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * @author jichen
 */
public class PropertiesUtil {

    private static Properties properties = new Properties();

    private static Map<String, String> propertiesMap = new HashMap<>();

    private static final String propertiesFileName = "application.properties";

    private static final String readConfig = "micro.stone.read.config";

    static void propertiesReader() {
        InputStream inputStream = null;
        try {
            // 读取默认配置
            inputStream = PropertiesUtil.class.getClassLoader().getResourceAsStream(propertiesFileName);
            properties.load(inputStream);
            // 读取动态配置
            String config = properties.getProperty(readConfig);
            if (config != null && config != "") {
                String[] configs = config.split(",");
                for (int i = 0; i < configs.length; i++) {
                    inputStream = PropertiesUtil.class.getClassLoader().getResourceAsStream(configs[i]);
                    properties.load(inputStream);
                }
            }
            initPropertiesMap(properties);
            propertiesMap.remove(readConfig);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static void initPropertiesMap(Properties properties) {
        Set<Map.Entry<Object, Object>> keyValues = properties.entrySet();
        for (Map.Entry<Object, Object> keyValue : keyValues) {
            propertiesMap.put((String) keyValue.getKey(), (String) keyValue.getValue());
        }
    }

    /**
     * 获取配置
     *
     * @param key
     * @return
     */
    public static String getValue(String key) {
        // 初始化数据
        if (propertiesMap.size() == 0) {
            propertiesReader();
        }
        String value = propertiesMap.get(key);
        // 没有获取到，从环境变量获取
        if (value == null || value.isEmpty()) {
            value = System.getenv(key);
        }
        return value;
    }

    public static String getValue(String key, String defaultValue) {
        // 初始化数据
        if (propertiesMap.size() == 0) {
            propertiesReader();
        }
        String value = propertiesMap.get(key);
        // 没有获取到，从环境变量获取
        if (value == null || value.isEmpty()) {
            value = System.getenv(key);
        }
        if (value == null || value.isEmpty()) {
            return defaultValue;
        }
        return value;
    }

}
