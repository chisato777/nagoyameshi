package com.example.samuraitravel.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.samuraitravel.entity.House;
import com.example.samuraitravel.entity.Review;
import com.example.samuraitravel.entity.User;
import com.example.samuraitravel.form.ReviewEditForm;
import com.example.samuraitravel.form.ReviewRegisterForm;
import com.example.samuraitravel.repository.ReviewRepository;

@Service
public class ReviewService {
	@Autowired
	private final ReviewRepository reviewRepository;
    
    public ReviewService(ReviewRepository reviewRepository, HouseService houseService) {
        this.reviewRepository = reviewRepository; 
    } 
    
    public Review findReviewById(Integer id) {
    	return reviewRepository.findReviewById(id);
    }
    
    public List<Review> findAllReviewsByHouse(House house) {
    	return reviewRepository.findByHouseOrderByCreatedAtDesc(house);
    }
    
    public long countReviews() {
    	return reviewRepository.count();
    }
    
    public Review findFirstReviewByOrderByIdDesc() {
    	return reviewRepository.findFirstReviewByOrderByIdDesc();
    }
    
    @Transactional
    public void createReview(ReviewRegisterForm reviewRegisterForm, House house, User user) {
    	Review review = new Review();
    	
    	review.setContent(reviewRegisterForm.getContent());
    	review.setRating(reviewRegisterForm.getRating());
    	review.setHouse(house);
    	review.setUser(user);
    	
    	reviewRepository.save(review);
    }
    
    @Transactional
    public void updateReview(ReviewEditForm reviewEditForm, Review review) {
        review.setRating(reviewEditForm.getRating());
        review.setContent(reviewEditForm.getContent());

        reviewRepository.save(review);
    }
 
    public void deleteReview(Review review) {
    	reviewRepository.delete(review);
    }
    
    public boolean hasUserAlreadyReviewed(House house, User user) {
    	return reviewRepository.existsByHouseAndUser(house, user);
    }
   
}
