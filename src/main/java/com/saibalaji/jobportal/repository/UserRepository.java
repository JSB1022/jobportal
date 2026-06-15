package com.saibalaji.jobportal.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.saibalaji.jobportal.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {

    User findByEmailAndPassword(String email, String password);

}