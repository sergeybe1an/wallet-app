package com.sbelan.walletapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication
public class WalletAppApplication extends SpringBootServletInitializer {

	public static void main(String[] args) {
		SpringApplication.run(WalletAppApplication.class, args);
	}
}
