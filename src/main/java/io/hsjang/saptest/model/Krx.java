package io.hsjang.saptest.model;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.Data;
import lombok.ToString;

@Entity
@Data
@ToString
public class Krx {
    
    @Id
    Long index;
    String symbol;
    String market;
    String name;
    String sector;
    String industry;
    LocalDateTime listingdate;
    String settlemonth;
    String representative;
    String homepage;
    String region;
}