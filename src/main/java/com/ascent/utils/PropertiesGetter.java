package com.ascent.utils;


import com.ascent.bean.PropertyEmpty;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * 配置加载工具类
 * 支持环境变量覆盖，优先级：环境变量 > 系统属性 > 配置文件
 */
public class PropertiesGetter {
    private static final Properties properties = new Properties();

    static{
        loadProperties();
    }

    private static void loadProperties() {
        try (InputStream input = PropertiesGetter.class.getClassLoader()
                .getResourceAsStream("config.properties")) {
            if (input != null) {
                properties.load(input);
                System.out.println("✅ 成功加载 config.properties");
            } else {
                System.err.println("⚠️ 未找到 config.properties 文件");
            }
        } catch (IOException e) {
            System.err.println("❌ 加载配置文件失败: " + e.getMessage());
        }
    }

    /**
     * 获取配置值，支持环境变量覆盖
     * @param key 配置键
     * @return 配置值
     * @throws PropertyEmpty 如果配置项为空
     */
    public static String getProperty(String key){
        // 优先检查环境变量 (例: DB_HOST 对应 db.host)
        String envKey = key.toUpperCase().replace(".","_");
        String value = System.getenv(envKey);
        if(value != null && !value.trim().isEmpty()){
            System.out.println("🌍 使用环境变量: " + envKey + " = " + value);
            return value;
        }
        // 2. 使用系统属性 (例: -Ddb.host=value)
        value = System.getProperty(key);
        if (value != null && !value.trim().isEmpty()) {
            System.out.println("⚙️ 使用系统属性: " + key + " = " + value);
            return value;
        }

        // 3. 使用配置文件中的值
        value = properties.getProperty(key);
        if (value != null && !value.trim().isEmpty()) {
            System.out.println("📄 使用配置文件: " + key + " = " + value);
            return value;
        }

        // 如果都没有找到，抛出异常
        throw new PropertyEmpty(key);
    }

    /**
     * 打印所有配置信息（用于调试）
     */
    public static void printConfiguration() {
        System.out.println("\n📋 当前配置信息:");
        System.out.println("================");

        String[] keys = {"db.url", "db.username", "db.password", "server.port"};

        for (String key : keys) {
            String value = key.contains("password") ? "***" : getProperty(key);// 避免打印敏感信息
            System.out.println(key + " = " + value);
        }

        System.out.println("================\n");
    }
}
