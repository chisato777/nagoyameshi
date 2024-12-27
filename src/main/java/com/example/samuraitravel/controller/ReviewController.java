package com.example.samuraitravel.controller;

import java.security.Principal;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.samuraitravel.entity.House;
import com.example.samuraitravel.entity.Review;
import com.example.samuraitravel.entity.User;
import com.example.samuraitravel.form.ReviewEditForm;
import com.example.samuraitravel.form.ReviewRegisterForm;
import com.example.samuraitravel.security.UserDetailsImpl;
import com.example.samuraitravel.service.HouseService;
import com.example.samuraitravel.service.ReviewService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/houses/{houseId}/reviews")
public class ReviewController {
	 
	private final ReviewService reviewService;
	private HouseService houseService;
	
	
	public ReviewController(ReviewService reviewService, HouseService houseService) {
		this.reviewService = reviewService;
		this.houseService = houseService;
	}

@GetMapping("/register")
public String register(@PathVariable(name = "houseId") Integer houseId, RedirectAttributes redirectAttributes, Principal principal, Model model) {
	House house = houseService.findHouseById(houseId);
	if (house == null) {
		model.addAttribute("errorMessage", "店舗が存在しません。");
		return "redirect:/houses/index";
	}
	
	model.addAttribute("house", house);
	model.addAttribute("reviewRegisterForm", new ReviewRegisterForm());
	
	return "reviews/register";
}

@PostMapping("/create")
public String create(@PathVariable(name = "houseId") Integer houseId, @ModelAttribute @Validated ReviewRegisterForm reviewRegisterForm, 
					 BindingResult bindingResult, @AuthenticationPrincipal UserDetailsImpl userDetailsImpl,RedirectAttributes redirectAttributes, Model model) {
	User user = userDetailsImpl.getUser();
	
	House house = houseService.findHouseById(houseId);
	
	if (house == null) {
		model.addAttribute("errorMessage", "店舗が存在しません。");
		return "redirect:/houses/index";
	}
	
	if (bindingResult.hasErrors()) {
		return "reviews/register";
	}
	
	reviewService.createReview(reviewRegisterForm, house, user);
	
	redirectAttributes.addFlashAttribute("successMessage", "レビューを投稿しました。");
	
	return "redirect:/houses/{houseId}";
}

@GetMapping("/{reviewId}/edit")
public String edit(@PathVariable(name = "houseId") Integer houseId, @PathVariable(name = "reviewId") Integer reviewId, Model model, @AuthenticationPrincipal UserDetailsImpl userDetailsImpl) {
	
	User user = userDetailsImpl.getUser();
    House house = houseService.findHouseById(houseId);
    Review review = reviewService.findReviewById(reviewId);

    if (house == null || review == null || !review.getHouse().getId().equals(houseId)) {
        model.addAttribute("errorMessage", "指定されたページが見つかりません。");
        return "redirect:/houses/index";
    }

    if (!review.getUser().getId().equals(user.getId())) {
        model.addAttribute("errorMessage", "不正なアクセスです。");
        return "redirect:/houses/" + houseId + "/reviews";
    }

    ReviewEditForm reviewEditForm = new ReviewEditForm(review.getRating(), review.getContent());

    model.addAttribute("house", house);
    model.addAttribute("review", review);
    model.addAttribute("reviewEditForm", reviewEditForm);

    return "reviews/edit";
}

@PostMapping("/{reviewId}/update")
public String update(@PathVariable(name = "houseId") Integer houseId, @PathVariable(name = "reviewId") Integer reviewId, @Valid ReviewEditForm reviewEditForm,BindingResult bindingResult,RedirectAttributes redirectAttributes, Model model, @AuthenticationPrincipal UserDetailsImpl userDetailsImpl) {
	User user = userDetailsImpl.getUser();
	House house = houseService.findHouseById(houseId);
	Review review = reviewService.findReviewById(reviewId);
	
	if (house == null || review == null || !review.getHouse().getId().equals(houseId)) {
        model.addAttribute("errorMessage", "指定されたページが見つかりません。");
        return "redirect:/houses/index";
    }
	
	if (!review.getHouse().getId().equals(houseId) || !review.getUser().getId().equals(user.getId())) {
		model.addAttribute("errorMessage", "不正なアクセスです。");
	}
	
	if (bindingResult.hasErrors()) {
        model.addAttribute("house", house);
        model.addAttribute("review", review);
        model.addAttribute("reviewEditForm", reviewEditForm);

        return "reviews/edit";
    }

    reviewService.updateReview(reviewEditForm, review);
    redirectAttributes.addFlashAttribute("successMessage", "レビューを編集しました。");
	
	return "redirect:/houses/" + houseId;
}

@GetMapping("/{reviewId}/delete")
public String delete(@PathVariable(name = "houseId") Integer houseId, @PathVariable(name = "reviewId") Integer reviewId, @AuthenticationPrincipal UserDetailsImpl userDetailsImpl, RedirectAttributes redirectAttributes) {
	User user = userDetailsImpl.getUser();
    House house = houseService.findHouseById(houseId);
    Review review = reviewService.findReviewById(reviewId);

    if (house == null || review == null || !review.getHouse().getId().equals(houseId) || !review.getUser().getId().equals(user.getId())) {
        redirectAttributes.addFlashAttribute("errorMessage", "不正なアクセスです。");
        return "redirect:/houses/" + houseId;
    }

    reviewService.deleteReview(review);
    redirectAttributes.addFlashAttribute("successMessage", "レビューを削除しました。");

    return "redirect:/houses/" + houseId;
}
}




