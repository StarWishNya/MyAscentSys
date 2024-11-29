package com.ascent.controllers;

import com.alibaba.fastjson.JSONObject;
import com.ascent.bean.Authenticate;
import com.ascent.util.UserService;

import com.alibaba.fastjson.JSON;

import java.util.Optional;


public class UserController {
    private final UserService userService = new UserService();

    /**
     * 用户登录
     * @param request json格式的请求
     * {
     *   "function": "login",
     *   "username": "用户名",
     *   "password": "密码"
     * }
     * @return json格式的响应
     * {
     *     "status": "1",
     *     "message": "登录成功",
     *     "authority": "用户权限"
     * }
     * 或
     * {
     *     "status": "0",
     *     "message": "登录失败"
     * }
     * 或
     * {
     *    "status": "0",
     *    "message": "登录异常"
     *    "error": "异常信息"
     * }
     */
    public String login(String request){
        try{
            // 解析请求
            JSONObject jsonObject = JSON.parseObject(request);
            String username = jsonObject.getString("username");
            String password = jsonObject.getString("password");

            // 登录验证
            Optional<Authenticate> isAuthentic = userService.authenticateUser(username, password);
            JSONObject response = new JSONObject();

            if(isAuthentic.isPresent()) {
                Authenticate authenticate = isAuthentic.get();
                response.put("status", "1");
                response.put("message", "登录成功");
                response.put("authority", authenticate.getAuthority());
            }else{
                response.put("status", "0");
                response.put("message", "登录失败");
            }
            return response.toJSONString();
        }catch (Exception e){
            e.printStackTrace();
            JSONObject error = new JSONObject();
            error.put("status", "0");
            error.put("message", "登录异常");
            error.put("error", e.getMessage());
            return error.toJSONString();
        }
    }

    /**
     * 用户注册
     * @param request json格式的请求
     * {
     *   "function": "register",
     *   "username": "用户名",
     *   "password": "密码"
     * }
     * @return json格式的响应
     * {
     *     "status": "1",
     *     "message": "注册成功"
     * }
     * 或
     * {
     *     "status": "0",
     *     "message": "注册失败"
     * }
     * 或
     * {
     *    "status": "0",
     *    "message": "注册异常"
     *    "error": "异常信息"
     * }
     */
    public String register(String request) {
        try {
            // 解析请求
            JSONObject jsonObject = JSON.parseObject(request);
            String username = jsonObject.getString("username");
            String password = jsonObject.getString("password");

            // 用户注册
            boolean isRegistered = userService.registerUser(username, password);
            JSONObject response = new JSONObject();

            if (isRegistered) {
                response.put("status", "1");
                response.put("message", "注册成功");
            } else {
                response.put("status", "0");
                response.put("message", "注册失败");
            }
            return response.toJSONString();
        } catch (Exception e) {
            e.printStackTrace();
            JSONObject error = new JSONObject();
            error.put("status", "0");
            error.put("message", "注册异常");
            error.put("error", e.getMessage());
            return error.toJSONString();
        }
    }
    /**
     * 更改用户密码
     * @param request json格式的请求
     * {
     *    "function": "changePassword",
     *    "username": "用户名",
     *    "oldPassword": "旧密码",
     *    "newPassword": "新密码"
     * }
     * @return json格式的响应
     * {
     *     "status": "1",
     *     "message": "修改密码成功"
     * }
     * 或
     * {
     *     status": "0",
     *     "message": "修改密码失败, 旧密码错误"
     * }
     * 或
     * {
     *    "status": "0",
     *    "message": "修改密码异常"
     *    error": "异常信息"
     * }
     */
    public String changePassword(String request) {
        try {
            // 解析请求
            JSONObject jsonObject = JSON.parseObject(request);
            String username = jsonObject.getString("username");
            String oldPassword = jsonObject.getString("oldPassword");
            String newPassword = jsonObject.getString("newPassword");

            // 修改密码
            Optional<Authenticate> isAuthentic = userService.authenticateUser(username, oldPassword);
            JSONObject response = new JSONObject();

            if (isAuthentic.isPresent()) {
                boolean isChanged = userService.changePassword(username, newPassword);
                if (isChanged) {
                    response.put("status", "1");
                    response.put("message", "修改密码成功");
                } else {
                    response.put("status", "0");
                    response.put("message", "修改密码失败");
                }
            } else {
                response.put("status", "0");
                response.put("message", "修改密码失败, 旧密码错误");
            }
            return response.toJSONString();
        } catch (Exception e) {
            e.printStackTrace();
            JSONObject error = new JSONObject();
            error.put("status", "0");
            error.put("message", "修改密码异常");
            error.put("error", e.getMessage());
            return error.toJSONString();
        }
    }

