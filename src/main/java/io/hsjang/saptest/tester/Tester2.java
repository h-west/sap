package io.hsjang.saptest.tester;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import io.hsjang.saptest.model.Series;
import io.hsjang.saptest.repos.r2dbc.KrxR2Repository;
import io.hsjang.saptest.repos.r2dbc.SeriesR2Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class Tester2 {

    long balance;
    Map<String,Stock> stocks = new HashMap<String,Stock>();

    SeriesR2Repository seriesRepository;
    KrxR2Repository krxRepository;
    
    // 
    LocalDateTime today;
    LocalDateTime yesterDay;

    // 전일 거래 정보
    List<Series> temp1=new ArrayList<Series>();

    

    // 조건
    LocalDateTime sDt;
    LocalDateTime eDt;
    List<String> excludes = null;//sList.of("000000", "000001"); // 제외 symbol
    int buyRatio = 50;
    int sellUnder = -5;
    int sellUpper = 5;
    int maxStored = 39;

    public Tester2(SeriesR2Repository seriesRepository,KrxR2Repository krxRepository){
        this.seriesRepository = seriesRepository;
        this.krxRepository = krxRepository;
    }

    public Mono<TradeResult> start(){
        
        TradeResult result = new TradeResult();
        result.setSDt(sDt);
        result.setEDt(eDt);

        return Flux.fromIterable(Meta.openDays)
            .map(this::setYesterDay)
            .filter(d->d.isEqual(sDt)||d.isEqual(eDt)||(d.isAfter(sDt)&&d.isBefore(eDt)))
            .map(this::dailyTest)
            .collectList()
            .map(result::addLogs);

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

    public LocalDateTime setYesterDay(LocalDateTime dt){
        if(today!=null){
            this.yesterDay = today;
        }else{
            for(LocalDateTime day: Meta.openDays){
                if(dt.isAfter(day)){
                    this.yesterDay = day;
                    break;
                }
            }
        }
        this.today = dt;
        return dt;
    }

    public TradeLog dailyTest(LocalDateTime dt){

        Flux.concat(buy())
            .subscribe(o->System.out.println(o));

        return new TradeLog();

        /*
TradeLog log = new TradeLog();
                log.add("test=>" + d.toString());
                log.add("yesterDay=>" + yesterDay.toString());
                log.setDt(d);
                log.setBalance(balance);
                return log;
        */
    }

    /**
     * 매수 주문 Flux
     */
    public Mono<List<Order>> getBuyOders(){
        return seriesRepository.findByDateAndChangeGreaterThanEqual(yesterDay, 0.29d)
            .map(s->Order.buy(s.getSymbol(), 0L))
            .collectList();
    }
    /**
     * 매수
     */
    public Flux<String> buy() {
        // seriesRepository.countByDateAndChangeGreaterThanEqual(yesterDay, 0.29d)
        //     .filter(c->c>0)
        //     .map(c->{
        //         System.out.println(">>>>>>>>>>>>>"+c);
        //         seriesRepository.findByDateAndChangeGreaterThanEqual(yesterDay, 0.29d)
        //             .doOnNext(System.out::println);
        //         return new TradeLog();
        //     })
        //     .subscribe(System.out::println);

        return Flux.empty();

        // 매수
        // series : 오늘 정보
        /*
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
                    stocks.put(o.getSymbol(), new Stock(o.getSymbol(),Meta.krxMap.get(o.getSymbol()),buyPrice,count,LocalDateTime.now()));
                }
                balance -= buyPrice*count;
                //System.out.println("BAL :: >>>>"+balance+" ["+s.getDate()+"]");

                logs.add(Meta.krxMap.get(o.getSymbol()) + "(" +o.getSymbol() + ") :: " +buyPrice+"(원)x"+count+"(개)="+buyPrice*count+"(원) 매수, 잔고:"+balance +"(원)");
            }
        });
         */
    }


    


    /**
     * 매도 주문 Flux
     */
    public Flux<Order> getSellOders(){
        return Flux.fromStream(stocks.keySet().stream())
                .map(s->{Stock stock = stocks.get(s);
                    String[] pa = "".split(":"); //sellCondition.split(":");
                    int lp = Integer.parseInt(pa[0]);
                    int up = Integer.parseInt(pa[1]);
    
                    Long uprice = (long) (stock.getPrice() * (1+(float)up/100));
                    Long lprice = (long) (stock.getPrice() * (1+(float)lp/100));
                    return Order.sell(stocks.get(s).getSymbol(), uprice, lprice);
                });
    }

    //public TradeLog testByDay(LocalDateTime dt, List<Series> candidates){
    public TradeLog DailyTest(LocalDateTime dt, List<Series> candidates){
//System.out.println(dt);
        TradeLog log = new TradeLog();
        log.setDt(dt);
        log.add("==================================================================================================================");
        log.add(" ** 거래일:" +dt+ "    매매로그");
        log.add("==================================================================================================================");


        //this.dt = dt;

        // List<Order> buyOrders = getBuyOrders();
        // if(buyOrders.size()>0){
        //     log.add(buy(buyOrders));
        // }

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

    public List<String> buys(List<Order> orders){
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
                    stocks.put(o.getSymbol(), new Stock(o.getSymbol(),Meta.krxMap.get(o.getSymbol()),buyPrice,count,LocalDateTime.now()));
                }
                balance -= buyPrice*count;
                //System.out.println("BAL :: >>>>"+balance+" ["+s.getDate()+"]");

                logs.add(Meta.krxMap.get(o.getSymbol()) + "(" +o.getSymbol() + ") :: " +buyPrice+"(원)x"+count+"(개)="+buyPrice*count+"(원) 매수, 잔고:"+balance +"(원)");
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

                logs.add(Meta.krxMap.get(o.getSymbol()) + "(" +o.getSymbol() + ") :: " +o.getUprice()+"(원)x"+myStock.getCount()+"(개)="+myStock.getCount() * o.getUprice()+"(원) 매도(차익), 잔고:"+balance +"(원)");
            }
        });

        List<String> removeList = new ArrayList<String>();
        stocks.keySet().stream().forEach(symbol->{
            if(stocks.get(symbol).getAge()>maxStored){
                Series s = seriesRepository.findByDateAndSymbol(LocalDateTime.now(),symbol).block();
                Stock myStock = stocks.get(symbol);
                removeList.add(symbol);
                balance += myStock.getCount() * s.getClose();

                logs.add(Meta.krxMap.get(symbol) + "(" +symbol + ") :: " +s.getClose()+"(원)x"+myStock.getCount()+"(개)="+myStock.getCount() * s.getClose()+"(원) 종가정리("+maxStored+"일지남), 잔고:"+balance+"(원)");
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


    public Tester2 setSDt(String sDt){
        LocalDate dt = LocalDate.parse(sDt, DateTimeFormatter.ISO_DATE);
        this.sDt = dt.atTime(0,0,0);
        return this;
    }

    public Tester2 setEDt(String eDt){
        LocalDate dt = LocalDate.parse(eDt, DateTimeFormatter.ISO_DATE);
        this.eDt = dt.atTime(0,0,0);
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
}