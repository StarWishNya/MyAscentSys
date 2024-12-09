package com.ascent.client;

import java.io.*;
import java.net.Socket;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import com.ascent.controllers.ProductController;
import com.ascent.controllers.UserController;


public class ClientHandler implements Runnable{
    private final Socket clientSocket;
    private final UserController userController = new UserController();
    private final ProductController productController = new ProductController();

    public ClientHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    @Override
    public void run(){
        try (BufferedReader Reader  = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             PrintWriter Writer = new PrintWriter(clientSocket.getOutputStream(), true)) {
            // 读取请求
            String request;
            while ((request = Reader.readLine()) != null) {
                System.out.println("接收到请求：" + request);
                JSONObject jsonObject = JSON.parseObject(request);
                String response = handleRequest(jsonObject);

                System.out.println("返回响应：" + response);
                Writer.println(response);
            }

        }catch (IOException e){
            JSONObject error = new JSONObject();
            error.put("status", "0");
            error.put("message", "处理请求异常");
            error.put("error", e.getMessage());
            System.err.println("返回响应：" + error.toJSONString());
        }
    }

    private String handleRequest(JSONObject jsonObject){
        try{
            String function = jsonObject.getString("function");
            String response;
            switch (function) {
                case "login":
                    response = userController.login(jsonObject.toJSONString());
                    break;
                case "register":
                    response = userController.register(jsonObject.toJSONString());
                    break;
                case "changePassword":
                    response = userController.changePassword(jsonObject.toJSONString());
                    break;
                case "changeAuthority":
                    response = userController.changeAuthority(jsonObject.toJSONString());
                    break;
                case "deleteUser":
                    response = userController.deleteUser(jsonObject.toJSONString());
                    break;
                case "queryProductById":
                    response = productController.queryProductById(jsonObject.toJSONString());
                    break;
                case "queryProductByName":
                    response = productController.queryProductByName(jsonObject.toJSONString());
                    break;
                case "insertProduct":
                    response = productController.insertProduct(jsonObject.toJSONString());
                    break;
                case "updateProduct":
                    response = productController.updateProduct(jsonObject.toJSONString());
                    break;
                case "deleteProduct":
                    response = productController.deleteProduct(jsonObject.toJSONString());
                    break;
                case "getAllUsers":
                    response = userController.getAllUsers();
                    break;
                case "getAllProducts":
                    response = productController.getAllProducts();
                    break;
                case "getAuthority":
                    response = userController.getAuthority(jsonObject.toJSONString());
                    break;
                default:
                    JSONObject error = new JSONObject();
                    error.put("status", "0");
                    error.put("message", "未知请求");
                    response = error.toJSONString();
            }
            return response;
        }catch (Exception e){
            JSONObject error = new JSONObject();
            error.put("status", "0");
            error.put("message", "处理请求异常");
            error.put("error", e.getMessage());
            return error.toJSONString();
        }
    }
}
