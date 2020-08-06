package io.hsjang.saptest.tester;

import lombok.Data;

@Data
public class Volumn {
    int price;
    int count;

    public Volumn(int price, int count){
        this.price = price;
        this.count = count;
    }

    public void add(int price, int count){

    }

    public Long total(){
        return (long)(this.price * this.count);
    }
}