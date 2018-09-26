package com.lmucassi.app.market;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;


public class MarketClient {
    public void startClient() throws IOException, InterruptedException {

        InetSocketAddress hostAddress = new InetSocketAddress("localhost", 9093);
        SocketChannel client = SocketChannel.open(hostAddress);

        System.out.println("Market Client... started");

        String threadName = Thread.currentThread().getName();

        // Send messages to server
        String[] messages = new String[] { threadName + ": msg1 from Market", threadName + ": msg2 from Market", threadName + ": msg3 from Market" };

        for (int i = 0; i < messages.length; i++) {
            ByteBuffer buffer = ByteBuffer.allocate(74);
            buffer.put(messages[i].getBytes());
            buffer.flip();
            client.write(buffer);
            System.out.println(messages[i] + " :From Market");
            buffer.clear();
            Thread.sleep(5000);
        }
        client.close();
    }

    public static void main(String[] args) {
        Runnable client = new Runnable() {
            @Override
            public void run() {
                try {
                    new MarketClient().startClient();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        };
        new Thread(client, "Market client-A").start();
        new Thread(client, "Market client-B").start();
    }

}
