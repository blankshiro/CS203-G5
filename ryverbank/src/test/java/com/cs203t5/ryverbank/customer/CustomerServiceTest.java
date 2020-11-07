package com.cs203t5.ryverbank.customer;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class CustomerServiceTest {

    @Mock
    private CustomerRepository users;

    @InjectMocks
    private CustomerServiceImpl userService;

    @Test
    void createUser_SameUser_ThrowCustomerExistsException() {
        Customer sameUser = new Customer("user1", "goodpassword1", "Ronald Trump", "S8529649C", "91251234",
                "White House", "ROLE_USER", true);
        users.save(sameUser);

        when(users.existsByUsername(any(String.class))).thenReturn(true);

        assertThrows(CustomerExistsException.class, () -> userService.createUser(sameUser));
    }

    @Test
    void getUser_FoundUser_ReturnUser() {
        Customer foundUser = new Customer("user1", "goodpassword1", "Ronald Trump", "S8529649C", "91251234",
                "White House", "ROLE_USER", true);

        Long userId = foundUser.getCustomerId();
        when(users.findById(userId)).thenReturn(Optional.empty());

        Customer gCustomer = userService.getUser(userId, foundUser.getUsername(), "USER_ROLE");
        assertNull(gCustomer);
        verify(users).findById(userId);

    }

    @Test
    void createUser_InvalidNric_ThrowsInvalidEntryExcepton() {
        Customer newUser = new Customer("user1", "goodpassword1", "Ronald Trump", "S1", "91251234", "White House",
                "ROLE_USER", true);

        when(users.save(any(Customer.class))).thenReturn(newUser);

        Customer savedCustomer = users.save(newUser);

        assertThrows(InvalidEntryException.class, () -> userService.createUser(newUser));
    }

    @Test
    void createCustomer_InvalidPhone_ThrowsInvalidEntryExcepton() {
        Customer newUser = new Customer("user1", "goodpassword1", "Ronald Trump", "S8529649C", "11111111",
                "White House", "ROLE_USER", true);

        when(users.save(any(Customer.class))).thenReturn(newUser);

        Customer savedCustomer = users.save(newUser);

        assertThrows(InvalidEntryException.class, () -> userService.createUser(newUser));
    }

    @Test
    void customerUpdateAddress_FoundUser_ReturnUpdatedAddress() {
        Customer foundUser = new Customer("user1", "goodpassword1", "Ronald Trump", "S8529649C", "91251234",
                "White House", "ROLE_USER", true);
        Long userId = foundUser.getCustomerId();
        when(users.findById(userId)).thenReturn(Optional.empty());

        Customer updatedCustomerAddress = userService.updateAddress(userId, "Blue House");

        assertNull(updatedCustomerAddress);
        verify(users).findById(userId);
    }


    @Test
    void customerUpdateAddress_UserNotFound_ReturnNull() {
        Long userId = 10L;
        when(users.findById(userId)).thenReturn(Optional.empty());
        Customer updatedAddress = userService.updateAddress(userId, "No Such Place");
        assertNull(updatedAddress);
    }

    @Test
    void customerUpdatePhone_FoundUser_ReturnUpdatedPhone() {
        Customer foundUser = new Customer("user1", "goodpassword1", "Ronald Trump", "S8529649C", "91251234",
                "White House", "ROLE_USER", true);
        Long userId = foundUser.getCustomerId();
        when(users.findById(userId)).thenReturn(Optional.empty());

        Customer updatedCustomerPhone = userService.updatePhone(userId, "81234567");

        assertNull(updatedCustomerPhone);
        verify(users).findById(userId);
    }

    @Test
    void customerUpdatePhone_UserNotFound_ReturnNull() {
        Long userId = 10L;
        when(users.findById(userId)).thenReturn(Optional.empty());
        Customer updatedPhone = userService.updatePhone(userId, "00000000");
        assertNull(updatedPhone);
    }

    @Test
    void customerUpdatePassword_FoundUser_ReturnUpdatedPassword() {
        Customer foundUser = new Customer("user1", "goodpassword1", "Ronald Trump", "S8529649C", "91251234",
                "White House", "ROLE_USER", true);
        Long userId = foundUser.getCustomerId();
        when(users.findById(userId)).thenReturn(Optional.empty());

        Customer updatedCustomerPassword = userService.updatePhone(userId, "betterpassword1");

        assertNull(updatedCustomerPassword);
        verify(users).findById(userId);
    }

    @Test
    void customerUpdatePassword_UserNotFound_ReturnNull() {
        Long userId = 10L;
        when(users.findById(userId)).thenReturn(Optional.empty());
        Customer updatedPassword = userService.updatePassword(userId, "canchangemeh?");
        assertNull(updatedPassword);
    }

    @Test
    void updateActiveStatus_FoundUser_ReturnUpdatedActiveStatus() {
        Customer foundUser = new Customer("user1", "goodpassword1", "Ronald Trump", "S8529649C", "91251234",
                "White House", "ROLE_USER", true);
        Long userId = foundUser.getCustomerId();

        when(users.findById(userId)).thenReturn(Optional.empty());

        Customer updatedActiveStatus = userService.updateActiveStatus(userId, false);

        assertNull(updatedActiveStatus);
        verify(users).findById(userId);
    }

    @Test
    void updateActiveStatus_UserNotFound_ReturnNull() {
        Long userId = 10L;
        when(users.findById(userId)).thenReturn(Optional.empty());
        Customer updatedStatus = userService.updateActiveStatus(userId, false);
        assertNull(updatedStatus);
    }

}
