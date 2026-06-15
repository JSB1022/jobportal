package com.saibalaji.jobportal.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import com.saibalaji.jobportal.entity.Application;
import com.saibalaji.jobportal.entity.Job;
import com.saibalaji.jobportal.entity.User;
import com.saibalaji.jobportal.repository.ApplicationRepository;
import com.saibalaji.jobportal.repository.JobRepository;
import com.saibalaji.jobportal.repository.UserRepository;

import jakarta.servlet.http.HttpSession;

@Controller
public class ApplicationController {

    @Autowired private ApplicationRepository applicationRepository;
    @Autowired private UserRepository         userRepository;
    @Autowired private JobRepository          jobRepository;

    /** Job seeker applies for a job */
    @PostMapping("/apply-job")
    public String applyJob(@RequestParam Long jobId, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return "redirect:/login";

        User user = userRepository.findById(userId).orElse(null);
        Job  job  = jobRepository.findById(jobId).orElse(null);
        if (user == null || job == null) return "redirect:/jobs-page";

        // Prevent duplicate applications
        List<Application> existing = applicationRepository.findByUser_Id(userId);
        boolean alreadyApplied = existing.stream()
                .anyMatch(a -> a.getJob() != null && a.getJob().getId().equals(jobId));
        if (alreadyApplied) return "redirect:/jobs-page?duplicate=true";

        Application application = new Application();
        application.setUser(user);
        application.setJob(job);
        application.setStatus("PENDING");
        applicationRepository.save(application);

        return "redirect:/jobs-page?applied=true";
    }

    /** Recruiter accepts an application */
    @PostMapping("/application/{id}/accept")
    public String acceptApplication(@PathVariable Long id, HttpSession session) {
        Application app = applicationRepository.findById(id).orElse(null);
        if (app != null) {
            app.setStatus("ACCEPTED");
            applicationRepository.save(app);
        }
        return "redirect:/recruiter-dashboard";
    }

    /** Recruiter rejects an application */
    @PostMapping("/application/{id}/reject")
    public String rejectApplication(@PathVariable Long id, HttpSession session) {
        Application app = applicationRepository.findById(id).orElse(null);
        if (app != null) {
            app.setStatus("REJECTED");
            applicationRepository.save(app);
        }
        return "redirect:/recruiter-dashboard";
    }

    /** JSON endpoint */
    @GetMapping("/applications")
    @ResponseBody
    public List<Application> getAllApplications() {
        return applicationRepository.findAll();
    }
}