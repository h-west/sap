package io.hsjang.saptest;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.InitializingBean;
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
public class ApiController implements InitializingBean{

    @Autowired
    KrxRepository krxRepository;

    @Autowired
    SeriesRepository seriesRepository;

    @Autowired
    Tester1 tester1;

    Map<String,String> krxMap;

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
    public Data series(@PathVariable String symbol) {
        Data result = new Data("series",seriesRepository.findBySymbol(symbol));
        return result.add("info",krxRepository.findBySymbol(symbol));
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
     * series
     */
    @RequestMapping(value="/ul/{dt}", method=RequestMethod.GET)
    public List<Data> ulsearch(@PathVariable String dt) throws Exception{
        Calendar cal = Calendar.getInstance();
        cal.setTime(new SimpleDateFormat("yyyy-MM-dd").parse(dt));
        cal.add(Calendar.HOUR, 9);
        return seriesRepository.findByDateAndChangeGreaterThanEqual(cal.getTime(), 0.29d)
                .stream()
                .map(s->
                    Data.of("name",krxMap.get(s.getSymbol()))
                    .add("symbol",s.getSymbol())
                    .add("change",s.getChange())
                ).collect(Collectors.toList());
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

    @Override
    public void afterPropertiesSet() throws Exception {
        List<Krx> krxs = krxRepository.findAll();
        krxMap = new HashMap<String,String>();
        for(Krx krx: krxs){
            krxMap.put(krx.getSymbol(), krx.getName());
        }
    }
}