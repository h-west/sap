package io.hsjang.saptest.tester;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.hsjang.saptest.model.Krx;
import io.hsjang.saptest.model.Series;
import io.hsjang.saptest.repos.r2dbc.KrxR2Repository;
import io.hsjang.saptest.repos.r2dbc.SeriesR2Repository;
import io.hsjang.saptest.repos.rdbc.KrxRepository;
import io.hsjang.saptest.repos.rdbc.SeriesRepository;
import reactor.core.publisher.Mono;

public class Tester2 {

    long balance;
    Map<String,Stock> stocks = new HashMap<String,Stock>();

    SeriesR2Repository seriesRepository;
    KrxR2Repository krxRepository;

    List<LocalDateTime> openDays;
    
    // 전일 거래 정보
    List<Series> temp1=new ArrayList<Series>();

    // 처리일자 (index)
    int dtIdx;

    // 조건
    LocalDateTime sDt;
    LocalDateTime eDt;
    List<String> excludes = List.of("000000", "000001"); // 제외 symbol
    int buyRatio = 50;
    int sellUnder = -5;
    int sellUpper = 5;
    int maxStored = 39;

    Map<String,String> krxMap = new HashMap<String,String>();

    public Tester2(KrxR2Repository krxRepository, SeriesR2Repository seriesRepository){
        
        long balance;
        Map<String,Stock> stocks = new HashMap<String,Stock>();

        this.krxRepository = krxRepository;
        this.seriesRepository = seriesRepository;

        openDays = seriesRepository.findSeriesDateList()
                        .map(s->(LocalDateTime)s.get(0))
                        .collectList()
                        .block();

        krxRepository.findAll().subscribe(krx->{
            krxMap.put(krx.getSymbol(), krx.getName());
        });
    }

    public Tester2 setSDt(String sDt){
        this.sDt = LocalDateTime.parse(sDt, DateTimeFormatter.ISO_DATE);
        return this;
    }

    public Tester2 setEDt(String eDt){
        this.eDt = LocalDateTime.parse(eDt, DateTimeFormatter.ISO_DATE);
        return this;
    }

    public Tester2 setBalnace(long balance){
        this.balance = balance;
        return this;
    }

    public Tester2 setByRatio(int buyRatio){
        this.buyRatio = buyRatio;
        return this;
    }

    public Tester2 setSellUnder(int sellUnder){
        this.sellUnder = sellUnder;
        return this;
    }

    public Tester2 setMaxStored(int maxStored){
        this.maxStored = maxStored;
        return this;
    }

    public void clearStocks(){
        stocks = new HashMap<String,Stock>();
    }
    

    public Mono<TradeResult> start(){
        
        int dIdx = -1;
        int mxIdx = openDays.size()-1;
        for(int i=0;i<=mxIdx; i++){
            if(openDays.get(i).isEqual(sDt)){
                dIdx = i;
                break;
            }
        }
        // temp
        return Mono.empty();
        
        /*
        int procCnt = 0;
        List<TradeLog> logs= new ArrayList<TradeLog>();
        while(dIdx>0 && dIdx<=mxIdx && openDays.get(dIdx).isBefore(eDt)){
            LocalDateTime dt = openDays.get(dIdx);
            List<Series> candidates = seriesRepository.findByDateAndChangeGreaterThanEqual(dt, 0.29d);
            logs.add(testByDay(dt, candidates));
            dIdx++;
            procCnt++;
        }
        //System.out.println("시작일["+sDt+"],종료일["+eDt+"],거래일["+procCnt+"],금액["+balance+"("+((float)balance/bal)*100+"%)]");
        long tot = balance+getTotalPrice();
        System.out.println(new TradeResult(sDt,eDt,procCnt,bal,tot,((float)tot/bal)*100));
        return Mono.just(new TradeResult(sDt,eDt,procCnt,bal,tot,((float)tot/bal)*100).addLogs(logs));
        */
    }

    public TradeLog testByDay(Date dt, List<Series> candidates){
//System.out.println(dt);
        TradeLog log = new TradeLog();
        log.setDt(dt);
        log.add("==================================================================================================================");
        log.add(" ** 거래일:" +dt+ "    매매로그");
        log.add("==================================================================================================================");


        //this.dt = dt;

        List<Order> buyOrders = getBuyOrders();
        if(buyOrders.size()>0){
            log.add(buy(buyOrders));
        }

        List<Order> sellOrders = getSellOrders();
        if(sellOrders.size()>0){
            log.add(sell(sellOrders));
        }

        log.add("=========================== 거래일:" +dt+ "  잔고 ============================");
        for(String symbol: stocks.keySet()){
            log.add(stocks.get(symbol).toString());

            // age ++
            stocks.get(symbol).addAge();
        }
        log.setBalance(this.balance + getTotalPrice());

        // 임시저장
        temp1 = candidates;

        return log;
    }

