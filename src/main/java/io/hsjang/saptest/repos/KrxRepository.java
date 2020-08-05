package io.hsjang.saptest.repos;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import io.hsjang.saptest.model.Krx;

@Repository
public interface KrxRepository extends JpaRepository<Krx,Long> {
    public List<Krx> findBySymbol(String symbol);
}