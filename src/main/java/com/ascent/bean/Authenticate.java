package com.ascent.bean;

import com.alibaba.fastjson.JSON;

public class Authenticate {
    private String username;
    private int authority;

    public Authenticate() {
    }

    public Authenticate(String username, int authority) {
        this.username = username;
        this.authority = authority;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getAuthority() {
        return authority;
    }

    public void setAuthority(int authority) {
        this.authority = authority;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }

}

