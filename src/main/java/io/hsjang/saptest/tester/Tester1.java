package io.hsjang.saptest.tester;

import java.text.SimpleDateFormat;
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

import io.hsjang.saptest.model.Series;
import io.hsjang.saptest.repos.SeriesRepository;

@Service
public class Tester1 implements InitializingBean{
    Long balance;
    Map<String,Stock> stocks = new HashMap<String,Stock>();

    @Autowired
    SeriesRepository seriesRepository;

    List<Date> openDays;
    
    // 전일 거래 정보
    List<Series> temp1=new ArrayList<Series>();

    // 처리일자
    Date dt;

    // 조건
    List<String> excludes = List.of("000000", "000001"); // 제외 symbol
    String buyCondition = "i"; // i:시가, p:퍼센트 (p:-5)
    int buyRatio = 70; // 잔고의 70% 만큼 후보 매수 (후보 1/n)

    String sellCondition = "-5:5";  // -5이하 손절 5이상 차익실현

    public void start(String sDt, Long bal){
        start(sDt, new SimpleDateFormat("yyyyMMdd").format(new Date()),bal);
    }

    public void start(String sDt, String eDt, Long bal){

        try {
            this.balance = bal;
            this.stocks = new HashMap<String,Stock>();
            Date startDt = new SimpleDateFormat("yyyyMMdd").parse(sDt);
            Date endDt = new SimpleDateFormat("yyyyMMdd").parse(eDt);

            Calendar c = Calendar.getInstance();
            c.setTime(startDt);
            c.add(Calendar.HOUR, 9);

            int dIdx = -1;
            for(int i=0;i<openDays.size(); i++){
                if(openDays.get(i).getTime()>startDt.getTime()){
                    dIdx = i;
                    break;
                }
            }

            while(dIdx>0 && openDays.get(dIdx).before(endDt)){
                Date dt = openDays.get(dIdx);
                List<Series> candidates = seriesRepository.findByDateAndChangeGreaterThanEqual(dt, 0.29d);
                testByDay(dt, candidates);
                dIdx++;
            }

        } catch (Exception e) {
            // 
        }
    }

    public void testByDay(Date dt, List<Series> candidates){
        this.dt = dt;

        List<Order> buyOrders = getBuyOrders();
        if(buyOrders.size()>0){
            buy(buyOrders);
        }

        List<Order> sellOrders = getSellOrders();
        if(sellOrders.size()>0){
            sell(sellOrders);
        }

        // 임시저장
        temp1 = candidates;
    }

    // 후보(매수) 리스트
    public List<Order> getBuyOrders(){
        // return temp1.stream().filter(s->!excludes.contains(s.getSymbol()))
        //     .map(s->{
        //         Long p = 0L;
        //         if(!buyCondition.equals("i")){
        //             String[] bCon = buyCondition.split(":");
        //             if(bCon.length>1){
        //                 if("p".equals(bCon[0])){
        //                     int persent = Integer.parseInt(bCon[1]);
        //                     p = s.getClose() * (1+persent/100);
        //                 } 
        //                 //else if{
        //                     // else if others
        //                 //}
        //             }
        //         }
        //         return Order.ofBuy(s.getSymbol(), p);
        //     }).collect(Collectors.toList());

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
                orders.add(Order.ofBuy(s.getSymbol(), p));
            }
        }
        return orders;
        // 전일 거래정보가 없거나 상한가 정보가 없으면 new ArrayList<Candidate>();  // size가 0이되면 그냥 temp1에 저장하고 종료
    }

    public List<Order> getSellOrders(){
        
        // return stocks.keySet().stream()
        //     .map(s->{
        //         Stock stock = stocks.get(s);
        //         String[] pa = sellCondition.split(":");
        //         int lp = Integer.parseInt(pa[0]);
        //         int up = Integer.parseInt(pa[1]);

        //         Long uprice = stock.getPrice() * (1+up/100);
        //         Long lprice = stock.getPrice() * (1+lp/100);
        //         return Order.ofSell(stocks.get(s).getSymbol(), uprice, lprice);
        //     }).collect(Collectors.toList());

        List<Order> orders = new ArrayList<Order>();
        for(String s: stocks.keySet()){
            Stock stock = stocks.get(s);
            String[] pa = sellCondition.split(":");
            int lp = Integer.parseInt(pa[0]);
            int up = Integer.parseInt(pa[1]);

            Long uprice = stock.getPrice() * (1+up/100);
            Long lprice = stock.getPrice() * (1+lp/100);
            orders.add(Order.ofSell(stocks.get(s).getSymbol(), uprice, lprice));
        }
        return orders;
    }

    public void buy(List<Order> orders){
        // 매수
        // series : 오늘 정보
        Long max = balance * buyRatio / 100 / orders.size();
        // orders.stream().forEach(o->{
        //     series.stream()
        //         .filter(s->s.getSymbol().equals(o.getSymbol()))
        //         .forEach(s->{
        //             if(o.getPrice()==0L){
        //                 Long buyPrice = s.getOpen();
        //                 Long count = max/buyPrice;
                        
        //                 if(stocks.containsKey(o.getSymbol())){
        //                     stocks.put(o.getSymbol(), stocks.get(o.getSymbol()).add(buyPrice, count));
        //                 }else{
        //                     stocks.put(o.getSymbol(), new Stock(o.getSymbol(),buyPrice,count,s.getDate()));
        //                 }
        //                 balance -= buyPrice*count;
        //                 //System.out.println("BAL :: >>>>"+balance+" ["+s.getDate()+"]");
        //             }
        //         });
        // });

        for(Order o: orders){
            Series s = seriesRepository.findByDateAndSymbol(dt,o.getSymbol());
            if(s.getVolume()>0L){  // 거래량이 있을것.
                if(o.getPrice()==0L){ // 시가 매수
                    Long buyPrice = s.getOpen();
                    Long count = max/buyPrice;
                    
                    if(stocks.containsKey(o.getSymbol())){
                        stocks.put(o.getSymbol(), stocks.get(o.getSymbol()).add(buyPrice, count));
                    }else{
                        stocks.put(o.getSymbol(), new Stock(o.getSymbol(),buyPrice,count,dt));
                    }
                    balance -= buyPrice*count;
                    //System.out.println("BAL :: >>>>"+balance+" ["+s.getDate()+"]");
                }
            }
        }

        System.out.println(dt +"(bal:"+balance+")::>" + stocks);
    }

    public void sell(List<Order> orders){
        // 매도
        // series : 오늘 정보
        // for(Order o:orders){
            
        // }

        System.out.println("SELL!!!");
        // System.out.println(stocks);
        // System.out.println(balance);
    }

    public Long getTotalPrice(){
        return stocks.values().stream().mapToLong(s->s.getCount()*s.getPrice()).sum();
    }


    @Override
    public void afterPropertiesSet() throws Exception {
        openDays = seriesRepository.findSeriesDateList().stream().map(s->(Date)s.get(0)).collect(Collectors.toList());
    }

    
}