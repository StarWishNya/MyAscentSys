package com.ascent.dao;

import com.ascent.bean.Product;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class ProductDAO {
    /**
     * 根据产品ID查询产品
     * @param id 产品ID
     * @return Optional<Product>
     */
    public Optional<Product> queryProductById(int id){
        String query = "SELECT * FROM product_tb WHERE id = ?";
        try(Connection connection = DBConnection.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(query)){

            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();

            if(resultSet.next()){
                Product product = new Product();
                product.setId(resultSet.getInt("id"));
                product.setName(resultSet.getString("name"));
                product.setPrice(resultSet.getDouble("price"));
                product.setStock(resultSet.getInt("stock"));
                product.setCas(resultSet.getString("cas"));
                product.setFormula(resultSet.getString("formula"));
                product.setCategory(resultSet.getString("category"));
                product.setStructurePictureAddress(resultSet.getString("structurePictureAddress"));
                return Optional.of(product);
            }

        }catch (SQLException e){
            e.printStackTrace();
        }
        return Optional.empty();
    }

    /**
     * 根据产品名查询产品
     * @param name 产品名
     * @return Optional<Product>
     */
    public Optional<Product> queryProductByName(String name) {
        String query = "SELECT * FROM product_tb WHERE name = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, name);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                Product product = new Product();
                product.setId(resultSet.getInt("id"));
                product.setName(resultSet.getString("name"));
                product.setPrice(resultSet.getDouble("price"));
                product.setStock(resultSet.getInt("stock"));
                product.setCas(resultSet.getString("cas"));
                product.setFormula(resultSet.getString("formula"));
                product.setCategory(resultSet.getString("category"));
                product.setStructurePictureAddress(resultSet.getString("structurePictureAddress"));
                return Optional.of(product);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }
    /**
     * 插入新产品
     * @param product 要插入的产品对象
     * @return boolean 插入是否成功
     */
    public boolean insertProduct(Product product) throws SQLException {
        String query = "INSERT INTO product_tb (name, price, stock, cas, formula, category, structurePictureAddress) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, product.getName());
            preparedStatement.setDouble(2, product.getPrice());
            preparedStatement.setInt(3, product.getStock());
            preparedStatement.setString(4, product.getCas());
            preparedStatement.setString(5, product.getFormula());
            preparedStatement.setString(6, product.getCategory());
            preparedStatement.setString(7, product.getStructurePictureAddress());
            return preparedStatement.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * 更新产品信息
     * @param product 要更新的产品对象
     * @return boolean 更新是否成功
     */
    public boolean updateProduct(Product product) throws SQLException {
        String query = "UPDATE product_tb SET price = ?, stock = ?, cas = ?, formula = ?, category = ?, structurePictureAddress = ? WHERE name = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setDouble(1, product.getPrice());
            preparedStatement.setInt(2, product.getStock());
            preparedStatement.setString(3, product.getCas());
            preparedStatement.setString(4, product.getFormula());
            preparedStatement.setString(5, product.getCategory());
            preparedStatement.setString(6, product.getStructurePictureAddress());
            preparedStatement.setString(7, product.getName());
            return preparedStatement.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 删除产品
     * @param id 要删除的产品的ID
     * @return boolean 删除是否成功
     */
    public boolean deleteProduct(int id) throws SQLException {
        String query = "DELETE FROM product_tb WHERE id = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, id);
            return preparedStatement.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 根据ID更新产品信息
     * @param product 要更新的产品对象
     * @return boolean 更新是否成功
     */
    public boolean updateProductById(Product product) throws SQLException {
        String query = "UPDATE product_tb SET name = ?, price = ?, stock = ?, cas = ?, formula = ?, category = ?, structurePictureAddress = ? WHERE id = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, product.getName());
            preparedStatement.setDouble(2, product.getPrice());
            preparedStatement.setInt(3, product.getStock());
            preparedStatement.setString(4, product.getCas());
            preparedStatement.setString(5, product.getFormula());
            preparedStatement.setString(6, product.getCategory());
            preparedStatement.setString(7, product.getStructurePictureAddress());
            preparedStatement.setInt(8, product.getId());
            return preparedStatement.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
