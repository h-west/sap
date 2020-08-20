package io.hsjang.saptest.tester;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lombok.Data;

@Data
public class TradeLog {
    Date dt;
    long balance;
    List<String> logs = new ArrayList<String>();

    public void add(List<String> logs){
        this.logs.addAll(logs);
    }

    public void add(String log){
        this.logs.add(log);
    }
}