package com.cs203t5.ryverbank.user;

import java.util.List;

// import com.cs203t5.ryverbank.token.*;

public interface UserService {
    List<User> listUsers();

    User getUser(Long userId);

    User addUser(User user);

    User updateUser(Long userId, User user);

    void deleteUser(Long userId);

    User register(User user);

    // void confirm(ConfirmationToken confirmationToken);

    // void sendEmail(String toUser, String token);
}
