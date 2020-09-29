package io.hsjang.saptest;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import io.hsjang.saptest.tester.DailyResult;
import lombok.Data;

@Data
public class TestResult implements Serializable{

    private static final long serialVersionUID = 1L;

    LocalDateTime sDt;
    LocalDateTime eDt;
    Long asset;
    Long balance;
    double roi;
    List<DailyResult> dailyResults = new ArrayList<DailyResult>();

    public TestResult(List<DailyResult> drs){
        if(!drs.isEmpty()){
            this.sDt = drs.get(0).getDt();
            this.eDt = drs.get(drs.size()-1).getDt();
        }
        this.dailyResults = drs;
    }

    public void setRoi(){
        this.roi = (double) balance / asset * 100;
    }

}