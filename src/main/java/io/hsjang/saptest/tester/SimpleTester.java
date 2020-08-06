package io.hsjang.saptest.tester;

import java.util.Date;

import org.springframework.stereotype.Service;

import io.hsjang.saptest.TestResult;
import io.hsjang.saptest.Tester;

@Service
public class SimpleTester {

    public TestResult test(Date startDt, Date endDt, Long capital, Tester t){
        return t.start(startDt, endDt, capital);
    }
}