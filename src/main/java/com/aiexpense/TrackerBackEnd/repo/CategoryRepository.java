package com.aiexpense.trackerbackend.repo;



import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;

import com.aiexpense.trackerbackend.model.Category;


@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {

    // Find all active categories
    List<Category> findByIsActiveTrue();
    
    // Find category by name (case insensitive)
    Optional<Category> findByNameIgnoreCase(String name);
    
    // Find active category by ID
    Optional<Category> findByIdAndIsActiveTrue(Integer id);
    
    // Check if category exists by name (for validation)
    boolean existsByNameIgnoreCaseAndIsActiveTrue(String name);
    
    // Find categories containing search term
    List<Category> findByNameContainingIgnoreCaseAndIsActiveTrue(String name);

}
