package ru.kata.spring.boot_security.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.repositories.UserRepository;

import java.util.List;


@Service
public class UserServiceImp implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

   @Autowired
    public UserServiceImp( UserRepository userRepository,PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> getUsersList() {
        return userRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public User getUser(int id) {
        return userRepository.findById(id).orElse(null);
    }

    @Override
    @Transactional
    public void addUser(User user) throws NumberFormatException {
        User userFromDB = userRepository.findByEmail(user.getUsername()).orElse(null);
        if (userFromDB == null) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            userRepository.save(user);
        }
    }

    @Override
    @Transactional
    public void editUser( User userUpdate) {
        userUpdate.setPassword(passwordEncoder.encode(userUpdate.getPassword()));
        userRepository.save(userUpdate);
   }

    @Override
    @Transactional
    public void deleteUser(int id) {
        userRepository.deleteById(id);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

}