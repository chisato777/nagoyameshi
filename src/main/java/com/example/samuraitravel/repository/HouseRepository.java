package com.example.samuraitravel.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.samuraitravel.entity.House;

public interface HouseRepository extends JpaRepository<House, Integer> {
	public Page<House> findByNameLikeOrCategory_NameLike(String nameKeyword, String categoryKeyword, Pageable pageable);

	public Page<House> findByNameLikeOrCategory_NameLikeOrderByCreatedAtDesc(String nameKeyword, String categoryKeyword, Pageable pageable);  
    public Page<House> findByNameLikeOrCategory_NameLikeOrderByPriceAsc(String nameKeyword, String categoryKeyword, Pageable pageable);  
    public Page<House> findByCategory_NameOrderByPriceAsc(String category, Pageable pageable);
    public Page<House> findByCategory_NameOrderByCreatedAtDesc(String category, Pageable pageable);
    public Page<House> findByPriceLessThanEqualOrderByCreatedAtDesc(Integer price, Pageable pageable);
    public Page<House> findByPriceLessThanEqualOrderByPriceAsc(Integer price, Pageable pageable); 
    public Page<House> findAllByOrderByCreatedAtDesc(Pageable pageable);
    public Page<House> findAllByOrderByPriceAsc(Pageable pageable);
    
    public List<House> findTop10ByOrderByCreatedAtDesc();
    public House findHouseById(Integer houseId);
	}

