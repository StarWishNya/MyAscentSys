package com.ascent.controllers;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import com.alibaba.fastjson.JSONObject;

public class UserControllerTest {
    private final UserController userController = new UserController();

    @Test
    @Order(1)
    void register() {
        JSONObject request = new JSONObject();
        request.put("function", "register");
        request.put("username", "testUser");
        request.put("password", "123456");
        System.out.println(request.toJSONString());
        String response = userController.register(request.toJSONString());
        System.out.println(response);
    }

    @Test
    @Order(2)
    void login() {
        JSONObject request = new JSONObject();
        request.put("function", "login");
        request.put("username", "testUser");
        request.put("password", "123456");
        System.out.println(request.toJSONString());
        String response = userController.login(request.toJSONString());
        System.out.println(response);
    }

    @Test
    @Order(3)
    void loginWithInvalidUsername() {
        JSONObject request = new JSONObject();
        request.put("function", "login");
        request.put("username", "invalidUser");
        request.put("password", "123456");
        System.out.println(request.toJSONString());
        String response = userController.login(request.toJSONString());
        System.out.println(response);
    }

    @Test
    @Order(4)
    void loginWithInvalidPassword() {
        JSONObject request = new JSONObject();
        request.put("function", "login");
        request.put("username", "testUser");
        request.put("password", "invalidPassword");
        System.out.println(request.toJSONString());
        String response = userController.login(request.toJSONString());
        System.out.println(response);
    }

    @Test
    @Order(5)
    void changePassword() {
        JSONObject request = new JSONObject();
        request.put("function", "changePassword");
        request.put("username", "testUser");
        request.put("oldPassword", "123456");
        request.put("newPassword", "654321");
        System.out.println(request.toJSONString());
        String response = userController.changePassword(request.toJSONString());
        System.out.println(response);
    }

    @Test
    @Order(6)
    void changePasswordWithInvalidUsername() {
        JSONObject request = new JSONObject();
        request.put("function", "changePassword");
        request.put("username", "invalidUser");
        request.put("oldPassword", "123456");
        request.put("newPassword", "654321");
        System.out.println(request.toJSONString());
        String response = userController.changePassword(request.toJSONString());
        System.out.println(response);
    }

    @Test
    @Order(7)
    void changeAuthority() {
        JSONObject request = new JSONObject();
        request.put("function", "changeAuthority");
        request.put("username", "testUser");
        request.put("authority", "1");
        System.out.println(request.toJSONString());
        String response = userController.changeAuthority(request.toJSONString());
        System.out.println(response);
    }

    @Test
    @Order(8)
    void delete() {
        JSONObject request = new JSONObject();
        request.put("function", "deleteUser");
        request.put("username", "testUser");
        System.out.println(request.toJSONString());
        String response = userController.deleteUser(request.toJSONString());
        System.out.println(response);
    }

    @Test
    @Order(9)
    void getAllUsers() {
        JSONObject request = new JSONObject();
        request.put("function", "getAllUsers");
        System.out.println(request.toJSONString());
        String response = userController.getAllUsers();
        System.out.println(response);
    }

}
