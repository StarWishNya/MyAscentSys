package com.ascent.util;

import com.ascent.bean.Product;
import com.ascent.dao.ProductDAO;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ProductService {
    private final ProductDAO productDAO = new ProductDAO();

    /**
     * 根据产品ID查询产品
     * @param id 产品ID
     * @return Optional<Product>
     */
    public Optional<Product> queryProductById(int id) {
        return productDAO.queryProductById(id);
    }

    /**
     * 根据产品名查询产品
     * @param name 产品名
     * @return Optional<Product>
     */
    public Optional<Product> queryProductByName(String name) {
        return productDAO.queryProductByName(name);
    }

    /**
     * 插入新产品
     * @param product 要插入的产品对象
     * @return boolean 插入是否成功
     */
    public boolean insertProduct(Product product) {
        try {
            return productDAO.insertProduct(product);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 更新产品信息
     * @param product 要更新的产品对象
     * @return boolean 更新是否成功
     */
    public boolean updateProduct(Product product) {
        try {
            return productDAO.updateProduct(product);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 删除产品
     * @param id 产品ID
     * @return boolean 删除是否成功
     */
    public boolean deleteProduct(int id) {
        try {
            return productDAO.deleteProduct(id);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 根据ID更新产品
     * @param id 产品ID
     * @return boolean 产品对象
     */
    public boolean updateProductById(int id, Product product) {
        try{
            Optional<Product> nowProduct = productDAO.queryProductById(id);
            if(nowProduct.isPresent()){
                Product product1 = nowProduct.get();
                product1.setId(product.getId());
                product1.setName(product.getName());
                product1.setPrice(product.getPrice());
                product1.setStock(product.getStock());
                product1.setCas(product.getCas());
                product1.setFormula(product.getFormula());
                product1.setCategory(product.getCategory());
                product1.setStructurePictureAddress(product.getStructurePictureAddress());
                return productDAO.updateProductById(product1);
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 获取所有产品
     * @return List<Product> 产品列表
     * @throws SQLException 数据库查询异常
     */
    public List<Product> getAllProducts() throws SQLException{
        ResultSet resultSet = productDAO.getAllProducts();
        List<Product> products = new ArrayList<>();
        while(resultSet.next()){
            Product product = new Product();
            product.setId(resultSet.getInt("id"));
            product.setName(resultSet.getString("name"));
            product.setPrice(resultSet.getDouble("price"));
            product.setStock(resultSet.getInt("stock"));
            product.setCas(resultSet.getString("cas"));
            product.setFormula(resultSet.getString("formula"));
            product.setCategory(resultSet.getString("category"));
            product.setStructurePictureAddress(resultSet.getString("structurePictureAddress"));
            products.add(product);
        }
        return products;
    }
}
