package io.hsjang.saptest.tester;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class DailyResult {

    LocalDateTime dt;
    long buyingPrice = 0L;
    long sellingPrice = 0L;
    double roi = 1;
    List<TradeLog> logs = new ArrayList<TradeLog>();

    public DailyResult(LocalDateTime dt){
        this.dt = dt;
    }

    public void add(TradeLog log){
        this.buyingPrice += log.getBuyingPrice();
        this.sellingPrice += log.getSellingPrice();
        roi = (double) this.sellingPrice / this.buyingPrice;
        logs.add(log);
    }
    
    public void merge(DailyResult dr){
        this.buyingPrice += dr.getBuyingPrice();
        this.sellingPrice += dr.getSellingPrice();
        roi = (double) this.sellingPrice / this.buyingPrice;
        logs.addAll(dr.getLogs());
    }
}
