package io.hsjang.saptest;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.hsjang.saptest.model.Krx;
import io.hsjang.saptest.model.Series;
import io.hsjang.saptest.repos.KrxRepository;
import io.hsjang.saptest.repos.SeriesRepository;
import io.hsjang.saptest.tester.uplimit.UplimitTester;


@RestController
@RequestMapping("/api")
public class ApiController {

    @Autowired
    KrxRepository krxRepository;

    @Autowired
    SeriesRepository seriesRepository;

    @Autowired
    UplimitTester uplimitTester;

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
    public String series(@RequestParam Map<String,Object> params) throws Exception{
        Date startDt = new SimpleDateFormat("yyyyMMddHH").parse("2020080109"); // default: 2015-06-02
        Date endDt = new Date();   // default: now (데이터 마지막일)
        Long capital = 10000000L;   // default: 10,000,000원
        
        uplimitTester.start(startDt, endDt, capital);
        
        return "";
    }
}