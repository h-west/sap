package io.hsjang.saptest.tester;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import lombok.Data;

@Data
public class Balance {
    Long cash;
    Map<String,Volumn> myStocks;

    public Balance(Long cash){
        this.cash = cash;
        myStocks = new HashMap<String,Volumn>();
    }

    public void trade(String symbol, int price, int count){
        if(myStocks.containsKey(symbol)){
            myStocks.get(symbol).add(price, count);
        }else{
            myStocks.put(symbol, new Volumn(price, count));
        }
        this.cash = this.cash - price * count;
    }
}