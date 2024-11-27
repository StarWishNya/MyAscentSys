package com.ascent.controllers;

import com.alibaba.fastjson.JSONObject;
import com.ascent.bean.Product;
import com.ascent.util.ProductService;

import com.alibaba.fastjson.JSON;

import java.util.Optional;

public class ProductController {
    private final ProductService productService = new ProductService();

    //TODO:增加返回错误信息

    /**
     * 根据产品ID查询产品
     * @param request json格式的请求
     * {
     *   "function": "queryProductById",
     *   "id": "产品ID"
     * }
     * @return json格式的响应
     * {
     *     "status": "1",
     *     "message": "查询成功",
     *     "product": {
     *                  "id": "产品ID",
     *                  "name": "产品名",
     *                  "price": "产品价格",
     *                  "stock": "产品库存",
     *                  "cas": "CAS号",
     *                  "formula": "分子式",
     *                  "category": "类别",
     *                  "structurePictureAddress": "结构式图片地址"
     *                  }
     * }
     * 或
     * {
     *     "status": "0",
     *     "message": "查询失败"
     *     "error": "异常信息"
     * }
     *
     */
    public String queryProductById(String request) {
        try {
            // 解析请求
            JSONObject jsonObject = JSON.parseObject(request);
            int id = jsonObject.getInteger("id");

            // 查询产品
            Optional<Product> product = productService.queryProductById(id);
            JSONObject response = new JSONObject();

            if (product.isPresent()) {
                Product product1 = product.get();
                response.put("status", "1");
                response.put("message", "查询成功");
                response.put("product", product1);
            } else {
                response.put("status", "0");
                response.put("message", "查询失败");
            }
            return response.toJSONString();
        } catch (Exception e) {
            JSONObject response = new JSONObject();
            response.put("status", "0");
            response.put("message", "查询异常");
            response.put("error", e.getMessage());
            return response.toJSONString();
        }
    }

    /**
     * 根据产品名查询产品
     * @param request json格式的请求
     * {
     *  "function": "queryProductByName",
     *  "name": "产品名"
     * }
     * @return json格式的响应
     * {
     *    "status": "1",
     *    "message": "查询成功",
     *    "product": {
     *             "id": "产品ID",
     *             "name": "产品名",
     *             "price": "产品价格",
     *             "stock": "产品库存",
     *             "cas": "CAS号",
     *             "formula": "分子式",
     *             "category": "类别",
     *             "structurePictureAddress": "结构式图片地址"
     *             }
     * }
     * 或
     * {
     *   "status": "0",
     *   "message": "查询失败"
     *   "error": "异常信息"
     * }
     */
    public String queryProductByName(String request) {
        try {
            // 解析请求
            JSONObject jsonObject = JSON.parseObject(request);
            String name = jsonObject.getString("name");

            // 查询产品
            Optional<Product> product = productService.queryProductByName(name);
            JSONObject response = new JSONObject();

            if (product.isPresent()) {
                Product product1 = product.get();
                response.put("status", "1");
                response.put("message", "查询成功");
                response.put("product", product1);
            } else {
                response.put("status", "0");
                response.put("message", "查询失败");
            }
            return response.toJSONString();
        } catch (Exception e) {
            JSONObject response = new JSONObject();
            response.put("status", "0");
            response.put("message", "查询异常");
            response.put("error", e.getMessage());
            return response.toJSONString();
        }
    }

