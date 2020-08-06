package io.hsjang.saptest;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.hsjang.saptest.repos.KrxRepository;
import io.hsjang.saptest.repos.SeriesRepository;
import io.hsjang.saptest.tester.SimpleTester;
import io.hsjang.saptest.model.Krx;
import io.hsjang.saptest.model.Series;

import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/api")
public class ApiController {

    @Autowired
    KrxRepository krxRepository;

    @Autowired
    SeriesRepository seriesRepository;

    @Autowired
    SimpleTester simpleTester;

    /**
     * krx
     */
    @RequestMapping(value="/krx", method=RequestMethod.GET)
    public List<Krx> krx() {
        return krxRepository.findAll();
    }

    /**
     * krx symbol
     */
    @RequestMapping(value="/krx/{symbol}", method=RequestMethod.GET)
    public Krx krx(@PathVariable String symbol) {
        return krxRepository.findBySymbol(symbol);
    }

    /**
     * series
     */
    @RequestMapping(value="/series/{symbol}", method=RequestMethod.GET)
    public List<Series> series(@PathVariable String symbol) {
        return seriesRepository.findBySymbol(symbol);
    }

    /**
     * series
     */
    @RequestMapping(value="/series1", method=RequestMethod.GET)
    public List<Series> series1() throws Exception{
        Calendar cal = Calendar.getInstance();
        cal.setTime(new SimpleDateFormat("yyyy-MM-dd").parse("2015-06-01"));
        cal.add(Calendar.HOUR, 9);
        return seriesRepository.findByDateAndChangeGreaterThanEqual(cal.getTime(), 0.29d);
    }

    /**
     * TEST1 
     */
    @RequestMapping(value="/test1", method=RequestMethod.GET)
    public TestResult series(@RequestParam Map<String,Object> params) throws Exception{
        Date startDt = new SimpleDateFormat("yyyyMMdd").parse("20150601"); // default: 2015-06-01
        Date endDt = new Date();   // default: now (데이터 마지막일)
        Long capital = 10000000L;   // default: 10,000,000원

        return simpleTester.test(startDt, endDt, capital, (sDt,eDt,c)->{
            /** 투자금 */
            Long money1 = c;

            Calendar cal = Calendar.getInstance();
            cal.setTime(startDt);
            /** 1. 시작일자 전날 상한가 종목 조회 */
            //seriesRepository.fin
            /** 2. 특정 조건에 따라 거래, 거래 내용 저장 (반복) */
            
            /** 3. 결과 리턴 */


            return new TestResult(sDt,eDt,c,c);
        });
    }
}