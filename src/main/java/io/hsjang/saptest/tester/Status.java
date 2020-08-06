package io.hsjang.saptest.tester;

import java.util.Calendar;
import java.util.Date;

import lombok.Data;

@Data
public class Status {
    
    Date from;
    Date to;
    
    Calendar current = Calendar.getInstance();

    public Status(Date from, Date to){
        this.from = from;
        this.to = to;
        current.setTime(from);
        current.add(Calendar.HOUR, 9);
    }

    public Date getCurrent(){
        return current.getTime();
    }

    public boolean next(){
        current.add(Calendar.DATE, 1);
        return current.getTime().before(to);
    }
}