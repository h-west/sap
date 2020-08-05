package io.hsjang.saptest.model;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.Data;

@Data
@Entity
public class Series {
    
    @Id
    Date date;
    Long open;
    Long high;
    Long low;
    Long close;
    Long volume;
    Long change;
    String symbol;
}