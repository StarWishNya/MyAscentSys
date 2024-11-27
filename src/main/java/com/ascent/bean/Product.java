package com.ascent.bean;

import com.alibaba.fastjson.JSON;
public class Product {
    private int id;
    private String name;//产品名
    private double price;//价格
    private int stock;//库存
    private String cas;//CAS号
    private String formula;//分子式
    private String category;//类别
    private String structurePictureAddress;//结构式图片地址

    public Product() {
    }

    public Product(int id, String name, double price, int stock, String cas, String formula, String category, String structurePictureAddress) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.stock = stock;
        this.cas = cas;
        this.formula = formula;
        this.category = category;
        this.structurePictureAddress = structurePictureAddress;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public String getCas() {
        return cas;
    }

    public void setCas(String cas) {
        this.cas = cas;
    }

    public String getFormula() {
        return formula;
    }

    public void setFormula(String formula) {
        this.formula = formula;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getStructurePictureAddress() {
        return structurePictureAddress;
    }

    public void setStructurePictureAddress(String structurePictureAddress) {
        this.structurePictureAddress = structurePictureAddress;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }

}
