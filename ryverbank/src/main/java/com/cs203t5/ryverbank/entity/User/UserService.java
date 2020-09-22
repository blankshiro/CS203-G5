package com.cs203t5.ryverbank.entity.User;

import java.util.List;

import com.cs203t5.ryverbank.entity.ConfirmationToken.ConfirmationToken;

public interface UserService {
    List<User> listUsers();

    User getUser(String id);

    User addUser(User user);

    User updateUser(String id, User user);

    void deleteUser(String id);

    void register(User user);

    void confirm(ConfirmationToken confirmationToken);

    void sendEmail(String toUser, String token);
}
