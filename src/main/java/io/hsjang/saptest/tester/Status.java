package io.hsjang.saptest.tester;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Data;

@Data
public class Status {
    
    List<Date> openDays;
    Date from;
    Date to;
    int yIdx;
    int dIdx;

    Long balance;
    Map<String, Volumn> myStocks;

    public Status(List<Date> openDays, Date from, Date to, Long cash){
        this.openDays = openDays;
        this.from = from;
        this.to = to;
        this.balance = cash;
        myStocks = new HashMap<String, Volumn>();

        Calendar c = Calendar.getInstance();
        c.setTime(from);
        c.add(Calendar.HOUR, 9);

        try{
            Date min = new SimpleDateFormat("yyyyMMddHH").parse("2015060209");
            if(c.getTime().before(min)){
                c.setTime(min);
            }
        }catch(Exception e){

        }

        for(int i=0;i<openDays.size(); i++){
            if(openDays.get(i).getTime()>from.getTime()){
                this.dIdx=i;
                this.yIdx=this.dIdx-1;
                break;
            }
        }
    }

    public Date getYesterday(){
        return openDays.get(yIdx);
    }

    public Date getCurrent(){
        return openDays.get(dIdx);
    }

    public boolean next(){
        yIdx++;
        dIdx++;
        return dIdx < openDays.size();
    }

    public void buy(String symbol, Long price, int count) {
        if (myStocks.containsKey(symbol)) {
            myStocks.get(symbol).buy(price, count);
        } else {
            myStocks.put(symbol, new Volumn(price, count));
        }
        this.balance -= price * count;
    }

    public void sell(String symbol, Long price, int count) {
        if (myStocks.containsKey(symbol)) {
            myStocks.get(symbol).sell(price, count);
            this.balance += price * count;
        }
        
    }
}