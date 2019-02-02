package com.lmucassi.app.broker;

import com.lmucassi.app.util.GetFile;
import java.util.Scanner;

public class Broker {

    private static String market;
    private static String instr = null;
    private static double quant;
    private static int trade;
    private static double price;

    public static String setInstrument(){
        try {
            Scanner scanner = new Scanner(System.in);

            while (scanner.hasNextLine()){
                String option = scanner.nextLine().trim();

                if (option.equals("Mineral")) {
                    instr = "Mineral";
                    break;
                }
                else if (option.equals("Oil")) {
                    instr = "Oil";
                    break;
                }
                else if (option.equals("Diamond")) {
                    instr = "Diamond";
                    break;
                }
                else if (option.equals("Gold")) {
                    instr = "Gold";
                    break;
                }
                else
                    System.out.println("\033[31;1mError: Invalid Input [enter name of instrument] \n. Try again.\033[m");
            }

        }catch (Exception ex){
            System.out.println("\033[31;1mError: Invalid Input, Please enter valid Instrument name.\033[m");
            setInstrument();
        }
        return instr;
    }

    public static double setQuantity(){

        try {
            Scanner sc = new Scanner(System.in);
            quant = sc.nextInt();
        }catch (Exception e){
            System.out.println("\033[31;1mError: Invalid Input, Please enter valid Quantity.\033[m");
            setQuantity();
        }
        return quant;
    }

    public static int setTrade(){
        try {
            Scanner sc = new Scanner(System.in);

            while (sc.hasNextLine()) {
                trade = sc.nextInt();

                if (trade > 2 || trade < 1)
                    System.out.println("\033[31;1mError: Invalid Input. Try again.\033[m");
                else
                    break;
            }
        }catch (Exception e){
            System.out.println("\033[31;1mError: Invalid Input, Please enter a valid corresponding market Index ID.\033[m");
            setTrade();
        }

        return trade;
    }

    public static String setMarket(){
        try {
            Scanner sc = new Scanner(System.in);

            while (sc.hasNextLine()){
                int option = sc.nextInt();

                if (option == 1) {
                    market = "Good Stocks";
                    break;
                }
                else if (option == 2) {
                    System.out.println("------------------------------------------------------------------------------------");
                    System.out.println("\033[34m $ Thank you for dealing with us \033[0m");
                    System.out.println("\033[34m $ Feel free to come back again\033[0m");
                    System.out.println("------------------------------------------------------------------------------------");
                    System.exit(0);
                }
                else
                    System.out.println("\033[31;1mError: Invalid Input. Try again.\033[m");
            }
        }catch (Exception e){
            System.out.println("\033[31;1mError: Invalid Input, Please enter valid corresponding market Index ID.\033[m");
            setMarket();
        }
        return market;
    }

    public static double sellQuantity(String instrument){
        try {
            Scanner sc = new Scanner(System.in);

            while (sc.hasNextLine()){
                quant = sc.nextInt();
                double sellQuantity = 0;

                String[] data = GetFile.getLine("assets.txt");
                String[] items = data[0].split(",");

                for (String item : items){
                    if (item.contains(instrument)){
                        sellQuantity = Double.parseDouble(item.split(" ")[1]);
                    }
                }

                if (quant < sellQuantity || quant == sellQuantity){
                    break;
                }
                else
                    System.out.println("\033[31;1mError: Invalid Input, Please enter valid Quantity.\033[m");
            }


        }catch (Exception e){
            System.out.println("\033[31;1mError: Invalid Input, Please enter valid Quantity.\033[m");
            sellQuantity(instrument);
        }
        return quant;
    }

    public static double sellPrice(){
        try {
            Scanner sc = new Scanner(System.in);

            while (sc.hasNextLine()){
                price = sc.nextDouble();

                if (price > 0)
                    break;
                else
                    System.out.println("\033[31;1mError: Invalid Input. Try again.\033[m");
            }
        }catch (Exception e){
            System.out.println("\033[31;1mError: Invalid Input, Please enter valid corresponding market Index ID.\033[m");
            setMarket();
        }
        return price;
    }
}
