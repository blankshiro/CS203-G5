package com.cs203t5.ryverbank;

import com.cs203t5.ryverbank.customer.*;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
public class RyverbankApplication {
	public static void main(String[] args) {

		ApplicationContext ctx = SpringApplication.run(RyverbankApplication.class, args);

	// 	 JPA user repository init
	// 	UserRepository users = ctx.getBean(UserRepository.class);
    //     BCryptPasswordEncoder encoder = ctx.getBean(BCryptPasswordEncoder.class);
    //     System.out.println("[Add user]: " + users.save(
    //         new User("admin", "goodpassword", "test","test","test","test","test", "ROLE_ADMIN")).getUsername());
	}
}
