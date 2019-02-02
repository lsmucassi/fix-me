package com.lmucassi.app.router;

import lombok.Getter;

import java.nio.channels.SocketChannel;

@Getter
public class RoutingTable {

    private String    id;
    private SocketChannel channel;

    public RoutingTable(String id, SocketChannel channel){
        this.id = id;
        this.channel = channel;

    }
}
