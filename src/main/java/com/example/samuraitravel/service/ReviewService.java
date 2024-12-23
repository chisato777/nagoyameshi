package com.example.samuraitravel.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.samuraitravel.entity.House;
import com.example.samuraitravel.entity.Review;
import com.example.samuraitravel.entity.User;
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
    
    public Review updateReview(Integer reviewId, String newContent, int newRating) {
    	
    	Review review = reviewRepository.findReviewById(reviewId);
    		review.setContent(newContent);
        	review.setRating(newRating);
        	return reviewRepository.save(review); 
    }
 
    public void deleteReview(Integer id) {
    	reviewRepository.deleteById(id);
    }
    
    public boolean hasUserAlreadyReviewed(House house, User user) {
    	Review review = reviewRepository.findByHouseAndUser(house, user); {
    		if(review == null) {
    			return false;
    		} else {
    			return true;
    		}
    	}
    }
    
    //指定した民宿に関連するレビューが存在するか
    public boolean existsReviewsForHouse(House house) {
    	return reviewRepository.existsByHouse(house);
    }
   

}
