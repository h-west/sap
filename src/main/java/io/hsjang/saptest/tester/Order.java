package io.hsjang.saptest.tester;

import lombok.Data;

@Data
public class Order {

    String type; // buy, sell
    String symbol;
    Long price; // 0:시가, others:기타
    Long uprice; // 상단
    Long lprice; // 하단

    public static Order buy(String symbol, Long price) {
        return new Order(symbol, price);
    }

    public static Order sell(String symbol, Long uprice, Long lprice) {
        return new Order(symbol, uprice, lprice);
    }

    public Order(String symbol, Long price) {
        this.type = "buy";
        this.symbol = symbol;
        this.price = price;
    }

    public Order(String symbol, Long uprice, Long lprice) {
        this.type = "sell";
        this.symbol = symbol;
        this.uprice = uprice;
        this.lprice = lprice;
    }

    public boolean isBuyOrder() {
        return "buy".equals(type);
    }

    public boolean isSellOrder() {
        return "sell".equals(type);
    }

}