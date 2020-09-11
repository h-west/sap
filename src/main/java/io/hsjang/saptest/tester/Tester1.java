package io.hsjang.saptest.tester;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
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
import io.hsjang.saptest.repos.rdbc.KrxRepository;
import io.hsjang.saptest.repos.rdbc.SeriesRepository;

@Service
public class Tester1 implements InitializingBean{
    long balance;
    Map<String,Stock> stocks = new HashMap<String,Stock>();

    @Autowired
    SeriesRepository seriesRepository;

    @Autowired
    KrxRepository krxRepository;

    List<Date> openDays;
    
    // 전일 거래 정보
    List<Series> temp1=new ArrayList<Series>();

    // 처리일자
    Date dt;

    // 조건
    List<String> excludes = List.of("000000", "000001"); // 제외 symbol
    String buyCondition = "i"; // i:시가, p:퍼센트 (p:-5)
    int buyRatio = 50; // 잔고의 70% 만큼 후보 매수 (후보 1/n)

    String sellCondition = "-5:5";  // -5이하 손절 5이상 차익실현
    int maxStored = 39;

    Map<String,String> krxMap;

    public void fullTest(){

        File file = new File("log.txt");
        FileWriter writer = null;


        float max =0;
        TradeResult maxResult = new TradeResult();
        
        try {
        for(int l=1;l<=1;l++){
            //this.maxStored = l;
            for(int u=3;u<=30;u++){
                
                     // 기존 파일의 내용에 이어서 쓰려면 true를, 기존 내용을 없애고 새로 쓰려면 false를 지정한다.
                    writer = new FileWriter(file, true);
                    writer.write("==================================================================================================================\n");
                    writer.write("  ** (조건)   1. 매도 : "+l+"% 하락할 경우   2. 매도 : "+u+"% 상승할 경우 \n");
                    writer.write("==================================================================================================================\n");
        
                    
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    int last = openDays.size()-1;
                    //for(int i=last-80; i<last; i++){
                        //for(int j=i+1; j<openDays.size(); j++){
                            TradeResult r = start(openDays.get(last-80), openDays.get(last), 10000000L, 70, l+":"+u); 
                            if(max<r.getEr()){
                                max=r.getEr();
                                r.setCondision(l+"% 하락, "+u+"% 상승");
                                maxResult = r;
                            }
                        //}
                        String log = "  ** (조건) "+l+"% 하락, "+u+"% 상승 => ["+sdf.format(openDays.get(last-80))+" ~ NOW]::::> 수익률["+r.getEr()+"]:::"+r;
                        System.out.println(log);
                        writer.write(log+"\n");
                    //}
                    writer.flush();
                    
                }
            }

            writer.write("==================================================================================================================\n");
            writer.write(" ** (조건) ["+maxResult.getCondision()+"]  =>  최대 수익률["+max+"]:::"+maxResult+"\n");
            writer.write("==================================================================================================================\n");

            writer.flush();

        } catch(IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if(writer != null) writer.close();
            } catch(IOException e) {
                e.printStackTrace();
            }
        }

    }

    public TradeResult start(String sDt, String eDt, long bal, int buyRatio, String sellCondition){
        this.buyRatio = buyRatio;
        this.sellCondition = sellCondition;
        return start(sDt, eDt, bal);
    }

    public TradeResult start(Date sDt, Date eDt, long bal, int buyRatio, String sellCondition){
        this.buyRatio = buyRatio;
        this.sellCondition = sellCondition;
        return start(sDt, eDt, bal);
    }

    public TradeResult start(String sDt, Long bal){
        return start(sDt, new SimpleDateFormat("yyyyMMdd").format(new Date()),bal);
    }

    public TradeResult start(String sDt, String eDt, long bal){
        try{

            Date startDt = new SimpleDateFormat("yyyyMMdd").parse(sDt);
            Date endDt = new SimpleDateFormat("yyyyMMdd").parse(eDt);
            return start(startDt, endDt ,bal);
        }catch(Exception e){
            //
        }
        return null;
    }

    public TradeResult start(Date sDt, Date eDt, long bal){
        this.balance = bal;
        this.stocks = new HashMap<String,Stock>();
        
        Calendar c = Calendar.getInstance();
        c.setTime(sDt);
        c.add(Calendar.HOUR, 9);
        
        int dIdx = -1;
        int mxIdx = openDays.size()-1;
        for(int i=0;i<=mxIdx; i++){
            if(openDays.get(i).getTime()>sDt.getTime()){
                dIdx = i;
                break;
            }
        }
        
        int procCnt = 0;
        List<TradeLog> logs= new ArrayList<TradeLog>();
        while(dIdx>0 && dIdx<=mxIdx && openDays.get(dIdx).before(eDt)){
            Date dt = openDays.get(dIdx);
            List<Series> candidates = seriesRepository.findByDateAndChangeGreaterThanEqual(dt, 0.29d);
            logs.add(testByDay(dt, candidates));
            dIdx++;
            procCnt++;
        }
        //System.out.println("시작일["+sDt+"],종료일["+eDt+"],거래일["+procCnt+"],금액["+balance+"("+((float)balance/bal)*100+"%)]");
        long tot = balance+getTotalPrice();
        //System.out.println(new TradeResult(sDt,eDt,procCnt,bal,tot,((float)tot/bal)*100));
        //return new TradeResult(sDt,eDt,procCnt,bal,tot,((float)tot/bal)*100).addLogs(logs);
        return null;
    }

    public TradeLog testByDay(Date dt, List<Series> candidates){
//System.out.println(dt);
        TradeLog log = new TradeLog();
       // log.setDt(dt);
        log.add("==================================================================================================================");
        log.add(" ** 거래일:" +dt+ "    매매로그");
        log.add("==================================================================================================================");


        this.dt = dt;

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
                String[] pa = sellCondition.split(":");
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
            Series s = seriesRepository.findByDateAndSymbol(dt,o.getSymbol());
            if(s!=null && s.getVolume()>0L && o.getPrice()==0L){
                Long buyPrice = s.getOpen();
                Long count = max/buyPrice;
                
                if(stocks.containsKey(o.getSymbol())){
                    stocks.put(o.getSymbol(), stocks.get(o.getSymbol()).buy(buyPrice, count));
                }else{
                    stocks.put(o.getSymbol(), new Stock(o.getSymbol(),krxMap.get(o.getSymbol()),buyPrice,count,dt.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime()));
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
            Series s = seriesRepository.findByDateAndSymbol(dt,o.getSymbol());
            if(o.getUprice() < s.getHigh()){
                Stock myStock = stocks.remove(o.getSymbol());
                balance += myStock.getCount() * o.getUprice();

                logs.add(krxMap.get(o.getSymbol()) + "(" +o.getSymbol() + ") :: " +o.getUprice()+"(원)x"+myStock.getCount()+"(개)="+myStock.getCount() * o.getUprice()+"(원) 매도(차익), 잔고:"+balance +"(원)");
            }
        });

        List<String> removeList = new ArrayList<String>();
        stocks.keySet().stream().forEach(symbol->{
            if(stocks.get(symbol).getAge()>maxStored){
                Series s = seriesRepository.findByDateAndSymbol(dt,symbol);
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


    @Override
    public void afterPropertiesSet() throws Exception {
        openDays = seriesRepository.findSeriesDateList().stream()
                        .map(s->Date.from(((LocalDateTime)s.get(0)).atZone(ZoneId.systemDefault()).toInstant())).collect(Collectors.toList());

        //Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant());


        List<Krx> krxs = krxRepository.findAll();
        krxMap = new HashMap<String,String>();
        for(Krx krx: krxs){
            krxMap.put(krx.getSymbol(), krx.getName());
        }
        
    }

    
}