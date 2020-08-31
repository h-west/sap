package io.hsjang.saptest.repos.rdbc;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import io.hsjang.saptest.model.Select;
import io.hsjang.saptest.model.Series;


@Repository
public interface SeriesRepository extends JpaRepository<Series,Long> {
    public List<Series> findBySymbol(String symbol);
    public List<Series> findByDateAndChangeGreaterThanEqual(Date date, Double change);
    public Series findByDateAndSymbol(Date date, String symbol);
    
    @Query("SELECT new io.hsjang.saptest.model.Select(s.date) FROM Series s GROUP BY s.date ORDER BY s.date")
    public List<Select> findSeriesDateList();
}