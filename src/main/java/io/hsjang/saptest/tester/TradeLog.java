package io.hsjang.saptest.tester;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class TradeLog {
    LocalDateTime dt;
    long balance;
    List<String> logs = new ArrayList<String>();

    String symbol;
    LocalDateTime buyingDt;
    LocalDateTime sellingDt;
    long buyingPrice;
    long sellingPrice;

    @Override
    public String toString(){
        return symbol+"["+Meta.getSymbolName(symbol)+"] " +buyingDt+"-"+sellingDt+"::"+buyingPrice+":"+sellingPrice;
    }


    public void add(List<String> logs){
        this.logs.addAll(logs);
    }

    public void add(String log){
        this.logs.add(log);
    }
}