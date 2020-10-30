package com.cs203t5.ryverbank.customer;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import com.cs203t5.ryverbank.portfolio.Portfolio;
import com.cs203t5.ryverbank.portfolio.PortfolioRepository;

/**
 * The CustomerServiceImpl implements all the functionality required to create
 * users, get users and update user information.
 * 
 * @see CustomerService
 */
@Service
public class CustomerServiceImpl implements CustomerService {
    private CustomerRepository users;
    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    private PortfolioRepository portfolios;

    /**
     * Constructs a CustomerServiceImpl with the following parameters.
     * 
     * @param users      The customer repository.
     * @param portfolios The portfolio repository.
     */
    public CustomerServiceImpl(CustomerRepository users, PortfolioRepository portfolios) {
        this.users = users;
        this.portfolios = portfolios;
    }

    /**
     * Creates a new user. If the username exists in the customer repository, throw
     * a CustomerExistsException. If the phone number and nric is invalid, throw a
     * InvalidEntryException.
     * 
     * @param user The user to be created.
     * @return The user created.
     */
    @Override
    public Customer createUser(Customer user) {
        if (users.existsByUsername(user.getUsername())) {
            throw new CustomerExistsException("username used");
        } else if (!user.validateNric(user.getNric())) {
            throw new InvalidEntryException("Invalid NRIC");
        } else if (!user.validatePhone(user.getPhone())) {
            throw new InvalidEntryException("Invalid phone number");
        }
        users.save(user);
        Portfolio portfolio = new Portfolio(user.getCustomerId());
        user.setPortfolio(portfolio);
        portfolios.save(portfolio);
        return user;
    }

    /**
     * Gets the customer with the specified user id, authenticated username and
     * authenticated user role. If the user calling this method is not a manager or
     * the authenticated user, throw CustomerUnauthorizedException.
     * 
     * @param userId                The user id.
     * @param authenticatedUsername The authenticated username.
     * @param authenticatedUserRole The authenticated user role.
     * @return The customer found.
     */
    @Override
    public Customer getUser(Long userId, String authenticatedUsername, String authenticatedUserRole) {

        return users.findById(userId).map(user -> {
            if (authenticatedUserRole.equals("ROLE_USER") && user.getUsername().equals(authenticatedUsername)
                    || authenticatedUserRole.equals("ROLE_MANAGER")) {
                return users.save(user);
            } else {
                throw new CustomerUnauthorizedException(userId);
            }
        }).orElse(null);
    }

    /**
     * Updates the customer's address with the specified user id and new address
     * information. If no user is found, return null.
     * 
     * @param userId     The user id.
     * @param newAddress The user's new address information.
     * @return The customer with the updated information.
     */
    @Override
    public Customer updateAddress(Long userId, String newAddress) {
        if (newAddress != null && !newAddress.isEmpty()) {
            return users.findById(userId).map(user -> {
                user.setAddress(newAddress);
                return users.save(user);
            }).orElse(null);
        }

        return null;

    }

    /**
     * Updates the customer's phone number with the specified user id and new phone
     * number. If no user is found, return null.
     * 
     * @param userId   The user id.
     * @param newPhone The user's new phone information.
     * @return The customer with the updated information.
     */
    @Override
    public Customer updatePhone(Long userId, String newPhone) {
        if (newPhone != null && !newPhone.isEmpty()) {
            return users.findById(userId).map(user -> {
                user.setPhone(newPhone);
                return users.save(user);
            }).orElse(null);
        }

        return null;
    }

    /**
     * Updates the customer's password with the specified user id and new password .
     * If no user is found, return null.
     * 
     * @param userId      The user id.
     * @param newPassword The user's new password.
     * @return The customer with the updated information.
     */
    @Override
    public Customer updatePassword(Long userId, String newPassword) {
        if (newPassword != null && !newPassword.isEmpty()) {
            return users.findById(userId).map(user -> {
                user.setPassword(encoder.encode(newPassword));
                return users.save(user);
            }).orElse(null);
        }

        return null;
    }

    /**
     * Updates the user's active status with the specified user id and new active
     * status. If no user is found, return null.
     * 
     * @param userId       The user id.
     * @param activeStatus The user's new active status.
     * @return The customer with the updated active status.
     */
    @Override
    public Customer updateActiveStatus(Long userId, Boolean activeStatus) {
        if (activeStatus != null) {
            return users.findById(userId).map(user -> {
                user.setActive(activeStatus);
                return users.save(user);
            }).orElse(null);
        }

        return null;
    }

}
