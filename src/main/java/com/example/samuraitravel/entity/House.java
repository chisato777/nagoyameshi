package com.example.samuraitravel.entity;

import java.sql.Timestamp;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Data;

@Entity
@Table(name = "houses")
@Data
public class House {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
	private Integer id;
	
	@Column(name = "name")
    private String name;
	
	 @Column(name = "image_name")
     private String imageName;
 
     @Column(name = "description")
     private String description;
 
     @Column(name = "price")
     private Integer price;
 
     @Column(name = "capacity")
     private Integer capacity;
 
     @Column(name = "postal_code")
     private String postalCode;
 
     @Column(name = "address")
     private String address;
 
     @Column(name = "phone_number")
     private String phoneNumber;
     
     @ManyToOne
     @JoinColumn(name = "category_id")
     private Category category; 
 
     @Column(name = "created_at", insertable = false, updatable = false)
     private Timestamp createdAt;
 
     @Column(name = "updated_at", insertable = false, updatable = false)
     private Timestamp updatedAt;

  // リレーションシップの定義
     @OneToMany(mappedBy = "house", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
     private List<Review> reviews;

     // 平均評価を計算して取得するメソッド
     @Transient
     public Double getAverageScore() {
         return reviews.stream()
                       .mapToInt(Review::getRating)
                       .average()
                       .orElse(0.0);
     }
}
