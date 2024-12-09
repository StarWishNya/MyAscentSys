package com.ascent.server;

import com.ascent.bean.User;
import com.ascent.bean.Authenticate;
import com.ascent.dao.UserDAO;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserService {
    private final UserDAO userDAO = new UserDAO();

    /**
     * 用户登录验证
     *
     * @param username 用户名
     * @param password 密码
     * @return Optional<Authenticate> 验证是否通过
     */
    public Optional<Authenticate> authenticateUser(String username, String password) {
        Optional<User> userOptional = userDAO.getUserByUsername(username);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (user.getPassword().equals(password)) {
                return Optional.of(new Authenticate(user.getUsername(), user.getAuthority()));
            }
        }
        return Optional.empty();
    }

    /**
     * 用户注册
     *
     * @param username 用户名
     * @param password 密码
     * @return boolean 注册是否成功
     */
    public boolean registerUser(String username, String password){
        User user = new User(username, password);
        try {
            return userDAO.insertUser(user);
        } catch (SQLException e) {
            if (e.getSQLState().equals("23505")) { // 23505 是 PostgreSQL 的唯一约束冲突错误代码
                return false; // 用户名已存在
            }
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 更改用户密码
     *
     * @param username 用户名
     * @param password 新密码
     * @return boolean 是否更改成功
     */
    public boolean changePassword(String username, String password) {
        User user = new User(username, password);
        return userDAO.updateUser(user);
    }

    /**
     * 更改用户权限
     *
     * @param username 用户名
     * @param authority 新权限
     * @return boolean 是否更改成功
     */
    public boolean changeAuthority(String username, int authority) {
        User user = new User(username, null, authority);
        return userDAO.updateUser(user);
    }

    /**
     * 删除用户
     *
     * @param username 用户名
     * @return boolean 是否删除成功
     */
    public boolean deleteUser(String username) {
        return userDAO.deleteUser(username);
    }

    /**
     * 获取所有用户
     * @return List<User> 所有用户
     * @throws SQLException 数据库查询异常
     */
    public List<User> getAllUsers() throws SQLException {
        ResultSet resultSet = userDAO.getAllUsers();
        List<User> users = new ArrayList<>();
        while(resultSet.next()){
            User user = new User();
            user.setUsername(resultSet.getString("username"));
            user.setAuthority(resultSet.getInt("authority"));
            users.add(user);
        }
        return users;
    }

    /**
     * 查询用户权限
     * @param username 用户名
     * @return int 用户权限
     */
    public int getUserAuthority(String username) {
        Optional<User> userOptional = userDAO.getUserByUsername(username);
        return userOptional.map(User::getAuthority).orElse(-1);
    }
}
