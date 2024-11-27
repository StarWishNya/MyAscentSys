package com.ascent.controllers;

import org.junit.jupiter.api.Test;
import com.alibaba.fastjson.JSONObject;
class ProductControllerTest {
    private final ProductController productController = new ProductController();

    @Test
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
    }

    //TODO :测试其他方法
}