package com.cs203t5.ryverbank.customer;

import java.util.List;

// import com.cs203t5.ryverbank.token.*;

public interface CustomerService {
    List<Customer> listUsers();

    Customer getUser(Long userId);

    Customer addUser(Customer user);

    Customer updateUser(Long userId, Customer user);

    void deleteUser(Long userId);

    Customer register(Customer user);

    // void sendEmail(String toUser, String token);
}
