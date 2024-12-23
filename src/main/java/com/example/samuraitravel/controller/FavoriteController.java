package com.example.samuraitravel.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.samuraitravel.entity.Favorite;
import com.example.samuraitravel.entity.House;
import com.example.samuraitravel.entity.User;
import com.example.samuraitravel.repository.FavoriteRepository;
import com.example.samuraitravel.security.UserDetailsImpl;
import com.example.samuraitravel.service.FavoriteService;
import com.example.samuraitravel.service.HouseService;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class FavoriteController {
	private final FavoriteRepository favoriteRepository;
	private final HouseService houseService;
	private final FavoriteService favoriteService;
	
	public FavoriteController(FavoriteRepository favoriteRepository, HouseService houseService, FavoriteService favoriteService) {
		this.favoriteRepository = favoriteRepository;
		this.houseService = houseService;
		this.favoriteService = favoriteService;
	}

@GetMapping("/favorites")
public String index(@AuthenticationPrincipal UserDetailsImpl userDetailsImpl, @PageableDefault(page = 0, size = 10, sort = "id", direction = Direction.ASC) Pageable pageable, Model model, RedirectAttributes redirectAttributes) {
	User user = userDetailsImpl.getUser();
	
	if (user.getRole().getName().equals("ROLE_FREE_MEMBER")) {
        redirectAttributes.addFlashAttribute("subscriptionMessage", "この機能を利用するには有料プランへの登録が必要です。");

        return "redirect:/subscription/register";
    }
	
    Page<Favorite> favoritePage = favoriteRepository.findByUserOrderByCreatedAtDesc(user, pageable);
    
    model.addAttribute("favoritePage", favoritePage);         
    
    return "favorites/index";
}
@PostMapping("/houses/{houseId}/favorites/create")
public String create(@PathVariable(name = "houseId") Integer houseId, @AuthenticationPrincipal UserDetailsImpl userDetailsImpl, RedirectAttributes redirectAttributes, Model model) {
    User user = userDetailsImpl.getUser();

    if (user.getRole().getName().equals("ROLE_FREE_MEMBER")) {
        redirectAttributes.addFlashAttribute("subscriptionMessage", "この機能を利用するには有料プランへの登録が必要です。");

        return "redirect:/subscription/register";
    }

    House house = houseService.findHouseById(houseId);

    if (house == null) {
        redirectAttributes.addFlashAttribute("errorMessage", "店舗が存在しません。");

        return "redirect:/houses";
    }

    favoriteService.createFavorite(house, user);
    redirectAttributes.addFlashAttribute("successMessage", "お気に入りに追加しました。");

    return "redirect:/houses/" + houseId;
}

@PostMapping("/favorites/{favoriteId}/delete")
public String delete(@PathVariable(name = "favoriteId") Integer favoriteId, @AuthenticationPrincipal UserDetailsImpl userDetailsImpl, RedirectAttributes redirectAttributes, HttpServletRequest httpServletRequest) {
    User user = userDetailsImpl.getUser();

    if (user.getRole().getName().equals("ROLE_FREE_MEMBER")) {
        redirectAttributes.addFlashAttribute("subscriptionMessage", "この機能を利用するには有料プランへの登録が必要です。");

        return "redirect:/subscription/register";
    }

    Favorite favorite = favoriteService.findFavoriteById(favoriteId);
    String referer = httpServletRequest.getHeader("Referer");

    if (favorite == null) {
        redirectAttributes.addFlashAttribute("errorMessage", "お気に入りが存在しません。");

        return "redirect:" + (referer != null ? referer : "/favorites");
    }

    if (!favorite.getUser().getId().equals(user.getId())) {
        redirectAttributes.addFlashAttribute("errorMessage", "不正なアクセスです。");

        return "redirect:" + (referer != null ? referer : "/favorites");
    }

    favoriteService.deleteFavorite(favorite);
    redirectAttributes.addFlashAttribute("successMessage", "お気に入りを解除しました。");

    return "redirect:/houses/" + favorite.getHouse().getId();
}

}
