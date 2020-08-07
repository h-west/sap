package io.hsjang.saptest.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;

import lombok.Data;

@Entity
@Data
@IdClass(SeriesKey.class)
public class Series implements Serializable{
    private static final long serialVersionUID = 1L;

    @Id
    Date date;
    @Id
    String symbol;
    Long open;
    Long high;
    Long low;
    Long close;
    Long volume;
    Double change;
}
class SeriesKey implements Serializable{
    private static final long serialVersionUID = 1L;
    Date date;
    String symbol;
}