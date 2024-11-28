package com.ascent.dao;

import com.ascent.bean.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class UserDAO {

    /**
     * 根据用户名查询用户
     *
     * @param username 用户名
     * @return Optional<User> 返回用户对象
     */
    public Optional<User> getUserByUsername(String username){
        String query = "SELECT * FROM user_tb WHERE username = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                User user = new User();
                user.setUsername(resultSet.getString("username"));
                user.setPassword(resultSet.getString("password"));
                user.setAuthority(resultSet.getInt("authority"));
                return Optional.of(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    /**
     * 插入新用户
     *
     * @param user 要插入的用户对象
     * @return boolean 插入是否成功
     */
    public boolean insertUser(User user) throws SQLException {
        String query = "INSERT INTO user_tb (username, password) VALUES (?, ?)";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, user.getUsername());
            preparedStatement.setString(2, user.getPassword());
            return preparedStatement.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 更新用户信息
     *
     * @param user 要更新的用户对象
     * @return boolean 更新是否成功
     */
    public boolean updateUser(User user){
    String query = "UPDATE user_tb SET authority = ? WHERE username = ?";
    if (user.getPassword() != null && !user.getPassword().isEmpty()) {
        query = "UPDATE user_tb SET password = ?, authority = ? WHERE username = ?";
    }
    try (Connection connection = DBConnection.getConnection();
         PreparedStatement preparedStatement = connection.prepareStatement(query)) {

        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            preparedStatement.setString(1, user.getPassword());
            preparedStatement.setInt(2, user.getAuthority());
            preparedStatement.setString(3, user.getUsername());
        } else {
            preparedStatement.setInt(1, user.getAuthority());
            preparedStatement.setString(2, user.getUsername());
        }
        return preparedStatement.executeUpdate() > 0;

    } catch (SQLException e) {
        e.printStackTrace();
    }
    return false;
}

    /**
     * 删除用户
     *
     * @param username 要删除的用户的用户名
     * @return boolean 删除是否成功
     */
    public boolean deleteUser(String username){
        String query = "DELETE FROM user_tb WHERE username = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, username);
            return preparedStatement.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
