package com.lmucassi.app.fix;

public class MessFixer {

    public static String fixMess(String brokerID, int stock, String market, String inst, double price, double quant){
        String fixMess = brokerID + "|" + "8=fix.4.4|9=len|";
        fixMess = fixMess.concat("49=" + brokerID + "|");
        fixMess = fixMess.concat("35=" + stock + "|");
        fixMess = fixMess.concat("56=" + market + "|");
        fixMess = fixMess.concat("55=" + inst + "|");
        fixMess = fixMess.concat("44=" + price + "|");
        fixMess = fixMess.concat("38=" + quant + "|");
        return fixMess;
    }
}
