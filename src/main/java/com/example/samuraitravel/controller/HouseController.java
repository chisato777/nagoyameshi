package com.example.samuraitravel.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.samuraitravel.entity.Favorite;
import com.example.samuraitravel.entity.House;
import com.example.samuraitravel.entity.Review;
import com.example.samuraitravel.entity.User;
import com.example.samuraitravel.form.ReservationInputForm;
import com.example.samuraitravel.repository.HouseRepository;
import com.example.samuraitravel.service.FavoriteService;
import com.example.samuraitravel.service.ReviewService;
import com.example.samuraitravel.service.UserService;

@Controller
@RequestMapping("/houses")
public class HouseController {
	private final HouseRepository houseRepository;
	private final ReviewService reviewService;
	private final UserService userService;
	private final FavoriteService favoriteService;

	public HouseController(HouseRepository houseRepository, ReviewService reviewService, UserService userService,
			FavoriteService favoriteService) {
		this.houseRepository = houseRepository;
		this.reviewService = reviewService;
		this.userService = userService;
		this.favoriteService = favoriteService;
	}

	@GetMapping
	public String index(@RequestParam(name = "keyword", required = false) String keyword,
			@RequestParam(name = "category", required = false) String category,
			@RequestParam(name = "price", required = false) Integer price,
			@RequestParam(name = "order", required = false) String order,
			@PageableDefault(page = 0, size = 10, sort = "id", direction = Direction.ASC) Pageable pageable,
			Model model) {
		Page<House> housePage;

		if (keyword != null && !keyword.isEmpty()) {
			if (order != null && order.equals("priceAsc")) {
				housePage = houseRepository.findByNameLikeOrCategory_NameLikeOrderByPriceAsc("%" + keyword + "%",
						"%" + keyword + "%", pageable);
			} else {
				housePage = houseRepository.findByNameLikeOrCategory_NameLikeOrderByCreatedAtDesc("%" + keyword + "%",
						"%" + keyword + "%", pageable);
			}
		} else if (category != null && !category.isEmpty()) {
			if (order != null && order.equals("priceAsc")) {
				housePage = houseRepository.findByCategory_NameOrderByPriceAsc(category, pageable);
			} else {
				housePage = houseRepository.findByCategory_NameOrderByCreatedAtDesc(category, pageable);
			}
		} else if (price != null) {
			if (order != null && order.equals("priceAsc")) {
				housePage = houseRepository.findByPriceLessThanEqualOrderByPriceAsc(price, pageable);
			} else {
				housePage = houseRepository.findByPriceLessThanEqualOrderByCreatedAtDesc(price, pageable);
			}
		} else {
			if (order != null && order.equals("priceAsc")) {
				housePage = houseRepository.findAllByOrderByPriceAsc(pageable);
			} else {
				housePage = houseRepository.findAllByOrderByCreatedAtDesc(pageable);
			}
		}

		model.addAttribute("housePage", housePage);
		model.addAttribute("keyword", keyword);
		model.addAttribute("category", category);
		model.addAttribute("price", price);
		model.addAttribute("order", order);

		return "houses/index";
	}

	@GetMapping("/{id}")
	public String show(@PathVariable(name = "id") Integer id, Model model, Principal principal) {
		House house = houseRepository.getReferenceById(id);

		if (house == null) {
			model.addAttribute("errorMessage", "民宿が存在しません。");
			return "redirect:/houses/index";
		}

		List<Review> reviews = reviewService.findAllReviewsByHouse(house);

		String userRole = null;
		boolean hasUserAlreadyReviewed = false;
		boolean isFavorite = false;
		Favorite favorite = null;

		if (principal != null) {
			
			User currentUser = userService.findByEmail(principal.getName());

			if (currentUser != null) {
				userRole = currentUser.getRole().getName(); 
				hasUserAlreadyReviewed = reviewService.hasUserAlreadyReviewed(house, currentUser); 
				favorite = favoriteService.findFavoriteByHouseAndUser(house, currentUser); 
				isFavorite = (favorite != null); 
			}
		}
		model.addAttribute("userRole", userRole); 
		model.addAttribute("hasUserAlreadyReviewed", hasUserAlreadyReviewed); 
		model.addAttribute("isFavorite", isFavorite);
		model.addAttribute("favorite", favorite);

		model.addAttribute("house", house);
		model.addAttribute("reviews", reviews);
		model.addAttribute("reservationInputForm", new ReservationInputForm());

		return "houses/show";
	}
}
