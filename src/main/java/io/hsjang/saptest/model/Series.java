package io.hsjang.saptest.model;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.Data;

@Entity
@Data
public class Series {
    
    @Id
    Date date;
    Long open;
    Long high;
    Long low;
    Long close;
    Long volume;
    Double change;
    String symbol;
}