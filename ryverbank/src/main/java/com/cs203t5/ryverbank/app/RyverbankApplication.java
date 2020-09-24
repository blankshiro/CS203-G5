package com.cs203t5.ryverbank.app;

//import com.cs203t5.ryverbank.service.*;
//import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
// import org.springframework.boot.context.event.ApplicationReadyEvent;
// import org.springframework.context.event.EventListener;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {"com.cs203t5.ryverbank", "com.cs203t5"})
@EnableJpaRepositories({"com.cs203t5.entity.Transaction", "com.cs203t5.entity.User", "com.cs203t5.entity.Account"})
@ComponentScan({"com.cs203t5.entity.Transaction", "com.cs203t5.entity.User","com.cs203t5.entity.Account"})
@EntityScan({"com.cs203t5.entity.Transaction", "com.cs203t5.entity.User","com.cs203t5.entity.Account"})
public class RyverbankApplication {

	// @Autowired
	// private SendEmailService sendEmailService;

	public static void main(String[] args) {
		SpringApplication.run(RyverbankApplication.class, args);
	}

	// @EventListener(ApplicationReadyEvent.class)
	// public void triggerWhenStarts() {
	// 	sendEmailService.sendEmail("derrick.lim.2019@sis.smu.edu.sg", "if the email service is working, you will receive this email. PS: Megumin best girl", "pls work");
	// }
}
