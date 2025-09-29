package com.aiexpense.trackerbackend.repo;



import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;

import com.aiexpense.trackerbackend.entities.Category;


@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {

}
