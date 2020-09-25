package io.hsjang.saptest.model;

import java.io.Serializable;
import java.time.LocalDateTime;

class SeriesKey implements Serializable {
    private static final long serialVersionUID = 1L;
    LocalDateTime date;
    String symbol;
}