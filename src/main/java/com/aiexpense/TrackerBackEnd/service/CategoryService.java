package com.aiexpense.trackerbackend.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aiexpense.trackerbackend.model.Category;
import com.aiexpense.trackerbackend.repo.CategoryRepository;


@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    // Get all categories
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public Category getCategory(int id) {
        return categoryRepository.findById(id).orElse(null);
    }

    // Get category by ID
    public Optional<Category> getCategoryById(Integer id) {
        return categoryRepository.findById(id); // Uses findById() from JpaRepository
    }

    // Create new category
    public Category createCategory(Category category) {
        // Set default active status if not provided
        if (category.getEnabled() == null) {
            category.setEnabled(true);
        }
        return categoryRepository.save(category); // Uses save() from JpaRepository
    }

    // Update existing category
    public Category updateCategory(Integer id, Category categoryDetails) {
        return categoryRepository.findById(id)
                .map(existingCategory -> {
                    // Update only the fields that are provided
                    if (categoryDetails.getName() != null) {
                        existingCategory.setName(categoryDetails.getName());
                    }
                    if (categoryDetails.getDescription() != null) {
                        existingCategory.setDescription(categoryDetails.getDescription());
                    }
                    if (categoryDetails.getEnabled() != null) {
                        existingCategory.setEnabled(categoryDetails.getEnabled());
                    }
                    return categoryRepository.save(existingCategory); // Uses save() from JpaRepository
                })
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + id));
    }

    // Delete category permanently
    public void deleteCategory(Integer id) {
        if (!categoryRepository.existsById(id)) {
            throw new RuntimeException("Category not found with id: " + id);
        }
        categoryRepository.deleteById(id); // Uses deleteById() from JpaRepository
    }

    // Soft delete category (set isactive to false)
    public void softDeleteCategory(Integer id) {
        categoryRepository.findById(id)
                .ifPresent(category -> {
                    category.setEnabled(false);
                    categoryRepository.save(category); // Uses save() from JpaRepository
                });
    }

    // Get only active categories
    public List<Category> getActiveCategories() {
        return getAllCategories().stream()
                .filter(Category::getEnabled)
                .toList();
    }

    // Check if category exists
    public boolean categoryExists(Integer id) {
        return categoryRepository.existsById(id); // Uses existsById() from JpaRepository
    }

}
