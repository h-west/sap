package io.hsjang.saptest.tester;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.hsjang.saptest.repos.r2dbc.KrxR2Repository;
import io.hsjang.saptest.repos.r2dbc.SeriesR2Repository;

@Service
public class Meta implements InitializingBean{

    public static List<LocalDateTime> openDays;
    public static Map<String,String> krxMap;

    @Autowired
    SeriesR2Repository seriesRepository;
    
    @Autowired
    KrxR2Repository krxRepository;

    @Override
    public void afterPropertiesSet() throws Exception {
        seriesRepository.findSeriesDateList()
            .map(s->(LocalDateTime)s.getDate())
            .collectList()
            .subscribe(l->Meta.openDays=l);

        Meta.krxMap = new HashMap<String,String>();
        krxRepository.findAll().subscribe(krx->krxMap.put(krx.getSymbol(), krx.getName()));
    }

    public static String getSymbolName(String symbol){
        return krxMap.get(symbol);
    }
    
    
}
