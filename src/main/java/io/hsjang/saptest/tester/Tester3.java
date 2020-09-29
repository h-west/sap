package io.hsjang.saptest.tester;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import io.hsjang.saptest.TestResult;
import io.hsjang.saptest.repos.r2dbc.KrxR2Repository;
import io.hsjang.saptest.repos.r2dbc.SeriesR2Repository;
import lombok.Data;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuples;

@Data
public class Tester3 {
    
    KrxR2Repository krxRepository;
    SeriesR2Repository seriesRepository;

    // condition
    long balance = 10000000L;
    LocalDateTime sDt = LocalDateTime.parse("2015010100", DateTimeFormatter.ofPattern("yyyyMMddHH"));
    LocalDateTime eDt = LocalDateTime.now();
    int buyingRatio = 100;
    int sellingUpperRatio = 7;
    int sellingUnderRatio = -1;
    int maxStoredDays = 30;

    public Tester3(KrxR2Repository krxRepository, SeriesR2Repository seriesRepository){
        this.krxRepository = krxRepository;
        this.seriesRepository = seriesRepository;
    }

    public Tester3(KrxR2Repository krxRepository, SeriesR2Repository seriesRepository, String sDt){
        this(krxRepository, seriesRepository);
        this.sDt = LocalDateTime.parse(sDt+"00", DateTimeFormatter.ofPattern("yyyyMMddHH"));;
    }

    public Mono<TestResult> start(){
        return seriesRepository.findSeriesDateList()
            .map(series->(LocalDateTime)series.getDate())
            .filter(dt->dt.isAfter(sDt)&&dt.isBefore(eDt)||dt.isEqual(sDt)||dt.isEqual(eDt))
            //.flatMap(dt->seriesRepository.findPreDay(dt).map(s->Tuples.of((LocalDateTime)s.getDate(),dt)))
            .flatMap(this::dailyTrade)
            .collect(Collectors.toMap(dr->dr.getDt(), dr->dr, (d1,d2)->d1.merge(d2)))
            .flatMapMany(map->Flux.fromIterable(map.values()))
            .collectSortedList((d1,d2)->d1.getDt().compareTo(d2.getDt()))
            .map(this::simulate)
            .doOnError(s->System.out.println("EEE1>"+s));
    }

    public Flux<DailyResult> dailyTrade(LocalDateTime dt){
        return seriesRepository.findPreDay(dt)
                .map(s->Tuples.of((LocalDateTime)s.getDate(),dt))
                .flatMapMany(days->
                    seriesRepository.findUpLimits(days.getT1(), 0.29)
                        .doOnNext(candidate->candidate.setDate(days.getT2()))
                        //.doOnNext(candidate->System.out.println("@@@>"+candidate))
                        .flatMap(candidate->
                            seriesRepository.findByDateAndSymbol(candidate.getDate(), candidate.getSymbol())
                                .filter(target->target.getOpen()>0&&target.getVolume()>0)
                                .flatMapMany(target->{
                                    long buyingPrice = target.getOpen();
                                    long sellingUpperPrice =  (long) (buyingPrice * (1+(float)sellingUpperRatio/100));
                                    long sellingUnderPrice =  (long) (buyingPrice * (1+(float)sellingUnderRatio/100));
                    
                                    DailyResult dr = new DailyResult(candidate.getDate());
                                    dr.setBuyingPrice(buyingPrice);

                                    TradeLog log = new TradeLog();
                                    log.setDt(target.getDate());
                                    log.setSymbol(target.getSymbol());
                                    log.setBuyingDt(target.getDate());
                                    log.setBuyingPrice(buyingPrice);
                                    

                                    if(sellingUnderPrice>target.getLow()){
                                        dr.setSellingPrice(sellingUnderPrice);
                                        log.setSellingDt(target.getDate());
                                        log.setSellingPrice(sellingUnderPrice);
                                    }else if(sellingUpperPrice<target.getHigh()){
                                        dr.setSellingPrice(sellingUpperPrice);
                                        log.setSellingDt(target.getDate());
                                        log.setSellingPrice(sellingUpperPrice);
                                    }else{
                                        //홀딩
                                        dr.setSellingPrice(target.getClose());
                                        log.setSellingDt(target.getDate());
                                        log.setSellingPrice(target.getClose());
                                        //log.setDt(target.getDate());
                                        // flux 2  (today, selling dt)
                                    }
                                    dr.add(log);
                                    return Flux.just(dr);
                                })
                        )
                );
    }

    public TestResult simulate(List<DailyResult> drs){
        TestResult result = new TestResult(drs);
        result.setAsset(balance);
        for(DailyResult dr: drs){
            balance *= dr.getRoi();
        }
        result.setBalance(balance);
        result.setRoi();
        return result;
    }

}
