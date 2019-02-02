package com.lmucassi.app.util;

import com.lmucassi.app.router.RoutingTable;
import com.lmucassi.app.util.Validation.*;
import lombok.Getter;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

@Getter
public class Server {
    private Selector selector;
    private InetSocketAddress listenAddress;

    private ByteBuffer buffer;
    private Socket socket;
    private ServerSocketChannel serverChannel;
    private Calendar cal = Calendar.getInstance();
    private SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");

    /**
     * Initializing the server
     *
     * @throws IOException
     */
    public Server() throws IOException {
        this.selector = Selector.open();
    }

    /**
     * Checks through the keys for when the incoming key is
     * Valid, Acceptable, Readable, Writable
     *
     * @throws IOException
     */
    public void startServer(List<RoutingTable> routingTables) throws Exception {

        System.out.println("\t\t\t \033[34m[ ROUTER ] \033[0m");


        int[] ports = {5000, 5001};

        for (int port : ports) {

            ServerSocketChannel server = ServerSocketChannel.open();
            server.configureBlocking(false);
            server.socket().bind(new InetSocketAddress(port));
            server.register(selector, SelectionKey.OP_ACCEPT);
            if (port == 5001){
                System.out.println("\033[34m [ " + sdf.format(cal.getTime()) +
                        " ] [SERVER]: Listening to MARKET @5001\033[0m");
            }
            else if (port == 5000){
                System.out.println("\033[34m [ " + sdf.format(cal.getTime()) +
                        " ] [SERVER]: Listening to BROKER @5000\033[0m");
            }
        }

        while (true) {
            if ( this.selector.select() == 0)
                continue;
            Set<SelectionKey> readyKeys = selector.selectedKeys();
            Iterator iterator = readyKeys.iterator();
            while (iterator.hasNext()) {
                SelectionKey key = (SelectionKey) iterator.next();
                iterator.remove();
                if (!key.isValid())
                    continue;
                if (key.isAcceptable())
                    this.accept(key, routingTables);
                if (key.isValid() && key.isReadable())
                    this.read(key, routingTables);
            }
        }
    }

    /**
     * The server creates the socket between client and the server
     * Sets the socket to non-blocking
     * Sends the client a unique id
     *
     * @param key
     * @throws IOException
     */

    private void accept(SelectionKey key, List<RoutingTable> routingTables) throws Exception {
        ServerSocketChannel serverChannel = (ServerSocketChannel) key.channel();
        SocketChannel channel = serverChannel.accept();
        channel.configureBlocking(false);
        this.socket = channel.socket();
        SocketAddress remoteAddr = socket.getRemoteSocketAddress();
        System.out.println("\033[34m [ " + sdf.format(cal.getTime()) +
                " ] [SERVER]: Listening on port " + socket.getLocalPort() + "\033[0m");
        String clientId;
        if (socket.getLocalPort() == 5001)
            clientId = "MK00001";
        else
            clientId =  IDGenerator.getIdGenerator().generateId(socket.getLocalPort());
        routingTables.add(new RoutingTable( clientId, channel));
        this.useSocketToWrite(channel, clientId);
    }

    /**
     * The server gets the socket from the key
     * Checks whether the is something to read, if so prints it out.
     * (here more works needs to be done to the message.--> Validate and so forth)
     * Sets the option on the selector to write
     *
     * @param key
     * @throws IOException
     */

    private void read(SelectionKey key, List<RoutingTable> routingTable) throws Exception {
        SocketChannel channel = (SocketChannel) key.channel();
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        int numRead = -1;
        numRead = channel.read(buffer);

        if (numRead == -1) {
            Socket socket = channel.socket();
            SocketAddress remoteAddr = socket.getRemoteSocketAddress();
            System.out.println("\033[33mConnection closed by client: " + remoteAddr + "\033[0m");
            channel.close();
            key.cancel();
            return;
        }
        byte[] data = new byte[numRead];
        System.arraycopy(buffer.array(), 0, data, 0, numRead);
        String msg = new String(data);
        SocketChannel marketChannel = validation(msg, routingTable);
        if (marketChannel == null) {
            System.out.println("\033[34m [ " + sdf.format(cal.getTime())
                    + " ] [SERVER]: No MARKET Connection found\033[0m");
        }
        else {
            if (socket.getLocalPort() == 5000)
                System.out.println("\033[34m [ " + sdf.format(cal.getTime())
                        + " ] [SERVER]: Valid message from Broker\033[0m");
            else if (socket.getLocalPort() == 5001)
                System.out.println("\033[34m [ " + sdf.format(cal.getTime())
                        + " ] [SERVER]: Valid message from Market\033[0m");
            this.useSocketToWrite(marketChannel, msg);
        }

        System.out.println("\033[34m [ " + sdf.format(cal.getTime()) + " ] [SERVER]: Got " + msg + "\033[0m");
        channel.register(this.selector, SelectionKey.OP_WRITE);
    }

    private SocketChannel validation(String msg, List<RoutingTable> routingTable){
        MessageValidationHandler chain1 = new CheckSumValidator();
        MessageValidationHandler chain2 = new DestinationVerification(routingTable);
        MessageValidationHandler chain3 = new MessageForwarding();

        chain1.setNextHandler(chain2);
        chain2.setNextHandler(chain3);

        FixMessageValidator request = new FixMessageValidator(msg);

        if (chain1.validateMessage(request)){
            return ((DestinationVerification) chain2).getChannel();
        }
        return null;
    }

    /**
     * Writes to client
     * Sets the option on the selector to read
     *
     * @param channel
     * @param message
     * @throws Exception
     */

    private void useSocketToWrite (SocketChannel channel, String message) throws Exception {
        this.buffer = ByteBuffer.allocate(1024);
        this.buffer.put(message.getBytes());
        this.buffer.flip();
        channel.write(buffer);
        this.buffer.clear();
        channel.register(this.selector, SelectionKey.OP_READ);
    }
}
