package com.cs203t5.ryverbank.customer;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class CustomerUnauthorizedException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public CustomerUnauthorizedException(Long id){
        super("Unauthorized access " + id);
    }

	public CustomerUnauthorizedException(String string) {
        super(string);
	}
}

