package com.lmucassi.app.broker;

import java.util.HashMap;
import java.util.Map;

public class Process {
    private String _inst;
    private int _quant;
    private String _markt;
    private int _price;
    Map<String, String> fxMess = new HashMap<String, String>();

    public Process(String inst, int quant, String markt, int price) {
        this._inst = inst;
        this._quant = quant;
        this._markt = markt;
        this._price = price;
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
