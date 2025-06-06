package com.ascent.bean;

public class PropertyEmpty extends RuntimeException {
    private final String propertyName;

    public PropertyEmpty(String propertyName) {
        super("配置项为空: " + propertyName);
        this.propertyName = propertyName;
    }

    public String getPropertyName() {
        return propertyName;
    }
}
