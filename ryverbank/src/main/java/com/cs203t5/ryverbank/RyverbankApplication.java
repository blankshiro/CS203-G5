package com.cs203t5.ryverbank;

import javax.servlet.http.HttpSession;

import com.cs203t5.ryverbank.customer.*;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.context.request.RequestContextHolder;

@SpringBootApplication
public class RyverbankApplication {
	public static void main(String[] args) {

		ApplicationContext ctx = SpringApplication.run(RyverbankApplication.class, args);

		// JPA user repository init
		//create a manager account 
		//to be filled in with address, NRIC, full name etc
		CustomerRepository users = ctx.getBean(CustomerRepository.class);
		BCryptPasswordEncoder encoder = ctx.getBean(BCryptPasswordEncoder.class);
		System.out.println("[Add admin]: " + users.save(
			new Customer("manager", encoder.encode("goodpassword"),null, null, null, null, "ROLE_MANAGER", true)).getUsername());
		
		System.out.println("[Add analyst]: " + users.save(
			new Customer("analyst", encoder.encode("goodpassword"),null, null, null, null, "ROLE_ANALYST", true)).getUsername());

		System.out.println("[Add user]: " + users.save(
			new Customer("user1", encoder.encode("goodpassword"),null, null, null, null, "ROLE_USER", true)).getUsername());
			

	// 	 JPA user repository init
	// 	UserRepository users = ctx.getBean(UserRepository.class);
    //     BCryptPasswordEncoder encoder = ctx.getBean(BCryptPasswordEncoder.class);
    //     System.out.println("[Add user]: " + users.save(
    //         new User("admin", "goodpassword", "test","test","test","test","test", "ROLE_ADMIN")).getUsername());
	}
}
