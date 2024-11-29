package com.ascent.server;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.net.Socket;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ClientHandlerIntegrationTest {
    private ClientHandler clientHandler;
    private Socket mockSocket;
    private ByteArrayInputStream inputStream;
    private ByteArrayOutputStream outputStream;

    @BeforeEach
    void setUp() throws IOException {
        // 创建模拟的 Socket
        mockSocket = mock(Socket.class);

        // 准备输出流
        outputStream = new ByteArrayOutputStream();
        when(mockSocket.getOutputStream()).thenReturn(outputStream);
    }

    // 辅助方法：执行客户端处理器的测试
    private void executeClientHandlerWithRequest(String requestJson) throws IOException {
        // 准备输入流
        inputStream = new ByteArrayInputStream((requestJson + "\n").getBytes());
        when(mockSocket.getInputStream()).thenReturn(inputStream);

        // 创建并运行 ClientHandler
        clientHandler = new ClientHandler(mockSocket);
        clientHandler.run();
    }

    @Test
    void testUserLogin() throws IOException {
        // 准备登录请求
        JSONObject loginRequest = new JSONObject();
        loginRequest.put("function", "login");
        loginRequest.put("username", "testuser");
        loginRequest.put("password", "123456");

        // 执行请求
        executeClientHandlerWithRequest(loginRequest.toJSONString());

        // 解析响应
        String response = outputStream.toString().trim();
        JSONObject responseJson = JSON.parseObject(response);

        // 断言
        assertNotNull(responseJson);
        assertEquals("1", responseJson.getString("status"), "登录应该成功");
    }

    @Test
    void testUserRegister() throws IOException {
        // 准备注册请求
        JSONObject registerRequest = new JSONObject();
        registerRequest.put("function", "register");
        registerRequest.put("username", "testuser");
        registerRequest.put("password", "123456");

        // 执行请求
        executeClientHandlerWithRequest(registerRequest.toJSONString());

        // 解析响应
        String response = outputStream.toString().trim();
        JSONObject responseJson = JSON.parseObject(response);

        // 断言
        assertNotNull(responseJson);
        assertEquals("1", responseJson.getString("status"), "注册应该成功");
    }

    @Test
    void testProductQuery() throws IOException {
        // 准备产品查询请求
        JSONObject queryRequest = new JSONObject();
        queryRequest.put("function", "queryProductByName");
        queryRequest.put("name","testProduct");  // 假设数据库中存在ID为1的产品

        // 执行请求
        executeClientHandlerWithRequest(queryRequest.toJSONString());

        // 解析响应
        String response = outputStream.toString().trim();
        JSONObject responseJson = JSON.parseObject(response);

        // 断言
        assertNotNull(responseJson);
        assertEquals("1", responseJson.getString("status"), "产品查询应该成功");
        assertNotNull(responseJson.getJSONObject("product"), "应该返回产品信息");
    }

    @Test
    void testProductInsertion() throws IOException {
        // 准备产品插入请求
        JSONObject insertRequest = new JSONObject();
        insertRequest.put("function", "insertProduct");
        JSONObject product = new JSONObject();
        product.put("name", "testProduct");
        product.put("price", 59.99);
        product.put("stock", 100);
        product.put("cas", "123-45-6");
        product.put("formula", "C6H12O6");
        product.put("category", "0");
        product.put("structurePictureAddress", "testAddress");
        insertRequest.put("product", product);

        // 执行请求
        executeClientHandlerWithRequest(insertRequest.toJSONString());

        // 解析响应
        String response = outputStream.toString().trim();
        JSONObject responseJson = JSON.parseObject(response);

        // 断言
        assertNotNull(responseJson);
        assertEquals("1", responseJson.getString("status"), "产品插入应该成功");
    }

    @Test
    void testUnknownRequest() throws IOException {
        // 准备未知请求
        JSONObject unknownRequest = new JSONObject();
        unknownRequest.put("function", "unknownFunction");

        // 执行请求
        executeClientHandlerWithRequest(unknownRequest.toJSONString());

        // 解析响应
        String response = outputStream.toString().trim();
        JSONObject responseJson = JSON.parseObject(response);

        // 断言
        assertNotNull(responseJson);
        assertEquals("0", responseJson.getString("status"), "未知请求应该返回失败状态");
        assertEquals("未知请求", responseJson.getString("message"), "应该返回未知请求消息");
    }
}