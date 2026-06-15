package com.saibalaji.jobportal.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import com.saibalaji.jobportal.entity.Job;
import com.saibalaji.jobportal.entity.User;
import com.saibalaji.jobportal.repository.JobRepository;
import com.saibalaji.jobportal.repository.UserRepository;

import jakarta.servlet.http.HttpSession;

@Controller
public class JobController {

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private UserRepository userRepository;

    /** Save job posted from the Post-a-Job form and attach recruiter */
    @PostMapping("/save-job")
    public String saveJob(@ModelAttribute Job job, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId != null) {
            User recruiter = userRepository.findById(userId).orElse(null);
            job.setPostedBy(recruiter);
        }
        jobRepository.save(job);
        return "redirect:/recruiter-dashboard";
    }

    /** JSON endpoint */
    @GetMapping("/jobs")
    @ResponseBody
    public List<Job> getAllJobs() {
        return jobRepository.findAll();
    }

    /** Delete a job posting (recruiter only) */
    @PostMapping("/delete-job/{id}")
    public String deleteJob(@PathVariable Long id, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        Job job = jobRepository.findById(id).orElse(null);
        // Only the owner can delete
        if (job != null && job.getPostedBy() != null
                && job.getPostedBy().getId().equals(userId)) {
            jobRepository.deleteById(id);
        }
        return "redirect:/recruiter-dashboard";
    }
}