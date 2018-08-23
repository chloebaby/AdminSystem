package com.mcit.AdmissionSystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@SpringBootApplication
@EnableAutoConfiguration
public class AdmissionSystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(AdmissionSystemApplication.class, args);
	}
}
