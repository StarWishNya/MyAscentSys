package com.ascent.controllers;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import com.alibaba.fastjson.JSONObject;
class ProductControllerTest {
    private final ProductController productController = new ProductController();

    //TODO:此处的id无法被其他的测试用例访问，需要修改
    private int id;



    @Test
    @Order(1)
    void insertProduct() {
        JSONObject request = new JSONObject();
        request.put("function", "insertProduct");
        JSONObject product = new JSONObject();
        product.put("name", "testProduct");
        product.put("price", "100");
        product.put("stock", "100");
        product.put("cas", "123456-78-9");
        product.put("formula", "C6H12O6");
        product.put("category", "0");
        product.put("structurePictureAddress", "testAddress");
        request.put("product", product);
        System.out.println(request.toJSONString());
        String response = productController.insertProduct(request.toJSONString());
        System.out.println(response);

        JSONObject jsonObject = JSONObject.parseObject(response);
        id = jsonObject.getInteger("id");

    }

    @Test
    @Order(2)
    void queryProductById(){
        JSONObject request = new JSONObject();
        request.put("function", "queryProductById");
        request.put("id", id);
        System.out.println(request.toJSONString());
        String response = productController.queryProductById(request.toJSONString());
        System.out.println(response);
    }

    @Test
    @Order(3)
    void queryProductByInvalidId(){
        JSONObject request = new JSONObject();
        request.put("function", "queryProductById");
        request.put("id", -1);
        System.out.println(request.toJSONString());
        String response = productController.queryProductById(request.toJSONString());
        System.out.println(response);
    }

    @Test
    @Order(4)
    void queryProductByName(){
        JSONObject request = new JSONObject();
        request.put("function", "queryProductByName");
        request.put("name", "testProduct");
        System.out.println(request.toJSONString());
        String response = productController.queryProductByName(request.toJSONString());
        System.out.println(response);
    }

    @Test
    @Order(5)
    void queryProductByInvalidName(){
        JSONObject request = new JSONObject();
        request.put("function", "queryProductByName");
        request.put("name", "invalidName");
        System.out.println(request.toJSONString());
        String response = productController.queryProductByName(request.toJSONString());
        System.out.println(response);
    }

    @Test
    @Order(6)
    void updateProduct(){
        JSONObject request = new JSONObject();
        request.put("function", "updateProduct");
        JSONObject product = new JSONObject();
        product.put("id", id);
        product.put("name", "updateTestProduct");
        product.put("price", "100");
        product.put("stock", "100");
        product.put("cas", "123456-78-9");
        product.put("formula", "C6H12O6");
        product.put("category", "0");
        product.put("structurePictureAddress", "testAddress");
        request.put("product", product);
        System.out.println(request.toJSONString());
        String response = productController.updateProduct(request.toJSONString());
        System.out.println(response);
    }

    @Test
    @Order(7)
    void updateProductWithInvalidId(){
        JSONObject request = new JSONObject();
        request.put("function", "updateProduct");
        JSONObject product = new JSONObject();
        product.put("id", -1);
        product.put("name", "updateTestProduct");
        product.put("price", "100");
        product.put("stock", "100");
        product.put("cas", "123456-78-9");
        product.put("formula", "C6H12O6");
        product.put("category", "0");
        product.put("structurePictureAddress", "testAddress");
        request.put("product", product);
        System.out.println(request.toJSONString());
        String response = productController.updateProduct(request.toJSONString());
        System.out.println(response);
    }

    @Test
    @Order(8)
    void deleteProduct(){
        JSONObject request = new JSONObject();
        request.put("function", "deleteProduct");
        request.put("id", id);
        System.out.println(request.toJSONString());
        String response = productController.deleteProduct(request.toJSONString());
        System.out.println(response);
    }

    @Test
    @Order(9)
    void getAllProducts(){
        JSONObject request = new JSONObject();
        request.put("function", "getAllProducts");
        System.out.println(request.toJSONString());
        String response = productController.getAllProducts();
        System.out.println(response);
    }
}