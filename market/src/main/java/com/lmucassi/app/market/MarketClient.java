package com.lmucassi.app.market;

import lombok.Getter;
import com.lmucassi.app.util.WriteToFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Set;

@Getter
public class MarketClient {
    private String name;
    private ArrayList<Asset> assets;
    private Selector selector;
    private InetSocketAddress hostAddress;
    private SocketChannel client;
    private ByteBuffer buffer;
    private  String messages, fixMessage;
    private BufferedReader bufferedReader;
    private boolean idFlag;
    private String clientID;
    private Calendar cal = Calendar.getInstance();
    private SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");

    public MarketClient(String name, Asset mineral, Asset stock1, Asset stock2, Asset stock3) {
        this.name = name;
        this.assets = new ArrayList<>();
        this.assets.add(stock1);
        this.assets.add(stock2);
        this.assets.add(stock3);
        this.idFlag = false;
        this.socketSetUp();
    }

    private void socketSetUp () {
        try {
            this.selector = Selector.open();
            this.hostAddress = new InetSocketAddress("localhost", 5001);
            this.client = SocketChannel.open(this.hostAddress);
            this.client.configureBlocking(false);

            int operations = SelectionKey.OP_CONNECT | SelectionKey.OP_READ;
            this.client.register(this.selector, operations);
            this.bufferedReader = new BufferedReader(new InputStreamReader(System.in));
            this.buffer = ByteBuffer.allocate(1024);
        }
        catch (IOException e) {
            System.out.println("\033[31mError: error strating the MARKET SERVER\033[0m");
            try {
                this.stop();
            }
            catch (IOException i) {
                System.out.println("\033[31mError: error starting the MARKET SERVER\033[0m");
            }
        }
    }

