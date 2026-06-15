package com.saibalaji.jobportal.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.saibalaji.jobportal.entity.User;
import com.saibalaji.jobportal.repository.UserRepository;

import jakarta.servlet.http.HttpSession;

@Controller
public class UserController {

    @Autowired
    private UserRepository userRepository;

    /* ── REGISTER (HTML form) ─────────────── */
    @PostMapping("/register-user")
    public String registerUserForm(@ModelAttribute User user) {
        userRepository.save(user);
        return "redirect:/login";
    }

    /* ── REGISTER (JSON / Postman) ────────── */
    @PostMapping("/users/register")
    @ResponseBody
    public User registerUser(@RequestBody User user) {
        return userRepository.save(user);
    }

    /* ── LOGIN ────────────────────────────── */
    @PostMapping("/login-user")
    public String loginUser(@RequestParam String email,
                            @RequestParam String password,
                            HttpSession session) {

        User user = userRepository.findByEmailAndPassword(email, password);

        if (user == null) {
            return "redirect:/login?error=true";
        }

        // Store session attributes
        session.setAttribute("userId",   user.getId());
        session.setAttribute("userName", user.getName());
        session.setAttribute("userRole", user.getRole());

        // Route by role
        if ("RECRUITER".equals(user.getRole())) {
            return "redirect:/recruiter-dashboard";
        }
        return "redirect:/jobs-page";
    }

    /* ── LOGOUT ───────────────────────────── */
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }

    /* ── PROFILE UPDATE ───────────────────── */
    @PostMapping("/update-profile")
    public String updateProfile(@RequestParam String name,
                                @RequestParam String email,
                                @RequestParam(required = false) String password,
                                @RequestParam String role,
                                HttpSession session) {

        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return "redirect:/login";

        User user = userRepository.findById(userId).orElse(null);
        if (user == null) return "redirect:/login";

        user.setName(name);
        user.setEmail(email);
        user.setRole(role);

        // Only update password if a new one was provided
        if (password != null && !password.isBlank()) {
            user.setPassword(password);
        }

        userRepository.save(user);

        // Refresh session attributes
        session.setAttribute("userName", user.getName());
        session.setAttribute("userRole", user.getRole());

        return "redirect:/profile?updated=true";
    }

    /* ── JSON endpoints ───────────────────── */
    @GetMapping("/users")
    @ResponseBody
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @GetMapping("/users/{id}")
    @ResponseBody
    public User getUserById(@PathVariable Long id) {
        return userRepository.findById(id).orElse(null);
    }

    @DeleteMapping("/users/{id}")
    @ResponseBody
    public String deleteUser(@PathVariable Long id) {
        userRepository.deleteById(id);
        return "User Deleted Successfully";
    }
}