package io.hsjang.saptest.tester;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TradeResult {
    LocalDateTime sDt;
    LocalDateTime eDt;
    long sBal;
    long eBal;
    float er;
    int tDays;
    String condision;
    List<TradeLog> logs;

    public TradeResult(){

    }

    public TradeResult(LocalDateTime sDt, LocalDateTime eDt, int tDays, long sBal, long eBal, float er){
        this.sDt= sDt;
        this.eDt = eDt;
        this.tDays = tDays;
        this.sBal = sBal;
        this.eBal = eBal;
        this.er = er;
    }

    public TradeResult addLogs(List<TradeLog> logs){
        this.logs = logs;
        return this;
    }

    public TradeResult addResult(TradeResult result){
        this.logs.addAll(result.getLogs());
        return this;
    }

    @Override
    public String toString(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        return "시작일["+sdf.format(sDt)+"],종료일["+sdf.format(eDt)+"],거래일["+tDays+"],금액["+eBal+"("+er+"%)]";
    }
}