    // 후보(매수) 리스트
    public List<Order> getBuyOrders(){
        return temp1.stream().filter(s->!excludes.contains(s.getSymbol()))
            .map(s->{
                Long p = 0L;
                // if(!buyCondition.equals("i")){
                //     String[] bCon = buyCondition.split(":");
                //     if(bCon.length>1){
                //         if("p".equals(bCon[0])){
                //             int persent = Integer.parseInt(bCon[1]);
                //             p = s.getClose() * (1+persent/100);
                //         } 
                //         //else if{
                //             // else if others
                //         //}
                //     }
                // }
                return Order.buy(s.getSymbol(), p);
            }).collect(Collectors.toList());

/*
        List<Order> orders = new ArrayList<Order>();
        for(Series s:temp1){
            if(!excludes.contains(s.getSymbol())){
                Long p = 0L;
                if(!buyCondition.equals("i")){
                    String[] bCon = buyCondition.split(":");
                    if(bCon.length>1){
                        if("p".equals(bCon[0])){
                            int persent = Integer.parseInt(bCon[1]);
                            p = s.getClose() * (1+persent/100);
                        } 
                        //else if{
                            // else if others
                        //}
                    }
                }
                orders.add(Order.buy(s.getSymbol(), p));
            }
        }
        return orders;
        // 전일 거래정보가 없거나 상한가 정보가 없으면 new ArrayList<Candidate>();  // size가 0이되면 그냥 temp1에 저장하고 종료
*/
    }

    public List<Order> getSellOrders(){
        
        return stocks.keySet().stream()
            .map(s->{
                Stock stock = stocks.get(s);
                String[] pa = "".split(":"); //sellCondition.split(":");
                int lp = Integer.parseInt(pa[0]);
                int up = Integer.parseInt(pa[1]);

                Long uprice = (long) (stock.getPrice() * (1+(float)up/100));
                Long lprice = (long) (stock.getPrice() * (1+(float)lp/100));
                return Order.sell(stocks.get(s).getSymbol(), uprice, lprice);
            }).collect(Collectors.toList());

            /*
        List<Order> orders = new ArrayList<Order>();
        for(String s: stocks.keySet()){
            Stock stock = stocks.get(s);
            String[] pa = sellCondition.split(":");
            int lp = Integer.parseInt(pa[0]);
            int up = Integer.parseInt(pa[1]);

            Long uprice = (long) (stock.getPrice() * (1+(float)up/100));
            Long lprice = (long) (stock.getPrice() * (1+(float)lp/100));
            orders.add(Order.sell(stocks.get(s).getSymbol(), uprice, lprice));
        }
        return orders;
        */
    }

    public List<String> buy(List<Order> orders){
        // 매수
        // series : 오늘 정보
        Long max = balance * buyRatio / 100 / orders.size();
        List<String> logs = new ArrayList<String>();

        orders.stream().forEach(o->{
            Series s = seriesRepository.findByDateAndSymbol(LocalDateTime.now(),o.getSymbol()).block();
            if(s!=null && s.getVolume()>0L && o.getPrice()==0L){
                Long buyPrice = s.getOpen();
                Long count = max/buyPrice;
                
                if(stocks.containsKey(o.getSymbol())){
                    stocks.put(o.getSymbol(), stocks.get(o.getSymbol()).buy(buyPrice, count));
                }else{
                    stocks.put(o.getSymbol(), new Stock(o.getSymbol(),krxMap.get(o.getSymbol()),buyPrice,count,LocalDateTime.now()));
                }
                balance -= buyPrice*count;
                //System.out.println("BAL :: >>>>"+balance+" ["+s.getDate()+"]");

                logs.add(krxMap.get(o.getSymbol()) + "(" +o.getSymbol() + ") :: " +buyPrice+"(원)x"+count+"(개)="+buyPrice*count+"(원) 매수, 잔고:"+balance +"(원)");
            }
        });

/*
        for(Order o: orders){
            Series s = seriesRepository.findByDateAndSymbol(dt,o.getSymbol());
            if(s!=null && s.getVolume()>0L){  // 거래량이 있을것.
                if(o.getPrice()==0L){ // 시가 매수
                    Long buyPrice = s.getOpen();
                    Long count = max/buyPrice;
                    
                    if(stocks.containsKey(o.getSymbol())){
                        stocks.put(o.getSymbol(), stocks.get(o.getSymbol()).buy(buyPrice, count));
                    }else{
                        stocks.put(o.getSymbol(), new Stock(o.getSymbol(),krxMap.get(o.getSymbol()),buyPrice,count,dt));
                    }
                    balance -= buyPrice*count;
                    //System.out.println("BAL :: >>>>"+balance+" ["+s.getDate()+"]");

                    logs.add(krxMap.get(o.getSymbol()) + "(" +o.getSymbol() + ") :: " +buyPrice+"(원)x"+count+"(개)="+buyPrice*count+"(원) 매수, 잔고:"+balance +"(원)");
                }
            }
        }
*/
        return logs;

        //System.out.println("BUY!!>>!"+dt +"(bal:"+balance+")::>" + stocks);
    }

