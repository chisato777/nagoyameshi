package com.example.samuraitravel.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.samuraitravel.entity.Category;
import com.example.samuraitravel.form.CategoryEditForm;
import com.example.samuraitravel.form.CategoryRegisterForm;
import com.example.samuraitravel.repository.CategoryRepository;

@Service
public class CategoryService {
	private final CategoryRepository categoryRepository;

	public CategoryService(CategoryRepository categoryRepository) {
		this.categoryRepository = categoryRepository;
	}

	@Transactional
	public void create(CategoryRegisterForm categoryRegisterForm) {
		Category category = new Category();

		category.setName(categoryRegisterForm.getName());

		categoryRepository.save(category);
	}
	
	@Transactional
    public void update(CategoryEditForm categoryEditForm) {
		Category category = categoryRepository.getReferenceById(categoryEditForm.getId());
        
        category.setName(categoryEditForm.getName());                
                    
        categoryRepository.save(category);
    }    
}
