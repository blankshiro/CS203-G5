package com.cs203t5.ryverbank.portfolio;

import java.util.Optional;

import com.cs203t5.ryverbank.customer.Customer;
import com.cs203t5.ryverbank.customer.CustomerRepository;
import com.cs203t5.ryverbank.customer.CustomerUnauthorizedException;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * A PorfolioController that accpes and returns portfolio JSON data.
 */
@RestController
public class PortfolioController {
    // private PortfolioRepository portfolios;
    private PortfolioService portfolioService;
    private CustomerRepository customers;

    /**
     * Constructs a Portfolio controller with the following parameters.
     * 
     * @param portfolios       The portfolio repository.
     * @param portfolioService The portfolio services.
     * @param customers        The customer repository.
     */
    public PortfolioController(PortfolioRepository portfolios, PortfolioService portfolioService,
            CustomerRepository customers) {
        // this.portfolios = portfolios;
        this.portfolioService = portfolioService;
        this.customers = customers;
    }

    /**
     * Search for the portfolio with the customer id. This method should only be
     * accessible by the authenticated user. If an unauthorized user uses this
     * method, it will throw a CustomerUnauthorizaedException.
     * 
     * @param auth Checks for user's authenticated username and user role.
     * @return The portfolio found.
     */
    @GetMapping("/portfolio")
    public Portfolio getPortfolio(Authentication auth) {
        String authenticatedUserRole = auth.getAuthorities().stream().findAny().get().getAuthority();

        String authenticatedUser = auth.getName();

        Optional<Customer> optional = customers.findByUsername(authenticatedUser);

        Customer customer = optional.get();
        // need to put unauthorize access for managers/analyst
        // need to make sure it is ROLE_USER
        if (authenticatedUserRole.equals("ROLE_MANAGER") || authenticatedUserRole.equals("ROLE_ANALYST")) {
            throw new CustomerUnauthorizedException(customer.getCustomerId());
        }

        return portfolioService.getPortfolio(customer.getCustomerId());
    }
}
