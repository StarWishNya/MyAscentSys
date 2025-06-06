package com.ascent;

import com.ascent.client.ServerConnection;

public class Main {
    public static void main(String[] args) {
        ServerConnection server = new ServerConnection();
        server.start();
    }
}
