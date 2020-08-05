package io.hsjang.saptest.model;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.Data;

@Data
@Entity
public class Krx {
    
    @Id
    Long index;
    String symbol;
    String market;
    String name;
    String sector;
    String industry;
    Date listingdate;
    String settlemonth;
    String representative;
    String homepage;
    String region;
}