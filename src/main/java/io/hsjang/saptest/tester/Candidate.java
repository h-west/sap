package io.hsjang.saptest.tester;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Candidate {

    String symbol;
    String position; // buy, sell
    Long price;  // 0:시가, others:기타

    public static Candidate ofBuy(String symbol, Long price){
        return new Candidate(symbol, "buy", price);
    }

    public static Candidate ofSell(String symbol, Long price){
        return new Candidate(symbol, "sell", price);
    }

    public boolean isBuyCandidate(){
        return "buy".equals(position);
    }

    public boolean isSellCandidate(){
        return "sell".equals(position);
    }
    
}