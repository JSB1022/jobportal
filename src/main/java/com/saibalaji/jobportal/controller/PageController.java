package com.saibalaji.jobportal.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.saibalaji.jobportal.entity.Application;
import com.saibalaji.jobportal.entity.Job;
import com.saibalaji.jobportal.repository.ApplicationRepository;
import com.saibalaji.jobportal.repository.JobRepository;
import com.saibalaji.jobportal.repository.UserRepository;

import jakarta.servlet.http.HttpSession;

@Controller
public class PageController {

    @Autowired private JobRepository         jobRepository;
    @Autowired private ApplicationRepository applicationRepository;
    @Autowired private UserRepository        userRepository;

    /* ── HOME ─────────────────────────────── */
    @GetMapping("/")
    public String home() {
        return "index";
    }

    /* ── AUTH PAGES ────────────────────────── */
    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    /* ── JOB SEEKER: browse jobs ─────────── */
    @GetMapping("/jobs-page")
    public String jobsPage(Model model) {
        model.addAttribute("jobs", jobRepository.findAll());
        return "jobs";
    }

    /* ── JOB SEEKER: own applications ───── */
    @GetMapping("/my-applications")
    public String myApplications(Model model, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return "redirect:/login";
        model.addAttribute("applications",
                applicationRepository.findByUser_Id(userId));
        return "my-applications";
    }

    /* ── RECRUITER: dashboard ─────────────── */
    @GetMapping("/recruiter-dashboard")
    public String recruiterDashboard(Model model, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return "redirect:/login";

        // Only recruiters
        String role = (String) session.getAttribute("userRole");
        if (!"RECRUITER".equals(role)) return "redirect:/jobs-page";

        // Jobs posted by this recruiter
        List<Job> jobs = jobRepository.findByPostedBy_Id(userId);

        // All applications for those jobs, keyed by jobId
        List<Application> allApps = applicationRepository.findByJob_PostedBy_Id(userId);
        Map<Long, List<Application>> applicationsByJob = new HashMap<>();
        for (Job job : jobs) {
            applicationsByJob.put(job.getId(),
                    applicationRepository.findByJob_Id(job.getId()));
        }

        // Aggregate stats
        long totalApplications = allApps.size();
        long totalAccepted     = allApps.stream().filter(a -> "ACCEPTED".equals(a.getStatus())).count();
        long totalPending      = allApps.stream().filter(a -> "PENDING".equals(a.getStatus())).count();

        model.addAttribute("jobs",               jobs);
        model.addAttribute("applicationsByJob",  applicationsByJob);
        model.addAttribute("totalApplications",  totalApplications);
        model.addAttribute("totalAccepted",      totalAccepted);
        model.addAttribute("totalPending",       totalPending);

        return "recruiter-dashboard";
    }

    /* ── ADMIN: all applications ─────────── */
    @GetMapping("/applications-page")
    public String applicationsPage(Model model, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return "redirect:/login";
        model.addAttribute("applications", applicationRepository.findAll());
        return "applications";
    }

    /* ── POST JOB page ───────────────────── */
    @GetMapping("/post-job")
    public String postJobPage(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return "redirect:/login";
        return "post-job";
    }

    /* ── PROFILE ─────────────────────────── */
    @GetMapping("/profile")
    public String profilePage(Model model, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return "redirect:/login";
        model.addAttribute("user",
                userRepository.findById(userId).orElse(null));
        return "profile";
    }
}