package io.hsjang.saptest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.hsjang.saptest.repos.KrxRepository;
import io.hsjang.saptest.repos.SeriesRepository;
import io.hsjang.saptest.model.Krx;
import io.hsjang.saptest.model.Series;

import org.springframework.web.bind.annotation.RequestMethod;


@RestController
@RequestMapping("/api")
public class ApiController {

    @Autowired
    KrxRepository krxRepository;

    @Autowired
    SeriesRepository seriesRepository;

    /**
     * stocks
     */
    @RequestMapping(value="/stocks", method=RequestMethod.GET)
    public List<Krx> stocks() {
        return krxRepository.findAll();
    }

    /**
     * stock info
     */
    @RequestMapping(value="/stocks/{symbol}", method=RequestMethod.GET)
    public List<Krx> stocks(@PathVariable String symbol) {
        return krxRepository.findBySymbol(symbol);
    }

    /**
     * histories
     */
    @RequestMapping(value="/hisotries/{symbol}", method=RequestMethod.GET)
    public List<Series> hisotries(@PathVariable String symbol) {
        return seriesRepository.findBySymbol(symbol);
    }
    
}