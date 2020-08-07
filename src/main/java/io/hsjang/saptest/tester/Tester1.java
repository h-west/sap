package io.hsjang.saptest.tester;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import io.hsjang.saptest.model.Series;

public class Tester1 {
    Long balance;
    Map<String,Stock> stocks;
    
    // 전일 거래 정보
    List<Series> temp1;
    // 입력 정보
    List<Series> series;

    // 후보(매수) 리스트
    public List<Candidate> getCandidates(){
        return temp1.stream().map(s->new Candidate()).collect(Collectors.toList());
        // 전일 거래정보가 없거나 상한가 정보가 없으면 new ArrayList<Candidate>();  // size가 0이되면 그냥 temp1에 저장하고 종료
    }

    public void trade(List<Candidate> cadidates){
        // 매매 구현
    }

    public void test(List<Series> series){
        this.series = series;
        List<Candidate> candidates = getCandidates();
        if(candidates.size()>0){
            trade(candidates);
        }
        // 임시저장
        temp1 = series;
    }
}