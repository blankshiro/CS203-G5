package com.cs203t5.ryverbank.configuration;

import io.swagger.v3.oas.annotations.*;
import io.swagger.v3.oas.annotations.info.*;
import io.swagger.v3.oas.annotations.servers.Server;

@OpenAPIDefinition(
  info = @Info(
  title = "Ryver bank Application",
  description = "Welcome to Ryver bank, for our Ryver Bank employees,"+
  " we also have a plan for the Ryverbank. We have a Ryver Bank, we have a together "+
  "and Ryver Bank plan. We care at the Ryver Bank",
    contact = @Contact(
    name = "CS203 Group 5", 
    url = "https://github.com/blankshiro/CS203-G5", 
    email = "planfor.ryverbank@gmail.com"
  ),
  license = @License(
    name = "We have no license, so I'm not sure what I should put here", 
    url = "https://www.youtube.com/watch?v=dQw4w9WgXcQ")),
  servers = @Server(url = "http://localhost:8080")
)
public class OpenAPIConfig {
    
}
