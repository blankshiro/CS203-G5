package com.cs203t5.ryverbank;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.event.EventListener;

@SpringBootApplication
public class RyverbankApplication {

	@Autowired
	private SendEmailService sendEmailService;
	public static void main(String[] args) {
		SpringApplication.run(RyverbankApplication.class, args);
	}

	@EventListener
	public void triggerWhenStarts() {
		sendEmailService.sendEmail("edwin.tok.2019@smu.edu.sg", "test", "test");
	} 

}
