package com.example.samuraitravel.service;

import java.time.LocalDate;
import java.time.LocalTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.samuraitravel.entity.House;
import com.example.samuraitravel.entity.Reservation;
import com.example.samuraitravel.entity.User;
import com.example.samuraitravel.form.ReservationRegisterForm;
import com.example.samuraitravel.repository.HouseRepository;
import com.example.samuraitravel.repository.ReservationRepository;
import com.example.samuraitravel.repository.UserRepository;


@Service
public class ReservationService {
	private final ReservationRepository reservationRepository;  
    private final HouseRepository houseRepository;  
    private final UserRepository userRepository;  
    
    public ReservationService(ReservationRepository reservationRepository, HouseRepository houseRepository, UserRepository userRepository) {
        this.reservationRepository = reservationRepository;  
        this.houseRepository = houseRepository;  
        this.userRepository = userRepository;  
    }    
    @Transactional
    public void create(ReservationRegisterForm reservationRegisterForm) { 
        Reservation reservation = new Reservation();
        House house = houseRepository.getReferenceById(reservationRegisterForm.getHouseId());
        User user = userRepository.getReferenceById(reservationRegisterForm.getUserId());
        LocalDate visitDate = LocalDate.parse(reservationRegisterForm.getVisitDate());
        LocalTime visitTime = LocalTime.parse(reservationRegisterForm.getVisitTime());         
                
        reservation.setHouse(house);
        reservation.setUser(user);
        reservation.setVisitDate(visitDate);
        reservation.setVisitTime(visitTime);
        reservation.setNumberOfPeople(reservationRegisterForm.getNumberOfPeople());
        
        reservationRepository.save(reservation);
    }    
    
//    @Transactional
//    public void create(Map<String, String> paymentIntentObject) { 
//        Reservation reservation = new Reservation();
//        
//        Integer houseId = Integer.valueOf(paymentIntentObject.get("houseId"));
//        Integer userId = Integer.valueOf(paymentIntentObject.get("userId"));
//        
//        House house = houseRepository.getReferenceById(houseId);
//        User user = userRepository.getReferenceById(userId);
//        LocalDate checkinDate = LocalDate.parse(paymentIntentObject.get("checkinDate"));
//        LocalDate checkoutDate = LocalDate.parse(paymentIntentObject.get("checkoutDate"));
//        Integer numberOfPeople = Integer.valueOf(paymentIntentObject.get("numberOfPeople"));        
//        Integer amount = Integer.valueOf(paymentIntentObject.get("amount"));         
//                
//        reservation.setHouse(house);
//        reservation.setUser(user);
//        reservation.setCheckinDate(checkinDate);
//        reservation.setCheckoutDate(checkoutDate);
//        reservation.setNumberOfPeople(numberOfPeople);
//        reservation.setAmount(amount);
//        
//        reservationRepository.save(reservation);
//    } 
	// 宿泊人数が定員以下かどうかをチェックする
    public boolean isWithinCapacity(Integer numberOfPeople, Integer capacity) {
        return numberOfPeople <= capacity;
    }
    
    @Transactional
    public void delete(Reservation reservation) {
        reservationRepository.delete(reservation);
    }
    
//    // 宿泊料金を計算する
//    public Integer calculateAmount(LocalDate checkinDate, LocalDate checkoutDate, Integer price) {
//        long numberOfNights = ChronoUnit.DAYS.between(checkinDate, checkoutDate);
//        int amount = price * (int)numberOfNights;
//        return amount;
//    } 
}
