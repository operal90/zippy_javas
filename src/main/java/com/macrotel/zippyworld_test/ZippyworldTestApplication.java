package com.macrotel.zippyworld_test;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
public class ZippyworldTestApplication {

	public static void main(String[] args) {
		SpringApplication.run(ZippyworldTestApplication.class, args);
	}

}