    public void startClient() throws Exception {


        System.out.println("\t\t\t \033[34m[ MARKET ] \033[0m");
        System.out.println("------------------------------------------------------------------------------------");
        System.out.println("\033[34m [ " + sdf.format(cal.getTime()) +
                " ] [ MARKET ]: Connected to Router Server\033[0m");
        System.out.println("------------------------------------------------------------------------------------");

        while (true){
            if (this.selector.select() == 0)
                continue;
            Set<SelectionKey> selectionKeys = this.selector.selectedKeys();
            Iterator<SelectionKey> iterator = selectionKeys.iterator();
            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();
                iterator.remove();
                if (!key.isValid())
                    continue;
                if (key.isConnectable()) {
                    boolean connected = processConnect(key);
                    if (!connected)
                        stop();
                }
                if (key.isReadable())
                    this.read();

                if (key.isWritable())
                    this.writeToClient();
            }
        }
    }

    private static boolean processConnect(SelectionKey key) throws Exception{
        SocketChannel channel = (SocketChannel) key.channel();
        while (channel.isConnectionPending()) {
            channel.finishConnect();
        }
        return true;
    }

    private void read () throws  Exception {
        client.read(buffer);
        messages = new String(buffer.array()).trim();
        if (messages.equals("MK00001"))
            System.out.println("\033[34m [ " + sdf.format(cal.getTime()) +
                    " ] [ MARKET ]:\t MARKET ID  "+ messages + "\033[0m");
        else
            System.out.println("\033[34m [ " + sdf.format(cal.getTime()) +
                    " ] [ MARKET ]:\t FixMessage from Broker "+ messages);
        if (!this.idFlag){
            this.clientID = messages;
            this.client.register(this.selector, SelectionKey.OP_READ);
            this.idFlag = true;
        }
        else {
            if (this.processMessage(messages)) {
                System.out.println("\033[34m [ " + sdf.format(cal.getTime()) + " ] [MARKET]:\t Buy is valid\033[0m");
                fixMessage = fixMessageGenerator(true);
                System.out.println("\033[34m [ " +sdf.format(cal.getTime()) +
                        " ] [MARKET]:\t Broker message: " + fixMessage + "\033[0m");
            }
            else {
                System.out.println("\033[34m [ " + sdf.format(cal.getTime()) +
                        " ] [MARKET]:\t Buy is not valid\033[0m");
                fixMessage = fixMessageGenerator(false);
                System.out.println("\033[34m [" + sdf.format(cal.getTime()) +
                        " [MARKET]:\t Broker message: " + fixMessage + "\033[0m");
            }
            this.client.register(this.selector, SelectionKey.OP_WRITE );
        }
        buffer.clear();
    }

    private boolean processMessage (String message) {
        String[] splitMessage = message.split("\\|");
        String instrument = splitMessage[6].split("=")[1];
        int buysell = Integer.parseInt(splitMessage[3].split("=")[1]);
        double quantity = Double.parseDouble(splitMessage[8].split("=")[1]);
        double price = Double.parseDouble(splitMessage[7].split("=")[1]);
        boolean quantityCheck = false;
        if (buysell == 1){
            for (Asset asset: this.assets) {
                if (!asset.getName().equals(instrument))
                    continue;
                quantityCheck = asset.buyAsset(quantity);
                System.out.println("asset: " + asset.getTotalAmount());
                WriteToFile.updateFile(instrument, quantity, 1, asset.getPrice(), "source.txt");
            }
        }
        else if (buysell == 2){
            for (Asset asset: this.assets) {
                if (!asset.getName().equals(instrument))
                    continue;
                quantityCheck = asset.sellAsset(quantity, price);
                System.out.println("asset: " + asset.getTotalAmount());
                WriteToFile.updateFile(instrument, quantity, 2, asset.getPrice(), "source.txt");
            }
        }
        return quantityCheck;
    }

    private String fixMessageGenerator(boolean check){
        String[] tags =  messages.split("\\|");
        String fixMessage = "";

        if (check == true){
            fixMessage = "YayYay|" + tags[1] + "|" + tags[2] + "|" + "35=Executed|" +
                        "49=YayYay" + "|" + "56=" + tags[0] + "|" + tags[6] + "|" + "44=0|" + tags[8] + "|";
            fixMessage = fixMessage + "10=" + checkSumCalculator(fixMessage);
        }
        else if (check == false){
            fixMessage = "YayYay|" + tags[1] + "|" + tags[2] + "|" + "35=Rejected|" +
                    "49=YayYay" + "|" + "56=" + tags[0] + "|" + tags[6] + "|" + "44=0|" + tags[8] + "|";
            fixMessage = fixMessage + "10=" + checkSumCalculator(fixMessage);
        }
        return fixMessage;
    }

    private String checkSumCalculator(String message){
        String checkSum;
        String checkSumMessage = message.replace('|', '\u0001');
        byte[] messageBytes = checkSumMessage.getBytes(StandardCharsets.US_ASCII);
        int total = 0;

        for (int i = 0; i < message.length(); i++)
            total += messageBytes[i];

        int CalculatedChecksum = total % 256;
        checkSum = Integer.toString(CalculatedChecksum - 1) ;

        return checkSum;
    }

    /**
     *
     * Writes to the client via the buffer
     * Sets the option on the selector to read
     *
     * @throws Exception thrown when fails to write to buffer
     */

    public void writeToClient() throws Exception {
        this.buffer = ByteBuffer.allocate(1024);
        this.buffer.put(fixMessage.getBytes());
        this.buffer.flip();
        client.write(this.buffer);
        this.buffer.clear();
        this.client.register(this.selector, SelectionKey.OP_READ);
        System.out.println("\033[34m [ " + sdf.format(cal.getTime()) +
                " ] [Market]:\t Fix Message sent to Router for Broker\033[0m");
    }

    private void stop() throws IOException {
        this.client.close();
        this.buffer = null;
    }

    public static void main(String[] args) {
        MarketClient market = new MarketClient("Good Stocks ",
                new Asset("Mineral", 78976.0, 1208.0),
                new Asset("Oil", 78565.0, 909.0) ,
                new Asset("Diamond", 74763.0, 1889.0),
                new Asset("Gold", 74563.0, 1881.0));

        try {
            market.startClient();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
