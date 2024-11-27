package com.ascent.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.io.InputStream;

public class DBConnection {

    // JDBC URL, 用户名和密码会从配置文件读取
    private static final String dbUrl;
    private static final String dbUsername;
    private static final String dbPassword;

    // 静态代码块加载配置文件
    static {
        try (InputStream input = DBConnection.class.getClassLoader().getResourceAsStream("dbconfig.properties")) {
            if (input == null) {
                throw new RuntimeException("配置文件 dbconfig.properties 未找到！");
            }
            Properties properties = new Properties();
            properties.load(input);

            dbUrl = properties.getProperty("db.url");
            dbUsername = properties.getProperty("db.username");
            dbPassword = properties.getProperty("db.password");

            // 确保 JDBC 驱动可用
            Class.forName(properties.getProperty("db.driver"));
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("加载数据库配置失败：" + e.getMessage());
        }
    }

    /**
     * 获取数据库连接
     *
     * @return Connection 对象
     * @throws SQLException 数据库连接异常
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
    }

    /**
     * 关闭数据库连接
     *
     * @param connection 要关闭的 Connection 对象
     */
    public static void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
