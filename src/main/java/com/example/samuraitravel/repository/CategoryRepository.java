package com.example.samuraitravel.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.samuraitravel.entity.Category;

public interface CategoryRepository extends JpaRepository<Category, Integer> {
	public Page<Category> findByNameLike(String keyword, Pageable pageable);
	
}

