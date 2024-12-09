package ru.kata.spring.boot_security.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.service.RoleService;
import ru.kata.spring.boot_security.demo.service.UserServiceImpl;
import javax.validation.Valid;


@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserServiceImpl userServiceImp;
    private final RoleService roleService;

    @Autowired
    public AdminController(UserServiceImpl userServiceImp, RoleService roleService) {
        this.userServiceImp = userServiceImp;
        this.roleService = roleService;
    }

    @GetMapping
    public String displayAllUser(@AuthenticationPrincipal User user, Model model) {
        model.addAttribute("user", user);
        model.addAttribute("users", userServiceImp.getUsersList());
        model.addAttribute("roles", roleService.getRoles());
        return "admin/admin";
    }

    @GetMapping("/show")
    public String showUser(@AuthenticationPrincipal User user, Model model) {
        model.addAttribute("user", user);
        return "admin/adminIndex";
    }


    @GetMapping("/new")
    public String newUser(@ModelAttribute("adduser") User adduser, Model model, @AuthenticationPrincipal User user) {
        model.addAttribute("roles", roleService.getRoles());
        model.addAttribute("user", user);
        return "admin/new";
    }


    @PostMapping("/new")
    public String create(@ModelAttribute("user") @Valid User adduser, Model model) {
        model.addAttribute("roles", roleService.getRoles());
        model.addAttribute("roleSet", adduser.getRoleSet());
        userServiceImp.addUser(adduser);
        return "redirect:/admin";
    }

    @PostMapping("/edit")
    public String update(@RequestParam("id") int id, @ModelAttribute("user") @Valid User user, Model model) {
        model.addAttribute("roles", roleService.getRoles());
        model.addAttribute("roleSet", user.getRoleSet());
        userServiceImp.editUser(id, user);
        return "redirect:/admin";
    }

    @PostMapping("/delete")
    public String delete(@RequestParam("id") int id) {
        userServiceImp.deleteUser(id);
        return "redirect:/admin";
    }
}