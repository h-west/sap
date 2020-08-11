package io.hsjang.saptest.tester;

import java.util.Date;

import lombok.Data;

@Data
public class Stock {
    
    String symbol;
    Long price;
    Long count;
    Date firstBuyDt;
    int age=0;

    public Stock(String symbol, Long price, Long count, Date dt){
        this.symbol = symbol;
        this.price = price;
        this.count = count;
        this.firstBuyDt = dt;
    }

    public Stock buy(Long price, Long count){
        if(count>0L){
            this.price =  ( (this.price*this.count) + (price*count) ) / (this.count+count);
            this.count += count;
            age++;
        }
        return this;
    }
}