package com.lmucassi.app.broker;

import com.lmucassi.app.util.GetFile;

public class BrokerView {

    public static void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    public static void startComm() {
        System.out.println("\033[34mMarket is waiting for communication ... \033[0m");
        System.out.println("------------------------------------------------------------------------------------");
        System.out.println("\t \033[34m - 1 - Continue  \033[0m");
        System.out.println("\t \033[31m - 2 : Exit \033[0m");
        System.out.println("------------------------------------------------------------------------------------");
        System.out.println("Select option: ");
    }

    public static void marketStock() {
        String[] markets = GetFile.getLine("data.txt");
        String line = markets[0];
        String[] items = line.split(",");

        System.out.println("\t\t\t \033[34m[ Broker ] \033[0m");
        System.out.println("\t\033[34mYou have these options from the market: \033[0m");
        System.out.println("------------------------------------------------------------------------------------");
        System.out.println("\t\033[34m 1. " + items[0].split(" ")[0] + "\t UNITS: " + items[0].split(" ")[1] +
                "\t\t PRICE: R" + items[0].split(" ")[2] + " each \033[0m");
        System.out.println("\t\033[34m 2. " + items[1].split(" ")[0] + "\t\t UNITS: " + items[1].split(" ")[1] +
                "\t PRICE: R" + items[1].split(" ")[2] + " each \033[0m");
        System.out.println("\t\033[34m 3. " + items[2].split(" ")[0] + "\t UNITS: " + items[2].split(" ")[1] +
                "\t PRICE: R" + items[2].split(" ")[2] + " each \033[0m");
        System.out.println("\t\033[34m 3. " + items[3].split(" ")[0] + "\t UNITS: " + items[3].split(" ")[1] +
                "\t PRICE: R" + items[3].split(" ")[2] + " each \033[0m");
        System.out.println("------------------------------------------------------------------------------------");
        System.out.println("Select option: ");
    }

    public static void getInstQuant() {
        System.out.println("\t\t\t \033[34m[ Broker ] \033[0m");
        System.out.println("------------------------------------------------------------------------------------");
        System.out.println("\t \033[34m Enter instrument quantity  \033[0m");
        System.out.println("------------------------------------------------------------------------------------");
        System.out.println("Select option: ");
    }

    public static void Stork() {
        System.out.println("\t\t\t \033[34m[ Broker ] \033[0m");
        System.out.println("Would you like to: ");
        System.out.println("------------------------------------------------------------------------------------");
        System.out.println("\t \033[34m - 1 - BUY  \033[0m");
        System.out.println("\t \033[31m - 2 : SELL \033[0m");
        System.out.println("------------------------------------------------------------------------------------");
        System.out.println("Select option: ");
    }

    public static void clientAssets() {
        String[] data = GetFile.getLine("assets.txt");
        String[] items = data[0].split(",");

        System.out.println("\t \033[31m - SELL \033[0m");
        int i = 1;
        System.out.println("------------------------------------------------------------------------------------");
        for (String item : items){
            System.out.println(i++ + ". " + item.split(" ")[0] + " " + item.split(" ")[1] + " units\n");
        }
        System.out.println("------------------------------------------------------------------------------------");
        System.out.println("Select option: ");
    }


    public static void marketAssets() {
        String[] markets = GetFile.getLine("source.txt");
        String line = markets[0];
        String[] items = line.split(",");

        System.out.println("\t\t\t \033[34m[ Broker ] \033[0m");
        System.out.println("\t\t\t \033[34mEnter price for your instrument \033[0m");
        System.out.println("------------------------------------------------------------------------------------");
        System.out.println("\033[34m1. " + items[0].split(" ")[0] + " R" + items[0].split(" ")[2] +
                " price for each unit\033[0m");
        System.out.println("\033[34m2. " + items[1].split(" ")[0] + " R" + items[1].split(" ")[2] +
                " price for each unit\033[0m");
        System.out.println("\033[34m3. " + items[2].split(" ")[0] + " R" + items[2].split(" ")[2] +
                " price for each unit\033[0m");
        System.out.println("\033[34m3. " + items[3].split(" ")[0] + " R" + items[3].split(" ")[2] +
                " price for each unit\033[0m");
        System.out.println("------------------------------------------------------------------------------------");
        System.out.println("Select option: ");

    }

}
