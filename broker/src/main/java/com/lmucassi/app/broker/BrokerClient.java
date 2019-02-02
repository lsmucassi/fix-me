package com.lmucassi.app.broker;

import com.lmucassi.app.util.GetFile;
import com.lmucassi.app.fix.MessFixer;

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
import java.util.Calendar;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Set;

public class BrokerClient {

    private static Selector selector;
    private InetSocketAddress hostAddress;
    private static SocketChannel client;
    private ByteBuffer buffer;
    private  String messages;
    private static String fixMess;
    private BufferedReader bufferedReader;
    private static boolean idFlag;
    private boolean sentFlag;
    private static String clientID;
    private static String market;
    private static String instr;
    private static double price;
    private static double quant;
    private static int stock;

    private static Calendar cal = Calendar.getInstance();
    private static SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");


    public BrokerClient() {
        try {
            this.selector = Selector.open();
            this.hostAddress = new InetSocketAddress("localhost", 5000);
            this.client = SocketChannel.open(this.hostAddress);
            this.client.configureBlocking(false);
            int operations = SelectionKey.OP_CONNECT | SelectionKey.OP_READ;
            this.client.register(this.selector, operations);
            this.bufferedReader = new BufferedReader(new InputStreamReader(System.in));
            this.buffer = ByteBuffer.allocate(1024);

        }
        catch (IOException e) {
            System.out.println("\033[31;1mError: error starting the BROKER SERVER.\033[0m");
            try {
                this.stop();
            }
            catch (IOException i) {
                System.out.println("\033[31;1mError: Error: error starting the BROKER SERVER.\033[0m");
            }
        }
    }

    public void startClient() throws Exception {
        System.out.println("\033[34m BROKER SERVER - [  Starting....  ]  \033[0m");
        System.out.println("\033[34m BROKER SERVER - [  Started  ]  \033[0m");

        while (true){
            if (this.selector.select() == 0)
                continue;
            Set<SelectionKey> selectionKeys = this.selector.selectedKeys();
            Iterator<SelectionKey> iterator = selectionKeys.iterator();
            fixMessGen();

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
                if (key.isReadable()) {
                    this.read();
                    break;
                }
                if (key.isWritable())
                    this.writer();
            }
        }
    }


    private void read () throws  Exception {
        client.read(buffer);
        messages = new String(buffer.array()).trim();

        if (!idFlag) {
            System.out.println("\033[34m [ " + sdf.format(cal.getTime()) +
                    " ] : BROKER [ ID ] :\t\033[0m" + messages + "\n");
            clientID = messages;
            idFlag = true;
            client.register(selector, SelectionKey.OP_READ);
        }
        else{
            System.out.println(sdf.format(cal.getTime()) + " : BROKER - [ Market ] :\t " + messages + "\n");
            processMarket(messages);
            client.register(selector, SelectionKey.OP_READ);
        }
        buffer.clear();
        this.client.register(this.selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE );
    }

    private void processMarket(String messages) {
        String[] tags =  messages.split("\\|");

        for (String item : tags){
            if (item.equals("35=Executed")){
                if (stock == 1)
                    System.out.println("\033[34m" + sdf.format(cal.getTime()) +
                            " : Broker - processed.\n" +
                            " : Broker - executed \n" +
                            " : Request processed and exercuted\033[0m");
                else if (stock == 2)
                    System.out.println("\033[34m" + sdf.format(cal.getTime()) +
                                    " : Broker - processed.\n" +
                                    " : Broker - executed \n" +
                                    " : Request processed and exercuted\033[0m");
                String a = tags[6].split("=")[1];
                double b = Double.parseDouble(tags[8].split("=")[1]);
                GetFile.updateFile(a, b, stock, "assets.txt");
                break;
            }
            else if (item.equals("35=Rejected")){
                if (stock == 1)
                    System.out.println("\033[34m" + sdf.format(cal.getTime()) +
                            " : Broker - Buy rejected\033[0m");
                else if (stock == 2)
                    System.out.println("\033[34m" + sdf.format(cal.getTime()) +
                            " : Broker - Sell rejected\033[0m");
                break;
            }
        }
    }


    public void writer() throws Exception {
        if (!sentFlag){
            this.buffer = ByteBuffer.allocate(1024);
            this.buffer.put(fixMess.getBytes());
            this.buffer.flip();
            client.write(this.buffer);
            System.out.println("\033[34m [ " + sdf.format(cal.getTime()) +
                    " ]\t : Broker to Server -  Fix Message created \n Fix Message\t :\n " + fixMess + "\033[0m");
            this.buffer.clear();
            this.client.register(this.selector, SelectionKey.OP_READ);
            System.out.println("\033[34m [ " + sdf.format(cal.getTime()) +
                    " ]\t : Broker Fix Message sent to Server");
            sentFlag = true;
        }
    }

    private String checkSumCalc(String message){
        String checkSum;
        String checkSumMess = message.replace('|', '\u0001');
        byte[] messBytes = checkSumMess.getBytes(StandardCharsets.US_ASCII);
        int total = 0;

        for (int i = 0; i < message.length(); i++) {
            total += messBytes[i];
        }

        int CalChecksum = total % 256;
        checkSum = Integer.toString(CalChecksum - 1) ;

        return checkSum;
    }

    public void stop() throws IOException {
        this.client.close();
        this.buffer = null;
    }

    public static boolean processConnect(SelectionKey key) throws Exception{
        SocketChannel channel = (SocketChannel) key.channel();
        while (channel.isConnectionPending()) {
            channel.finishConnect();
        }
        return true;
    }

    public void fixMessGen() throws Exception {
        fixMess = MessFixer.fixMess(clientID, stock, market, instr, price, quant);
        fixMess = fixMess + "10=" + checkSumCalc(fixMess);
        client.register(selector, SelectionKey.OP_READ);
    }

    public static void main(String[] args){
        BrokerView.Stork();
        stock = Broker.setTrade();
        if (stock == 1){
            BrokerView.clearScreen();
            BrokerView.startComm();
            market = Broker.setMarket();
            BrokerView.clearScreen();
            BrokerView.marketStock();
            instr = Broker.setInstrument();
            BrokerView.clearScreen();
            BrokerView.getInstQuant();
            quant = Broker.setQuantity();

            if (instr.equals("Mineral"))
                price = 320008.2 * quant;
            else if (instr.equals("Oil"))
                price = 50000909.5 * quant;
            else if (instr.equals("Diamond"))
                price = 4130.0 * quant;
            else if (instr.equals("Gold"))
                price = 4130.0 * quant;
        }
        else if (stock == 2){

            BrokerView.clearScreen();
            BrokerView.startComm();
            market = Broker.setMarket();
            BrokerView.clientAssets();
            instr = Broker.setInstrument();
            BrokerView.clearScreen();
            BrokerView.getInstQuant();
            quant = Broker.sellQuantity(instr);
            BrokerView.clearScreen();
            BrokerView.marketAssets();
            price = Broker.sellPrice();
        }

        BrokerView.startComm();

        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextLine()) {
            String opt = scanner.nextLine();
            if (opt.equals("1")) {
                BrokerClient client = new BrokerClient();
                try {
                    client.startClient();
                } catch (Exception err) {
                    err.printStackTrace();
                }
            }
            else if (opt.equals("2")) {
                System.out.println("------------------------------------------------------------------------------------");
                System.out.println("\033[34m $ Thank you for dealing with us \033[0m");
                System.out.println("\033[34m $ Feel free to come back again\033[0m");
                System.out.println("------------------------------------------------------------------------------------");
                System.exit(0);
            }
        }
        scanner.close();
    }
}
