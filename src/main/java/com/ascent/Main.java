package com.ascent;

import com.ascent.client.ServerConnection;

public class main {
    public static void main(String[] args) {
        ServerConnection server = new ServerConnection();
        server.start();
    }
}
