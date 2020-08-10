package io.hsjang.saptest.tester;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Stock {
    
    String symbol;
    Long price;
    Long count;
    Date firstBuyDt;

    public Stock add(Long price, Long count){
        this.price =  ( (this.price*this.count) + (price*count) ) / (this.count+count);
        this.count += count;
        return this;
    }
}