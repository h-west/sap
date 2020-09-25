package io.hsjang.saptest.model;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;

import lombok.Data;
import lombok.ToString;

@Entity
@Data
@IdClass(SeriesKey.class)
@ToString
public class Series implements Serializable{
    private static final long serialVersionUID = 1L;

    @Id
    LocalDateTime date;
    @Id
    String symbol;
    Long open;
    Long high;
    Long low;
    Long close;
    Long volume;
    Double change;
}