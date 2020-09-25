package io.hsjang.saptest;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lombok.Data;

@Data
public class TestResult implements Serializable{

    private static final long serialVersionUID = 1L;

    Date start;
    Date end;
    Long money1;
    Long money2;
    List<String> trades;

    public TestResult(){}

    public TestResult(Date start, Date end, Long money1, Long money2){
        this.start = start;
        this.end = end;
        this.money1 = money1;
        this.money2 = money2;
        this.trades = new ArrayList<String>();
    }

    public TestResult addTrade(String trade){
        this.trades.add(trade);
        return this;
    }
}