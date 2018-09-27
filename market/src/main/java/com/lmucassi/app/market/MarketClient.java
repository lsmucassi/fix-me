package com.lmucassi.app.market;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;


public class MarketClient {

    private InetSocketAddress hostAddress;
    private SocketChannel client;
    private Selector selector;
    private ByteBuffer buffer;

    public MarketClient() throws IOException {
        int operations = SelectionKey.OP_CONNECT | SelectionKey.OP_WRITE
        this.hostAddress  = new InetSocketAddress("localhost", 5000);
        this.selector = Selector.open()
        this.client = SocketChannel.open(hostAddress);
        this.client.configureBlocking(false);
        this.client.register(this.selector, operations);
        this.buffer = ByteBuffer.allocate(1024);
    }

    private void stop() throws IOException {
        this.client.close();
        this.buffer = null;
    }

    private boolean processConnection(SelectionKey key) throws IOException {
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
        System.out.println(messages + " :To Server");
        buffer.clear();
        client.register(this.selector, SelectionKey.OP_READ);
    }

    private void read(SelectionKey key) throws IOException {
        SocketChannel channel = (SocketChannel) key.channel();
        int numRead = -1;
        numRead = channel.read(buffer);

        if (numRead == -1) {
            Socket socket = channel.socket();
            SocketAddress remoteAddr = socket.getRemoteSocketAddress();
            System.out.println("Connection closed: [ Market client: " + remoteAddr + " ]");
            channel.close();
            key.cancel();
            return;
        }
    }

    public void startClient() throws IOException, InterruptedException {
        System.out.println("Market Client... started");

        while (true) {
            if (this.selector.select() == 0)
                continue;

            Set<SelectionKey> readyKeys = selector.selectedKeys();
            Iterator iter = readyKeys.iterator();
            while (iter.hasNext()) {
                SelectionKey key = (SelectionKey) iter.next();

            }
        }

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
//        client.close();
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        MarketClient client  = new MarketClient();
        client.startClient();
    }

}
