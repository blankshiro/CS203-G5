package com.cs203t5.ryverbank.customer;

import java.util.List;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class CustomerServiceImpl implements CustomerServices {
    /** The Customer Repository */
    private CustomerRepository users;
    
    /** BCryptPasswordEncoder */
    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    /**
     * The constructor for CustomerServiceImpl.
     * @param users The Customer Repository.
     */
    public CustomerServiceImpl(CustomerRepository users) {
        this.users = users;
    }

    @Override
    public List<Customer> listUsers() {
        return users.findAll();
    }

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
    @Override
    public Customer updateAddress(Long userId, String newAddress, String authenticatedUsername) {
        if (newAddress != null && !newAddress.isEmpty()) {
            return users.findById(userId).map(user -> {
                if (user.getUsername().equals(authenticatedUsername)) {
                    user.setAddress(newAddress);
                    return users.save(user);
                } else {
                    throw new CustomerUnauthorizedException("You do not have permission to access this information");
                }
            }).orElse(null);
        }

        return null;
    }

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
    @Override
    public Customer updatePhone(Long userId, String newPhone, String authenticatedUsername) {
        if (newPhone != null && !newPhone.isEmpty()) {
            return users.findById(userId).map(user -> {
                if (user.getUsername().equals(authenticatedUsername)) {
                    user.setPhone(newPhone);
                    return users.save(user);
                } else {
                    throw new CustomerUnauthorizedException("You do not have permission to access this information");
                }
            }).orElse(null);
        }

        return null;
    }

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
    @Override
    public Customer updatePassword(Long userId, String newPassword, String authenticatedUsername) {
        if (newPassword != null && !newPassword.isEmpty()) {
            return users.findById(userId).map(user -> {
                if (user.getUsername().equals(authenticatedUsername)) {
                    user.setPassword(encoder.encode(newPassword));
                    return users.save(user);
                } else {
                    throw new CustomerUnauthorizedException("You do not have permission to access this information");
                }
            }).orElse(null);
        }

        return null;
    }

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

    @Override
    public Customer createUser(Customer user) {
        if (users.existsByUsername(user.getUsername())) {
            throw new CustomerExistsException("username used");
        }

        return users.save(user);
    }

}
