package io.hsjang.saptest.tester.uplimit;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.hsjang.saptest.model.Series;
import io.hsjang.saptest.repos.SeriesRepository;
import io.hsjang.saptest.tester.Status;

@Service
public class UplimitTester implements InitializingBean {

    Status status;
    //Balance balance;
    List<Date> openDays;

    @Autowired
    SeriesRepository seriesRepository;

    public void start(Date from, Date to, Long cash) {
        status = new Status(openDays, from, to, cash);
        //balance = new Balance(cash);
        trade();
    }

    public void trade() {
        // candidate
        List<Series> candidates = seriesRepository.findByDateAndChangeGreaterThanEqual(status.getYesterday(), 0.29d);
        //Map<String,Series> dUps = new HashMap<String,Series>();
        Long buyable = status.getBalance()/candidates.size();
        candidates.stream().forEach(candidate->{
            //dUps.put(y.getSymbol(),seriesRepository.findByDateAndSymbol(status.getCurrent(), y.getSymbol()));
            Series d = seriesRepository.findByDateAndSymbol(status.getCurrent(), candidate.getSymbol());
            //Long yClose = candidate.getClose();
            Long open = d.getOpen();

            int c = (int)(buyable/open);
            status.buy(candidate.getSymbol(), open, c);
        });
       

        // buy
        // 1. 시가
        // 2. 시가가 5%하락시
        // 3. 장중 5%하락시


        //next();
    }

    public void end() {
        //
    }

    public void next() {
        if (status.next()) {
            trade();
        } else {
            end();
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        openDays = seriesRepository.findSeriesDateList().stream().map(s->(Date)s.get(0)).collect(Collectors.toList());
    }

}