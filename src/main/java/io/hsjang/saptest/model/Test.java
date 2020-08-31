package io.hsjang.saptest.model;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

@Entity
@Data
@ToString
@AllArgsConstructor
public class Test {
    
    @Id
    LocalDateTime date;
    String str;
}