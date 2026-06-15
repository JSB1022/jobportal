package com.saibalaji.jobportal.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.saibalaji.jobportal.entity.Application;

public interface ApplicationRepository extends JpaRepository<Application, Long> {

    /** Applications submitted by a specific job seeker */
    List<Application> findByUser_Id(Long userId);

    /** Applications for a specific job (recruiter view) */
    List<Application> findByJob_Id(Long jobId);

    /** Applications for all jobs posted by a specific recruiter */
    List<Application> findByJob_PostedBy_Id(Long recruiterId);
}