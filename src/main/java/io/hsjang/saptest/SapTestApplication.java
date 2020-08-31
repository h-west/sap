package io.hsjang.saptest;

import javax.sql.DataSource;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;
import org.springframework.web.reactive.config.WebFluxConfigurer;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "io.hsjang.saptest.repos.rdbc")
@EnableR2dbcRepositories(basePackages = "io.hsjang.saptest.repos.r2dbc")
public class SapTestApplication implements WebFluxConfigurer{

	public static void main(String[] args) {
		//SpringApplication app = new SpringApplication(SapTestApplication.class);
		//app.setDefaultProperties(Collections.singletonMap("server.port", "8081"));
		//app.run(args);
		SpringApplication.run(SapTestApplication.class, args);
	}

	@Bean
	public DataSource dataSource(){
		return DataSourceBuilder
				.create()
				.username("root")
				.password("hsjang11")
				.url("jdbc:mysql://localhost:3306/stocks?charset=utf8")
				.driverClassName("com.mysql.cj.jdbc.Driver")
				.build();
	}

	@Override
	public void addResourceHandlers(org.springframework.web.reactive.config.ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/**").addResourceLocations("classpath:/static/");
	}
}
