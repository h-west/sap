package io.hsjang.saptest.repos.r2dbc;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;

import io.hsjang.saptest.model.Krx;
import reactor.core.publisher.Mono;

@Repository
public interface KrxR2Repository extends R2dbcRepository<Krx,Long> {
    public Mono<Krx> findBySymbol(String symbol);
}