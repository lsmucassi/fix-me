package com.lmucassi.app.market;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Process {
    String ID;
    String inst;
    int quant;
    String markt;
    int price;

    public Process() {

    }
}
