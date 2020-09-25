package io.hsjang.saptest.tester;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

import javax.persistence.Tuple;

import org.springframework.format.annotation.DateTimeFormat;

import io.hsjang.saptest.TestResult;
import io.hsjang.saptest.model.Series;
import io.hsjang.saptest.repos.r2dbc.KrxR2Repository;
import io.hsjang.saptest.repos.r2dbc.SeriesR2Repository;
import lombok.Data;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
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
    int sellingUpperRatio = 5;
    int sellingUnderRatio = -5;
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
            //.doOnNext(s->System.out.println("MMMMMMM>"+s))
            .groupBy(dr->dr.getDt())
            //.map(f->{f.})
            //.doOnNext(s->s.subscribe(a->System.out.println("BBBBB>"+a)))
            //.collect(DailyResult::new,(a,b)->a.merge(b))
            .collectList()
            .map(list->{
                System.out.println("DAY LIST>>"+list);
                
                return new TestResult();
            }).doOnError(s->System.out.println("EEE1>"+s));
            // .collect(Collectors.toList()
            // .collectMultimap(dr->dr.getDt())
            // .map(map->map.);
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
                                    //dr.setBuyingPrice(buyingPrice);

                                    TradeLog log = new TradeLog();
                                    log.setSymbol(target.getSymbol());
                                    log.setBuyingDt(target.getDate());
                                    log.setBuyingPrice(buyingPrice);
                                    

                                    if(sellingUnderPrice>target.getLow()){
                                        //dr.setSellingPrice(sellingUnderPrice);
                                        log.setSellingDt(target.getDate());
                                        log.setSellingPrice(sellingUnderPrice);
                                    }else if(sellingUpperPrice<target.getHigh()){
                                        //dr.setSellingPrice(sellingUpperPrice);
                                        log.setSellingDt(target.getDate());
                                        log.setSellingPrice(sellingUpperPrice);
                                    }else{
                                        //홀딩
                                        //dr.setSellingPrice(target.getClose());
                                        log.setSellingDt(target.getDate());
                                        log.setSellingPrice(target.getClose());
                                        // flux 2  (today, selling dt)
                                    }
                                    dr.add(log);
                                    return Flux.just(dr);
                                })
                        )
                );
                
                /*
                .flatMap(days->
                    seriesRepository.findUpLimits(days.getT1(), 0.29)
                        .map(candidate->{
                            candidate.setDate(days.getT2());
                            return candidate;
                        })
                        .map(this::trade)
                );*/
    }

    public Flux<Series> getCandidate(Tuple2<LocalDateTime,LocalDateTime> days){
        return seriesRepository.findUpLimits(days.getT1(), 0.29)
                .map(candidate->{
                    candidate.setDate(days.getT2());
                    return candidate;
                }); 
    }

    public Flux<DailyResult> trade(Series candidate){
        return seriesRepository.findByDateAndSymbol(candidate.getDate(), candidate.getSymbol())
            .flatMapMany(target->{
                long buyingPrice = target.getOpen();
                long sellingUpperPrice =  buyingPrice * sellingUpperRatio/100;
                long sellingUnderPrice =  buyingPrice * sellingUnderRatio/100;

                DailyResult dr = new DailyResult(candidate.getDate());
                dr.setBuyingPrice(buyingPrice);
                
                if(sellingUnderPrice>target.getLow()){
                    dr.setSellingPrice(sellingUnderPrice);
                }else if(sellingUpperPrice<target.getHigh()){
                    dr.setSellingPrice(sellingUpperPrice);
                }else{
                    //홀딩
                    dr.setSellingPrice(target.getClose());
                    // flux 2  (today, selling dt)
                }
                return Flux.just(dr);
            });
    }

    /*
                                return seriesRepository.findByDateAndSymbol(today, candidate.getSymbol())
                                        .flatMap(target->{
                                            long buyingPrice = target.getOpen();
                                            long sellingUpperPrice =  buyingPrice * sellingUpperRatio/100;
                                            long sellingUnderPrice =  buyingPrice * sellingUnderRatio/100;

                                            DailyResult dr = new DailyResult(today);
                                            dr.setBuyingPrice(buyingPrice);
                                            
                                            if(sellingUnderPrice>target.getLow()){
                                                dr.setSellingPrice(sellingUnderPrice);
                                            }else if(sellingUpperPrice<target.getHigh()){
                                                dr.setSellingPrice(sellingUpperPrice);
                                            }else{
                                                //홀딩
                                                dr.setSellingPrice(target.getClose());
                                                // flux 2  (today, selling dt)
                                            }
                                            return dr;
                                        });
                            );
                            */


    // public Flux<DailyResult> dailyTrade(Tuple2<LocalDateTime,LocalDateTime> dts){
    //     LocalDateTime preday = dts.getT1();
    //     LocalDateTime today = dts.getT2();
    //     return seriesRepository.findUpLimits(preday, 0.29)
    //             .
    //             .map(s-> new DailyResult(dt))
    //             .defaultIfEmpty(new DailyResult(dt)).doOnError(s->System.out.println("EEE>"+s));
    // }

}
