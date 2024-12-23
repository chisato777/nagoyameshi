package com.example.samuraitravel.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.samuraitravel.entity.Favorite;
import com.example.samuraitravel.entity.House;
import com.example.samuraitravel.entity.User;
import com.example.samuraitravel.repository.FavoriteRepository;

import jakarta.transaction.Transactional;

@Service
public class FavoriteService {
	private FavoriteRepository favoriteRepository;

	public FavoriteService(FavoriteRepository favoriteRepository) {
		this.favoriteRepository = favoriteRepository;
	}

	public Favorite findFavoriteById(Integer id) {
		return favoriteRepository.findFavoriteById(id);
	}

	public Favorite findFavoriteByHouseAndUser(House house, User user) {
		return favoriteRepository.findByHouseAndUser(house, user);
	}

	public Page<Favorite> findFavoritesByUserOrderByCreatedAtDesc(User user, Pageable pageable) {
		return favoriteRepository.findByUserOrderByCreatedAtDesc(user, pageable);
	}

	public long countFavorites() {
		return favoriteRepository.count();
	}

	@Transactional
	public void createFavorite(House house, User user) {
		Favorite favorite = new Favorite();

		favorite.setHouse(house);
		favorite.setUser(user);

		favoriteRepository.save(favorite);
	}

	@Transactional
	public void deleteFavorite(Favorite favorite) {
		favoriteRepository.delete(favorite);
	}

	public boolean isFavorite(House house, User user) {
		Favorite favorite = favoriteRepository.findByHouseAndUser(house, user);
		if (favorite != null) {
			return true;
		} else {
			return false;
		}
	}

}
