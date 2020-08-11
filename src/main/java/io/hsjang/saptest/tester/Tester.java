package io.hsjang.saptest.tester;

import java.util.Date;
import java.util.List;

public interface Tester {
 
    public List<TradeLog> start(Date sDt, Date eDt, Long balance);
}