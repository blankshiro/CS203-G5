package com.cs203t5.ryverbank.app;

import com.cs203t5.ryverbank.entity.User.*;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.ApplicationContext;

@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
// scanBasePackages = {"com.cs203t5.ryverbank", "com.cs203t5"},
// @EnableJpaRepositories({"com.cs203t5.entity.Transaction",
// "com.cs203t5.entity.User"})
// @ComponentScan({"com.cs203t5.entity.Transaction", "com.cs203t5.entity.User"})
// @EntityScan({"com.cs203t5.entity.Transaction", "com.cs203t5.entity.User"})
public class RyverbankApplication {
	public static void main(String[] args) {
		SpringApplication.run(RyverbankApplication.class, args);

		// JPA user repository init
		//UserRepository users = ctx.getBean(UserRepository.class);
		//BCryptPasswordEncoder encoder = ctx.getBean(BCryptPasswordEncoder.class);
		//System.out.println("[Add user]: "
				//+ users.save(new User("admin", encoder.encode("goodpassword").getUsername());
	}
}
