package com.cs203t5.ryverbank.customer;

import java.util.List;

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
     * The constructor for CustomerServiceImpl.
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
     * 
     * 
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

    // Updates the address of a particular user
    // This method will be used exclusively by Managers
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

    // Updates the address of a particular user
    // This method will be used exclusively by Customer
    // @Override
    // public Customer updateAddress(Long userId, String newAddress, String authenticatedUsername) {
    //     if (newAddress != null && !newAddress.isEmpty()) {
    //         return users.findById(userId).map(user -> {
    //             if (user.getUsername().equals(authenticatedUsername)) {
    //                 user.setAddress(newAddress);
    //                 return users.save(user);
    //             } else {
    //                 throw new CustomerUnauthorizedException("You do not have permission to access this information");
    //             }
    //         }).orElse(null);
    //     }

    //     return null;
    // }

    // Updates the phone of a particular user
    // This method will be used exclusively by Managers
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

    // Updates the phone of a particular user
    // This method will be used exclusively by Customer
    // @Override
    // public Customer updatePhone(Long userId, String newPhone, String authenticatedUsername) {
    //     if (newPhone != null && !newPhone.isEmpty()) {
    //         return users.findById(userId).map(user -> {
    //             if (user.getUsername().equals(authenticatedUsername)) {
    //                 user.setPhone(newPhone);
    //                 return users.save(user);
    //             } else {
    //                 throw new CustomerUnauthorizedException("You do not have permission to access this information");
    //             }
    //         }).orElse(null);
    //     }

    //     return null;
    // }

    // Updates the password of a particular user
    // This method will be used exclusively by Managers
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

    // Updates the password of a particular user
    // This method will be used exclusively by Customer
    // @Override
    // public Customer updatePassword(Long userId, String newPassword, String authenticatedUsername) {
    //     if (newPassword != null && !newPassword.isEmpty()) {
    //         return users.findById(userId).map(user -> {
    //             if (user.getUsername().equals(authenticatedUsername)) {
    //                 user.setPassword(encoder.encode(newPassword));
    //                 return users.save(user);
    //             } else {
    //                 throw new CustomerUnauthorizedException("You do not have permission to access this information");
    //             }
    //         }).orElse(null);
    //     }

    //     return null;
    // }

    // Updates the active field of a particular user
    // This method will be used exclusively by Managers
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
