package io.hsjang.saptest.tester.uplimit;

public enum BuyType {
    INIT(0),LOW(-5);

    int p;
    BuyType(int p){
        this.p = p;
    }
    public void setP(int p){
        this.p = p;
    }
    public int getP(){
        return this.p;
    }
}