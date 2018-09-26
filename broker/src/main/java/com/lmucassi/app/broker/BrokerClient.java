package com.lmucassi.app.broker;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class BrokerClient {
    public void startClient() throws IOException, InterruptedException {

        InetSocketAddress hostAddress = new InetSocketAddress("localhost", 9093);
        SocketChannel client = SocketChannel.open(hostAddress);

        System.out.println("Broker Client... started");

        String threadName = Thread.currentThread().getName();

        // Send messages to server
        String[] messages = new String[] { threadName + ": msg1 from Broker", threadName + ": msg2 from Broker", threadName + ": msg3 from Broker" };

        for (int i = 0; i < messages.length; i++) {
            ByteBuffer buffer = ByteBuffer.allocate(74);
            buffer.put(messages[i].getBytes());
            buffer.flip();
            client.write(buffer);
            System.out.println(messages[i] + " :From Broker");
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
                    new BrokerClient().startClient();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        };
        new Thread(client, "Broker client-A").start();
        new Thread(client, "Broker client-B").start();
    }
}
