package ru.kata.spring.boot_security.demo.service;

import ru.kata.spring.boot_security.demo.model.User;

import java.util.List;


public interface UserService {

    List<User> getUsersList();
    User getUser(int id);
    void addUser(User user);
    void editUser(int id, User user);
    void deleteUser(int id);
    boolean existByUserName(String userName);

}