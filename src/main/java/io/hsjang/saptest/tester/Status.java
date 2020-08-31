package io.hsjang.saptest.tester;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Status {
    
    List<LocalDateTime> dtRange;
    int nIdx;
    long balance;
    Map<String,Stock> stocks = new HashMap<String,Stock>();

    
}