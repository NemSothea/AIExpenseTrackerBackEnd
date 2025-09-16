package com.aiexpense.trackerbackend.repo;


import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.aiexpense.trackerbackend.model.Users;

@Repository
public interface UserRepository extends JpaRepository<Users,Integer> {

    Optional<Users> findByEmail(String email);
    Users findByUsername(String username);

}
