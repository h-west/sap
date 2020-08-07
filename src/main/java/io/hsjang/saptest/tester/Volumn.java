package io.hsjang.saptest.tester;

import lombok.Data;

@Data
public class Volumn {
    Long price;
    int count;

    public Volumn(Long price, int count){
        this.price = price;
        this.count = count;
    }

    public void buy(Long price, int count){
        int sum = this.count + count;
        this.price = (this.price*this.count + price*count) / sum;
        this.count = sum;
    }

    public void sell(Long price, int count){
        this.count -= count;
    }

    public Long total(){
        return (long)(this.price * this.count);
    }
}