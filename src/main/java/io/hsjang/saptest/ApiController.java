package io.hsjang.saptest;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import io.hsjang.saptest.model.Krx;
import io.hsjang.saptest.model.Series;
import io.hsjang.saptest.repos.KrxRepository;
import io.hsjang.saptest.repos.SeriesRepository;
import io.hsjang.saptest.tester.Tester1;
import io.hsjang.saptest.tester.TradeLog;
import io.hsjang.saptest.tester.TradeResult;


@RestController
@RequestMapping("/api")
public class ApiController {

    @Autowired
    KrxRepository krxRepository;

    @Autowired
    SeriesRepository seriesRepository;

    @Autowired
    Tester1 tester1;

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

    @RequestMapping(value="/test2", method=RequestMethod.GET)
    public String test2(@RequestParam Map<String,Object> params) throws Exception{
        
        tester1.start("20200725", 10000000L);
        
        return "";
    }

    @RequestMapping(value="/test3", method=RequestMethod.GET)
    public String test3(@RequestParam Map<String,Object> params) throws Exception{
        
        tester1.fullTest();
        
        return "";
    }

    @RequestMapping(value="/test4", method=RequestMethod.GET)
    @ResponseBody
    public TradeResult test4(@RequestParam Map<String,Object> params) throws Exception{
        

        TradeResult tr = tester1.start("20200409","20200805", 10000000L, 70, "-1:9");
        

        File file = new File("log-logs.txt");
        FileWriter writer = null;

        try {
            writer = new FileWriter(file, true);
            for(TradeLog log: tr.getLogs()){
                for(String l: log.getLogs()){
                    System.out.println(l);
                    writer.write(l+"\n");
                }
            }
            writer.flush();

        } catch(IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if(writer != null) writer.close();
            } catch(IOException e) {
                e.printStackTrace();
            }
        }

        return tr;
    }
}