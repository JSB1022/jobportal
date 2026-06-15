package com.saibalaji.jobportal.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.saibalaji.jobportal.entity.Job;

public interface JobRepository extends JpaRepository<Job, Long> {

    /** All jobs posted by a specific recruiter */
    List<Job> findByPostedBy_Id(Long userId);
}