package io.hsjang.saptest.tester;

import java.util.HashMap;
import java.util.Map;

import lombok.Data;

@Data
public class Balance {
    Long cash;
    Map<String, Volumn> myStocks;

    public Balance(Long cash) {
        this.cash = cash;
        myStocks = new HashMap<String, Volumn>();
    }

    public void buy(String symbol, Long price, int count) {
        if (myStocks.containsKey(symbol)) {
            myStocks.get(symbol).buy(price, count);
        } else {
            myStocks.put(symbol, new Volumn(price, count));
        }
        this.cash -= price * count;
    }

    public void sell(String symbol, Long price, int count) {
        if (myStocks.containsKey(symbol)) {
            myStocks.get(symbol).sell(price, count);
            this.cash += price * count;
        }
        
    }
}