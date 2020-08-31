package io.hsjang.saptest.repos.r2dbc;

import java.time.LocalDateTime;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;

import io.hsjang.saptest.model.Test;

@Repository
public interface TestR2Repository extends R2dbcRepository<Test,LocalDateTime> {
}