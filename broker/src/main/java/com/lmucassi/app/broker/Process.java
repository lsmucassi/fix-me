package com.lmucassi.app.broker;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

@Setter
@Getter
public class Process {
    String ID;
    String _inst;
    int _quant;
    String _markt;
    int _price;

    Scanner getFxMess = new Scanner(System.in);
    Map<String, String> fxMess = new HashMap<String, String>();

    public Process(/*String inst, int quant, String markt, int price*/) {
//        this._inst = inst;
//        this._quant = quant;
//        this._markt = markt;
//        this._price = price;
    }

    public void setGetFxMess() {
        String mess;
        System.out.print("Enter Instrument: ");
        //setInst(mess = getFxMess.nextLine());
        System.out.println();

        System.out.print("Enter Quantity: ");
        //setQuant(mess = getFxMess.nextLine());
        System.out.println();

        System.out.print("Enter Market: ");
        //setMarket(mess = getFxMess.nextLine());
        System.out.println();

        System.out.print("Enter Price: ");
        //setPrice(mess = getFxMess.nextLine());
        System.out.println();
    }

    public void showFXMess() {
        fxMess.put("Instrument", this._inst);
        fxMess.put("Quantity", Integer.toString(this._quant));
        fxMess.put("Market", this._markt);
        fxMess.put("Price", Integer.toString(this._price));

        System.out.println("using entrySet and toString");
        for (Map.Entry<String, String> entry : fxMess.entrySet()) {
            System.out.println(entry);
        }
        System.out.println();
    }
}
