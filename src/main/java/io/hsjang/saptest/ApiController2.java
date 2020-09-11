package io.hsjang.saptest;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import io.hsjang.saptest.repos.r2dbc.KrxR2Repository;
import io.hsjang.saptest.repos.r2dbc.SeriesR2Repository;
import io.hsjang.saptest.repos.r2dbc.TestR2Repository;
import io.hsjang.saptest.tester.Tester2;
import io.hsjang.saptest.tester.Meta;
import io.hsjang.saptest.tester.TradeResult;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@RestController
@RequestMapping("/apir")
public class ApiController2 {

    @Autowired
    KrxR2Repository krxRepository;

    @Autowired
    SeriesR2Repository seriesRepository;
    
    @Autowired
    TestR2Repository testRepository;


    /**
     * krx
     */
    @RequestMapping(value="/krx", method=RequestMethod.GET)
    public Flux<Krx> krx() {
        return krxRepository.findAll();
    }
    /**
     * krx (cache map)
     */
    @RequestMapping(value="/krxm", method=RequestMethod.GET)
    public Map<String,String> krxcache() {
        return Meta.krxMap;
    }

    /**
     * krx symbol
     */
    @RequestMapping(value="/krx/{symbol}", method=RequestMethod.GET)
    public Mono<Krx> krx(@PathVariable String symbol) {
        return krxRepository.findBySymbol(symbol);
    }

    /**
     * series
     */
    @RequestMapping(value="/series/{symbol}", method=RequestMethod.GET)
    public Mono<Data> series(@PathVariable String symbol) {
        Mono<List<Series>> series = seriesRepository.findBySymbol(symbol).collectList();
        Mono<Krx> krx = krxRepository.findBySymbol(symbol);
        return Mono.zip(series,krx,(s,k)->new Data("info",k).add("series",s));
    }
    /**
     * series
     */
    @RequestMapping(value="/seriesd/{symbol}", method=RequestMethod.GET)
    public Mono<Series> seriesd(@PathVariable String symbol) throws Exception{
        Calendar cal = Calendar.getInstance();
        cal.setTime(new SimpleDateFormat("yyyy-MM-dd").parse("2020-08-21"));
        return seriesRepository.findByDateAndSymbol(LocalDateTime.ofInstant(cal.toInstant(), ZoneId.systemDefault()),symbol);
    }

    /**
     * series
     */
    @RequestMapping(value="/series1", method=RequestMethod.GET)
    public Flux<Series> series1() throws Exception{
        Calendar cal = Calendar.getInstance();
        cal.setTime(new SimpleDateFormat("yyyy-MM-dd").parse("2020-07-21"));
        LocalDateTime ldt = LocalDateTime.ofInstant(cal.toInstant(), ZoneId.systemDefault());
        return seriesRepository.findByDateAndChangeGreaterThanEqual(ldt, 0.29d);
    }

    /**
     * series
     */
    @RequestMapping(value="/ul/{dt}", method=RequestMethod.GET)
    public Flux<Data> ulsearch(@PathVariable String dt) throws Exception{
        Calendar cal = Calendar.getInstance();
        cal.setTime(new SimpleDateFormat("yyyy-MM-dd").parse(dt));
        return seriesRepository.findByDateAndChangeGreaterThanEqual(LocalDateTime.ofInstant(cal.toInstant(), ZoneId.systemDefault()), 0.29d)
                .map(s->
                    Data.of("name",Meta.krxMap.get(s.getSymbol()))
                    .add("symbol",s.getSymbol())
                    .add("change",s.getChange())
                );
    }

    @RequestMapping(value="/test/up", method=RequestMethod.GET)
    public Mono<TradeResult> testUp(@RequestParam Map<String,Object> params) throws Exception{
        String sDt = params.get("sDt").toString().replaceAll("-", "");
        String eDt = params.get("eDt").toString().replaceAll("-", "");
        // String lp = params.get("lp").toString();
        // String up = params.get("up").toString();
        // int bp = Integer.parseInt(params.get("bp").toString());
        return  new Tester2(seriesRepository, krxRepository)
            .setSDt(sDt)
            .setEDt(eDt)
            .start();
    }

    @RequestMapping(value="/test2", method=RequestMethod.GET)
    public String test2(@RequestParam Map<String,Object> params) throws Exception{
        
        Tester2 tester2 = new Tester2(seriesRepository, krxRepository);
        tester2.start();
        
        return "";
    }

    @RequestMapping(value="/test3", method=RequestMethod.GET)
    public String test3(@RequestParam Map<String,Object> params) throws Exception{
        Tester2 tester2 = new Tester2(seriesRepository, krxRepository);
        //tester2.fullTest();
        
        return "";
    }

    @RequestMapping(value="/test4", method=RequestMethod.GET)
    @ResponseBody
    public Mono<TradeResult> test4(@RequestParam Map<String,Object> params) throws Exception{
        Tester2 tester2 = new Tester2(seriesRepository, krxRepository);
        Mono<TradeResult> tr = tester2.start();
        

        File file = new File("log-logs.txt");
        FileWriter writer = null;

        try {
            writer = new FileWriter(file, true);
            /*
            for(TradeLog log: tr.getLogs()){
                for(String l: log.getLogs()){
                    System.out.println(l);
                    writer.write(l+"\n");
                }
            }
            */
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

    @RequestMapping(value="/test5", method=RequestMethod.GET)
    @ResponseBody
    public Mono<TradeResult> test5(@RequestParam Map<String,Object> params) throws Exception{
        return new Tester2(seriesRepository, krxRepository)
            .setSDt("2020-06-29")
            .setEDt("2020-07-07")
            .start();
    }

    
}