package com.aiexpense.trackerbackend.repo;


import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.aiexpense.trackerbackend.model.Users;

@Repository
public interface UserRepository extends JpaRepository<Users,Integer> {

    @Query("select u from Users u where u.email = :email")
    public Users getUserByEmail(@Param("email") String email);
    boolean existsByEmail(String email);  // Matches email with the emails in user table

    Optional<Users> findByEmail(String email);

    @Query("SELECT u.role FROM Users u WHERE u.email = :email")
    String findRoleByUsername(String email);

}