    public List<String> sell(List<Order> orders){
        List<String> logs = new ArrayList<String>();

        orders.stream().forEach(o->{
            Series s = seriesRepository.findByDateAndSymbol(LocalDateTime.now(),o.getSymbol()).block();
            if(o.getUprice() < s.getHigh()){
                Stock myStock = stocks.remove(o.getSymbol());
                balance += myStock.getCount() * o.getUprice();

                logs.add(krxMap.get(o.getSymbol()) + "(" +o.getSymbol() + ") :: " +o.getUprice()+"(원)x"+myStock.getCount()+"(개)="+myStock.getCount() * o.getUprice()+"(원) 매도(차익), 잔고:"+balance +"(원)");
            }
        });

        List<String> removeList = new ArrayList<String>();
        stocks.keySet().stream().forEach(symbol->{
            if(stocks.get(symbol).getAge()>maxStored){
                Series s = seriesRepository.findByDateAndSymbol(LocalDateTime.now(),symbol).block();
                Stock myStock = stocks.get(symbol);
                removeList.add(symbol);
                balance += myStock.getCount() * s.getClose();

                logs.add(krxMap.get(symbol) + "(" +symbol + ") :: " +s.getClose()+"(원)x"+myStock.getCount()+"(개)="+myStock.getCount() * s.getClose()+"(원) 종가정리("+maxStored+"일지남), 잔고:"+balance+"(원)");
            }
        });
        for(String rk: removeList){
            stocks.remove(rk);
        }

        return logs;
        /*
        // 매도
        for(Order o: orders){
           // System.out.println("SELL-ORDER : "+o);
            Series s = seriesRepository.findByDateAndSymbol(dt,o.getSymbol());
           // System.out.println("SELL-TODAY : "+s);
            // if(o.getLprice() > s.getLow()){
            //     Stock myStock = stocks.remove(o.getSymbol());
            //     balance += myStock.getCount() * o.getLprice();

            //     logs.add(krxMap.get(o.getSymbol()) + "(" +o.getSymbol() + ") :: " +o.getLprice()+"(원)x"+myStock.getCount()+"(개)="+myStock.getCount() * o.getLprice()+"(원) 매도(손절), 잔고:"+balance+"(원)");
            // }else 
            if(o.getUprice() < s.getHigh()){
                Stock myStock = stocks.remove(o.getSymbol());
                balance += myStock.getCount() * o.getUprice();

                logs.add(krxMap.get(o.getSymbol()) + "(" +o.getSymbol() + ") :: " +o.getUprice()+"(원)x"+myStock.getCount()+"(개)="+myStock.getCount() * o.getUprice()+"(원) 매도(차익), 잔고:"+balance +"(원)");
            }
        }

        // 수익을 못내도 특정기간이 지나면 판다 (종가로)
        List<String> removeList = new ArrayList<String>();
        for(String symbol: stocks.keySet()){
            if(stocks.get(symbol).getAge()>maxStored){
                Series s = seriesRepository.findByDateAndSymbol(dt,symbol);
                Stock myStock = stocks.get(symbol);
                removeList.add(symbol);
                balance += myStock.getCount() * s.getClose();

                logs.add(krxMap.get(symbol) + "(" +symbol + ") :: " +s.getClose()+"(원)x"+myStock.getCount()+"(개)="+myStock.getCount() * s.getClose()+"(원) 종가정리("+maxStored+"일지남), 잔고:"+balance+"(원)");
            }
        }
        for(String rk: removeList){
            stocks.remove(rk);
        }

        return logs;
        */
        
        //System.out.println("SELL!!>>!"+dt +"(bal:"+balance+")::>" + stocks);
        // System.out.println(stocks);
        // System.out.println(balance);
    }

    public Long getTotalPrice(){
        return stocks.values().stream().mapToLong(s->s.getCount()*s.getPrice()).sum();
    }


    

    // 시작
    // setOrders
    // 
}