    /**
     * 更改用户权限
     * @param request json格式的请求
     * {
     *    "function": "changeAuthority",
     *    "username": "用户名",
     *    "authority": "新权限"
     * }
     * @return json格式的响应
     * {
     *     "status": "1",
     *     "message": "修改权限成功"
     * }
     * 或
     * {
     *     status": "0",
     *     "message": "修改权限失败"
     * }
     * 或
     * {
     *    "status": "0",
     *    "message": "修改权限异常"
     *    error": "异常信息"
     * }
     */
    public String changeAuthority(String request) {
        try {
            // 解析请求
            JSONObject jsonObject = JSON.parseObject(request);
            String username = jsonObject.getString("username");
            int authority = jsonObject.getInteger("authority");

            // 修改权限
            boolean isChanged = userService.changeAuthority(username, authority);
            JSONObject response = new JSONObject();

            if (isChanged) {
                response.put("status", "1");
                response.put("message", "修改权限成功");
            } else {
                response.put("status", "0");
                response.put("message", "修改权限失败");
            }
            return response.toJSONString();
        } catch (Exception e) {
            e.printStackTrace();
            JSONObject error = new JSONObject();
            error.put("status", "0");
            error.put("message", "修改权限异常");
            error.put("error", e.getMessage());
            return error.toJSONString();
        }
    }

    /**
     * 删除用户
     * @param request json格式的请求
     * {
     *    "function": "deleteUser",
     *    "username": "用户名"
     * }
     * @return json格式的响应
     * {
     *     "status": "1",
     *     "message": "删除用户成功"
     * }
     * 或
     * {
     *    "status": "0",
     *    "message": "删除用户失败"
     *    "error": "用户不存在"
     * }
     * 或
     * {
     *   "status": "0",
     *   "message": "删除用户异常"
     *   "error": "异常信息"
     */
    public String deleteUser(String request) {
        try {
            // 解析请求
            JSONObject jsonObject = JSON.parseObject(request);
            String username = jsonObject.getString("username");

            // 删除用户
            boolean isDeleted = userService.deleteUser(username);
            JSONObject response = new JSONObject();

            if (isDeleted) {
                response.put("status", "1");
                response.put("message", "删除用户成功");
            } else {
                response.put("status", "0");
                response.put("message", "删除用户失败");
                response.put("error", "用户不存在");
            }
            return response.toJSONString();
        } catch (Exception e) {
            e.printStackTrace();
            JSONObject error = new JSONObject();
            error.put("status", "0");
            error.put("message", "删除用户异常");
            error.put("error", e.getMessage());
            return error.toJSONString();
        }
    }

    /**
     * 获取所有用户
     * @return json格式的响应
     * {
     *     "status": "1",
     *     "message": "获取用户成功",
     *     "users": [
     *     {
     *     "username": "用户名",
     *     "authority": "用户权限"
     *     },]
     * }
     */
    public String getAllUsers() {
        try {
            // 获取所有用户
            JSONObject response = new JSONObject();
            response.put("status", "1");
            response.put("message", "获取用户成功");
            response.put("users", userService.getAllUsers());
            return response.toJSONString();
        } catch (Exception e) {
            e.printStackTrace();
            JSONObject error = new JSONObject();
            error.put("status", "0");
            error.put("message", "获取用户异常");
            error.put("error", e.getMessage());
            return error.toJSONString();
        }
    }
}
