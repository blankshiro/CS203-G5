package com.cs203t5.ryverbank.entity.User;

import java.util.List;


import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService{

    
    private UserRepository users;

    public UserServiceImpl(UserRepository users){
        this.users = users;
    }

    @Override
    public List<User> listUsers(){
        return users.findAll();
    }

    @Override
    public User getUser(String id){
        return users.findById(id).map(user -> {
            return user;
        }).orElse(null);
    }

    @Override
    public User addUser(User user){
        return users.save(user);
    }

    @Override
    public User updateUser(String id, User newUserInfo){
        return users.findById(id).map(user -> {user.setPassword(newUserInfo.getPassword());
            return users.save(user);
        }).orElse(null);
    }

    @Override
    public void deleteUser(String id){
        users.deleteById(id);
    }
}
