package io.hsjang.saptest.repos.r2dbc;

import java.time.LocalDateTime;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import io.hsjang.saptest.model.Series;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@Repository
public interface SeriesR2Repository extends R2dbcRepository<Series,Long> {
    public Flux<Series> findBySymbol(String symbol);
    //public Mono<Long> countByDateAndChangeGreaterThanEqual(LocalDateTime date, Double change);
    public Flux<Series> findByDateAndChangeGreaterThanEqual(LocalDateTime date, Double change);
    public Mono<Series> findByDateAndSymbol(LocalDateTime date, String symbol);
    
    //@Query("SELECT new io.hsjang.saptest.model.Select(s.date) FROM Series s GROUP BY s.date ORDER BY s.date")
    //public Flux<Select> findSeriesDateList();

    @Query("SELECT date FROM series GROUP BY date ORDER BY date")
    public Flux<Series> findSeriesDateList();

    @Query("SELECT MAX(s.date) date FROM series s WHERE s.date<:date")
    public Mono<Series> findPreDay(LocalDateTime dt);

    @Query("SELECT s.* FROM series s WHERE s.date=:date and s.change>=:change")
    public Flux<Series> findUpLimits(@Param("date")LocalDateTime date, @Param("change")Double change);

    @Query("SELECT count(s.date) FROM series s WHERE s.date=:date and s.change>= :change")
    public Mono<Long> countDateAndChangeGreaterThanEqual(@Param("date")LocalDateTime date, @Param("change")Double change);
}