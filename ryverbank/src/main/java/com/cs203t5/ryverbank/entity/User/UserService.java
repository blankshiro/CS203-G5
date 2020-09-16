package com.cs203t5.ryverbank.entity.User;

import java.util.List;

public interface UserService {
    List<User> listUsers();
    User getUser(String id);
    User addUser(User user);
    User updateUser(String id, User user);

    void deleteUser(String id);
}
