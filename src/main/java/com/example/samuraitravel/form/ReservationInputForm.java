package com.example.samuraitravel.form;

import java.time.LocalDate;
import java.time.LocalTime;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ReservationInputForm {
//	@NotBlank(message = "チェックイン日とチェックアウト日を選択してください。")
//    private String fromCheckinDateToCheckoutDate;    
//    
//    @NotNull(message = "宿泊人数を入力してください。")
//    @Min(value = 1, message = "宿泊人数は1人以上に設定してください。")
//    private Integer numberOfPeople; 
//
//    // チェックイン日を取得する
//    public LocalDate getCheckinDate() {
//        String[] checkinDateAndCheckoutDate = getFromCheckinDateToCheckoutDate().split(" から ");
//        return LocalDate.parse(checkinDateAndCheckoutDate[0]);
//    }
//
//    // チェックアウト日を取得する
//    public LocalDate getCheckoutDate() {
//        String[] checkinDateAndCheckoutDate = getFromCheckinDateToCheckoutDate().split(" から ");
//        return LocalDate.parse(checkinDateAndCheckoutDate[1]);
//    }
    
    @NotBlank(message = "来店日を選択してください。")
    private String visitDate; // 来店日

    @NotBlank(message = "来店時間を選択してください。")
    private String visitTime; // 来店時間

    @NotNull(message = "人数を入力してください。")
    @Min(value = 1, message = "人数は1人以上に設定してください。")
    private Integer numberOfPeople; // 人数

    // LocalDate型の来店日を取得
    public LocalDate getParsedVisitDate() {
        return LocalDate.parse(this.visitDate);
    }

    // LocalTime型の来店時間を取得
    public LocalTime getParsedVisitTime() {
        return LocalTime.parse(this.visitTime);
    }
}
