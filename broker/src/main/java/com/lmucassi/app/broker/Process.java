package com.lmucassi.app.broker;

import java.util.HashMap;
import java.util.Map;

public class Process {
    public Process() {

    }

    public void getFXMess(String inst, String quant, String markt, String price){
        Map<String, String> fxMess = new HashMap<String, String>();
        fxMess.put("Instrument", inst);
        fxMess.put("Quantity", quant);
        fxMess.put("Market", markt);
        fxMess.put("Price", price);

    }


}
