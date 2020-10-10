package com.cs203t5.ryverbank.customer;

import org.springframework.security.core.Authentication;

import static org.junit.jupiter.api.Assertions.assertNotNull;
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
    void createUser_NewUser_ReturnNewUser() {
        // Arrange
        Customer newUser = new Customer("user1", "goodpassword1", "Ronald Trump", "S8529649C", "91251234",
                "White House", "ROLE_USER", true);
        // Mock the save function
        when(users.save(any(Customer.class))).thenReturn(newUser);
        // Act
        Customer savedCustomer = userService.createUser(newUser);

        // Assert
        assertNotNull(savedCustomer);
        verify(users).save(newUser);
    }

    @Test // This is not working!
    void createUser_SameUser_ThrowCustomerExistsException() {
        // Arrange
        Customer sameUser = new Customer("user1", "goodpassword1", "Ronald Trump", "S8529649C", "91251234",
                "White House", "ROLE_USER", true);
        users.save(sameUser);
        // Mock
        when(users.existsByUsername(any(String.class))).thenReturn(true);
        // Act
        assertThrows(CustomerExistsException.class, () -> userService.createUser(sameUser));
    }

    @Test
    void getUser_FoundUser_ReturnUser() {
        // Arrange
        Customer foundUser = new Customer("user1", "goodpassword1", "Ronald Trump", "S8529649C", "91251234",
                "White House", "ROLE_USER", true);
        // Mock
        Long userId = foundUser.getId();
        when(users.findById(userId)).thenReturn(Optional.empty());

        Customer gCustomer = userService.getUser(userId, foundUser.getUsername(), "USER_ROLE");
        assertNull(gCustomer);
        verify(users).findById(userId);

    }

    @Test
    void getCustomer_NotFound_ThrowsCustomerNotFoundException() {
        
    }

    @Test
    void customerUpdateAddress_FoundUser_ReturnUpdatedAddress() {
        Customer foundUser = new Customer("user1", "goodpassword1", "Ronald Trump", "S8529649C", "91251234",
                "White House", "ROLE_USER", true);
        Long userId = foundUser.getId();
        when(users.findById(userId)).thenReturn(Optional.empty());

        Customer updatedCustomerAddress = userService.updateAddress(userId, "Blue House", "USER_ROLE");

        assertNull(updatedCustomerAddress);
        verify(users).findById(userId);
    }

    @Test
    void customerUpdatePhone_FoundUser_ReturnUpdatedPhone() {
        Customer foundUser = new Customer("user1", "goodpassword1", "Ronald Trump", "S8529649C", "91251234",
                "White House", "ROLE_USER", true);
        Long userId = foundUser.getId();
        when(users.findById(userId)).thenReturn(Optional.empty());

        Customer updatedCustomerPhone = userService.updatePhone(userId, "81234567", "ROLE_USER");

        assertNull(updatedCustomerPhone);
        verify(users).findById(userId);
    }

    @Test
    void customerUpdatePassword_FoundUser_ReturnUpdatedPassword() {
        Customer foundUser = new Customer("user1", "goodpassword1", "Ronald Trump", "S8529649C", "91251234",
                "White House", "ROLE_USER", true);
        Long userId = foundUser.getId();
        when(users.findById(userId)).thenReturn(Optional.empty());

        Customer updatedCustomerPassword = userService.updatePhone(userId, "betterpassword1", "USER_ROLE");

        assertNull(updatedCustomerPassword);
        verify(users).findById(userId);
    }

    @Test
    void updateActiveStatus_FoundUser_ReturnUpdatedActiveStatus() {
        Customer foundUser = new Customer("user1", "goodpassword1", "Ronald Trump", "S8529649C", "91251234",
                "White House", "ROLE_USER", true);
        Long userId = foundUser.getId();

        when(users.findById(userId)).thenReturn(Optional.empty());

        Customer updatedActiveStatus = userService.updateActiveStatus(userId, false);

        assertNull(updatedActiveStatus);
        verify(users).findById(userId);
    }

}