    /**
     * 插入新产品
     * @param request json格式的请求
     * {
     *   "function": "insertProduct",
     *   "product": {
     *                "name": "产品名",
     *                "price": "产品价格",
     *                "stock": "产品库存",
     *                "cas": "CAS号",
     *                "formula": "分子式",
     *                "category": "类别",
     *                "structurePictureAddress": "结构式图片地址"
     *                }
     * }
     * @return json格式的响应
     * {
     *    "status": "1",
     *    "message": "插入成功"
     *    "id": "产品ID"
     * }
     * 或
     * {
     *   "status": "0",
     *   "message": "插入失败"
     *   "error": "异常信息"
     * }
     */
    public String insertProduct(String request) {
        try {
            // 解析请求
            JSONObject jsonObject = JSON.parseObject(request);
            JSONObject productJson = jsonObject.getJSONObject("product");
            Product product = productJson.toJavaObject(Product.class);

            // 插入产品
            boolean result = productService.insertProduct(product);
            JSONObject response = new JSONObject();

            if (result) {
                response.put("status", "1");
                response.put("message", "插入成功");
                response.put("id", product.getId());
            } else {
                response.put("status", "0");
                response.put("message", "插入失败");
            }
            return response.toJSONString();
        } catch (Exception e) {
            JSONObject response = new JSONObject();
            response.put("status", "0");
            response.put("message", "插入异常");
            response.put("error", e.getMessage());
            return response.toJSONString();
        }
    }
    /**
     * 更新产品信息
     * @param request json格式的请求
     * {
     *  "function": "updateProduct",
     *  "product": {
     *                "id": "产品ID",
     *                "name": "产品名",
     *                "price": "产品价格",
     *                "stock": "产品库存",
     *                "cas": "CAS号",
     *                "formula": "分子式",
     *                "category": "类别",
     *                "structurePictureAddress": "结构式图片地址"
     *                }
     * }
     * @return json格式的响应
     * {
     *   "status": "1",
     *   "message": "更新成功"
     * }
     * 或
     * {
     *  "status": "0",
     *  "message": "更新失败"
     *  "error": "异常信息"
     * }
     */
    public String updateProduct(String request) {
        try {
            // 解析请求
            JSONObject jsonObject = JSON.parseObject(request);
            JSONObject productJson = jsonObject.getJSONObject("product");
            Product product = productJson.toJavaObject(Product.class);

            // 更新产品
            boolean result = productService.updateProductById(product.getId(), product);
            JSONObject response = new JSONObject();

            if (result) {
                response.put("status", "1");
                response.put("message", "更新成功");
            } else {
                response.put("status", "0");
                response.put("message", "更新失败");
            }
            return response.toJSONString();
        } catch (Exception e) {
            JSONObject response = new JSONObject();
            response.put("status", "0");
            response.put("message", "更新异常");
            response.put("error", e.getMessage());
            return response.toJSONString();
        }
    }

    /**
     * 删除产品
     * @param request json格式的请求
     * {
     *   "function": "deleteProduct",
     *   "id": "产品ID"
     * }
     * 或
     * {
     *   "function": "deleteProduct",
     *   "name": "产品名"
     * }
     * @return json格式的响应
     * {
     *     "status": "1",
     *     "message": "删除成功"
     * }
     * 或
     * {
     *      "status": "0",
     *      "message": "删除失败"
     *      "error": "异常信息"
     * }
     */
    public String deleteProduct(String request) {
        try {
            // 解析请求
            JSONObject jsonObject = JSON.parseObject(request);
            String id = jsonObject.getString("id");
            String name = jsonObject.getString("name");
            int num_id = Integer.parseInt(id);
            // 删除产品
            boolean result;
            if (!id.isEmpty()) {
                result = productService.deleteProduct(num_id);
            } else {
                try{
                    Optional<Product> product = productService.queryProductByName(name);
                    if (product.isPresent()) {
                        num_id = product.get().getId();
                    }
                } catch (Exception e) {
                    JSONObject response = new JSONObject();
                    response.put("status", "0");
                    response.put("message", "删除失败");
                    response.put("error", "产品不存在");
                    return response.toJSONString();
                }
                result = productService.deleteProduct(num_id);
            }
            JSONObject response = new JSONObject();

            if (result) {
                response.put("status", "1");
                response.put("message", "删除成功");
            } else {
                response.put("status", "0");
                response.put("message", "删除失败");
            }
            return response.toJSONString();
        } catch (Exception e) {
            JSONObject response = new JSONObject();
            response.put("status", "0");
            response.put("message", "删除异常");
            response.put("error", e.getMessage());
            return response.toJSONString();
        }
    }
}
