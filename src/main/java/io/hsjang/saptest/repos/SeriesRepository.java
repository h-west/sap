package io.hsjang.saptest.repos;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import io.hsjang.saptest.model.Series;


@Repository
public interface SeriesRepository extends JpaRepository<Series,Long> {
    public List<Series> findBySymbol(String symbol);
    public List<Series> findByDateAndChangeGreaterThanEqual(Date date, Double change);
}