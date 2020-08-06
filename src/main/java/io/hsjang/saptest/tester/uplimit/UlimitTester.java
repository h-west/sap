package io.hsjang.saptest.tester.uplimit;

import java.util.Date;

import org.springframework.stereotype.Service;

import io.hsjang.saptest.tester.Balance;
import io.hsjang.saptest.tester.Status;

@Service
public class UlimitTester {

    Status status;
    Balance balance;

    public void test(Date from, Date to, Long cash){
        status = new Status(from, to);
        balance = new Balance(cash);
        trade();
    }

    public void trade(){
        next();
    }

    public void finish(){
        //
    }

    public void next(){
        if(status.next()){
            trade();
        }else{
            finish();
        }
    }

}