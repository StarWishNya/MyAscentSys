package com.ascent.dao;

import java.sql.Connection;

public class DBConnectionTest {
    public static void main(String[] args) {
        try {
            Connection connection = DBConnection.getConnection();
            if (connection != null) {
                System.out.println("数据库连接成功！");
                DBConnection.closeConnection(connection);
            }
        } catch (Exception e) {
            System.err.println("数据库连接失败：" + e.getMessage());
        }
    }
}
