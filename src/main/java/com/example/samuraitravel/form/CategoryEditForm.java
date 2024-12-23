package com.example.samuraitravel.form;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CategoryEditForm {
	@NotNull
    private Integer id;    
    
    @NotBlank(message = "カテゴリー名を入力してください。")
    private String name;
}
