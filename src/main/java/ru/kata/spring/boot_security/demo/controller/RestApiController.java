package ru.kata.spring.boot_security.demo.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.service.RoleService;
import ru.kata.spring.boot_security.demo.service.UserServiceImp;

import java.util.List;


@RestController
@RequestMapping("/api")
public class RestApiController {

    private final UserServiceImp userServiceImp;
    private final RoleService roleService;

    public RestApiController(UserServiceImp userServiceImp, RoleService roleService) {
        this.userServiceImp = userServiceImp;
        this.roleService = roleService;
    }

    @GetMapping("/admin")
    public ResponseEntity<List<User>> getUsers() {
        List<User> usersList = userServiceImp.getUsersList();
        return !usersList.isEmpty()
                ? ResponseEntity.status(HttpStatus.OK).body(usersList)
                : ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @GetMapping("/admin/{id}")
    public ResponseEntity<User> getUser(@PathVariable("id") int id) {
        User user = userServiceImp.getUser(id);
        return user != null
                ? ResponseEntity.status(HttpStatus.OK).body(user)
                : ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @PostMapping("/admin/create")
    public ResponseEntity<User> createUser(@RequestBody User user) {
        userServiceImp.addUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    @PutMapping("/admin/update")
    public ResponseEntity<User> updateUser(@RequestBody User user) {
        userServiceImp.editUser(user);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(user);
    }

    @DeleteMapping("/admin/delete/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable("id") int id) {
        userServiceImp.deleteUser(id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping("/admin/roles")
    public ResponseEntity<List<Role>> getRoles() {
        List<Role> roles = roleService.getRoles();
        return !roles.isEmpty()
                ? ResponseEntity.status(HttpStatus.OK).body(roles)
                : ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @GetMapping("/user")
    public ResponseEntity<User> showUser(@AuthenticationPrincipal User user) {
        return ResponseEntity.status(HttpStatus.OK).body(user);
    }
}
