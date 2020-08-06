package io.hsjang.saptest;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TestResult {

    Date start;
    Date end;
    Long money1;
    Long money2;
    List<String> trades;

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