package com.lmucassi.app.router;

import com.lmucassi.app.util.Server;
import lombok.Getter;

import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;

@Getter
public class RouterSever {

    private List<RoutingTable> routingTable ;
    private Server server;

    public RouterSever(){
        this.routingTable = new ArrayList<>();
        try {
            this.server = new Server();
            this.server.startServer(this.routingTable);
            this.addToRoutingTable("000000", null);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addToRoutingTable(String id, SocketChannel channel){

        this.routingTable.add(new RoutingTable(id, channel));
    }

    public static void main(String[] args) {
        new RouterSever();

    }
}
