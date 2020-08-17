package io.hsjang.saptest.tester;

import java.util.Date;

import lombok.Data;

@Data
public class Stock {
    
    String symbol;
    String name;
    Long price;
    Long count;
    Date firstBuyDt;
    int age=0;

    public Stock(String symbol, String name, Long price, Long count, Date dt){
        this.symbol = symbol;
        this.name= name;
        this.price = price;
        this.count = count;
        this.firstBuyDt = dt;
    }

    public Stock buy(Long price, Long count){
        if(count>0L){
            this.price =  ( (this.price*this.count) + (price*count) ) / (this.count+count);
            this.count += count;
        }
        return this;
    }

    public void addAge(){
        age++;
    }

    @Override
    public String toString(){
        return name + "(" +symbol + ") :: " + price + "(원)x" + count + "(개)" + price * count + "(원), 첫매수일:"+firstBuyDt+", 구매 후 ["+age+"]영업일 경과";
    }
}