package com.lmucassi.app.broker;

import java.awt.*;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Set;


public class BrokerClient {

    Process getMes ;
    private InetSocketAddress hostAddress;
    private SocketChannel client;
    private Selector selector;
    private ByteBuffer buffer;

    public BrokerClient() throws IOException{
        int operations = SelectionKey.OP_CONNECT | SelectionKey.OP_WRITE;
        this.hostAddress =  new InetSocketAddress("localhost",

                9093);
        this.selector = Selector.open();
        this.client  = SocketChannel.open(hostAddress);
        this.client.configureBlocking(false);
        this.client.register(this.selector, operations);


        this.buffer = ByteBuffer.allocate(1024);
    }

    private void stop() throws IOException {
        this.client.close();
        this.buffer = null;
    }

    private boolean processConnect(SelectionKey key) throws IOException{
        SocketChannel channel = (SocketChannel) key.channel();
        while (channel.isConnectionPending()) {
            channel.finishConnect();
        }
        return true;
    }

    public void writer(String messages) throws IOException {
        buffer.put(messages.getBytes());
        buffer.flip();
        client.write(buffer);
        System.out.println(messages + " :From Broker");
        buffer.clear();
        client.register(this.selector, SelectionKey.OP_READ);
    }

    // read from the socket channel
    private void read(SelectionKey key) throws IOException {
        SocketChannel channel = (SocketChannel) key.channel();
        int numRead = -1;
        numRead = channel.read(buffer);

        if (numRead == -1) {
            Socket socket = channel.socket();
            SocketAddress remoteAddr = socket.getRemoteSocketAddress();
            System.out.println("Connection closed by Sever client: " + remoteAddr);
            channel.close();
            key.cancel();
            return;
        }

        byte[] data = new byte[numRead];
        System.arraycopy(buffer.array(), 0, data, 0, numRead);
        System.out.println("Got: " + new String(data));
        channel.register(this.selector, SelectionKey.OP_WRITE);
    }

    public void startClient() throws IOException {
        System.out.println("Broker Client... started");

        while (true){

            if (this.selector.select() == 0) {

                continue;
            }

            Set<SelectionKey> readyKeys = selector.selectedKeys();
            Iterator iterator = readyKeys.iterator();
//            if(brokerFlag)
//                Broker();
            while (iterator.hasNext()) {
                SelectionKey key = (SelectionKey) iterator.next();
                // Remove key from set so we don't process it twice
                iterator.remove();

                if (!key.isValid()) {
                    continue;
                }
                if (key.isConnectable()) {
                    boolean connected = processConnect(key);
                    if (!connected)
                        stop();
                }
                if (key.isReadable()) { // Read from client
                    this.read(key);
                } else if (key.isWritable()) {
                    // write data to client...
                    System.out.println("getting broker message :");
                    getMes.setGetFxMess();
                    this.writer("written by broker");

                }
            }
        }
        //client.close();
    }

    public static void main(String[] args) throws IOException{

        BrokerClient client = new BrokerClient();
        client.startClient();
    }
}
