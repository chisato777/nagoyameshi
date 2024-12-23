package com.example.samuraitravel.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.samuraitravel.entity.Category;
import com.example.samuraitravel.entity.House;
import com.example.samuraitravel.form.HouseEditForm;
import com.example.samuraitravel.form.HouseRegisterForm;
import com.example.samuraitravel.repository.CategoryRepository;
import com.example.samuraitravel.repository.HouseRepository;
import com.example.samuraitravel.service.HouseService;

@Controller
@RequestMapping("/admin/houses")
public class AdminHouseController {
 private final HouseRepository houseRepository;
 private final HouseService houseService;
 private final CategoryRepository categoryRepository;
     
 public AdminHouseController(HouseRepository houseRepository, HouseService houseService, CategoryRepository categoryRepository) {
         this.houseRepository = houseRepository;
         this.houseService = houseService;
         this.categoryRepository = categoryRepository;
     }	
     
     @GetMapping
     public String index(Model model, @PageableDefault(page = 0, size = 10, sort = "id", direction = Direction.ASC) Pageable pageable, @RequestParam(name = "keyword", required = false) String keyword) {
    	 Page<House> housePage;
         
         if (keyword != null && !keyword.isEmpty()) {
             housePage = houseRepository.findByNameLikeOrCategory_NameLike("%" + keyword + "%", "%" + keyword + "%", pageable);                
         } else {
             housePage = houseRepository.findAll(pageable);
         }  
                 
         model.addAttribute("housePage", housePage);
         model.addAttribute("keyword", keyword);
         
         return "admin/houses/index";
     }
     @GetMapping("/{id}")
     public String show(@PathVariable(name = "id") Integer id, Model model) {
         House house = houseRepository.getReferenceById(id);
         
         model.addAttribute("house", house);
         
         return "admin/houses/show";
     }
     
     @GetMapping("/register")
     public String register(Model model) {
    	 List<Category> categories = categoryRepository.findAll();
         model.addAttribute("categories", categories);
         
         model.addAttribute("houseRegisterForm", new HouseRegisterForm());
         return "admin/houses/register";
     } 
     
     @PostMapping("/create")
     public String create(@ModelAttribute @Validated HouseRegisterForm houseRegisterForm, BindingResult bindingResult, RedirectAttributes redirectAttributes) {        
         if (bindingResult.hasErrors()) {
             return "admin/houses/register";
         }
         
         houseService.create(houseRegisterForm);
         redirectAttributes.addFlashAttribute("successMessage", "店舗を登録しました。");    
         
         return "redirect:/admin/houses";
     }
     
     @GetMapping("/{id}/edit")
     public String edit(@PathVariable(name = "id") Integer id, Model model) {
         House house = houseRepository.getReferenceById(id);
         String imageName = house.getImageName();
         HouseEditForm houseEditForm = new HouseEditForm(house.getId(), house.getName(), null, house.getCategory().getId(), house.getDescription(), house.getPrice(), house.getCapacity(), house.getPostalCode(), house.getAddress(), house.getPhoneNumber());
         List<Category> categories = categoryRepository.findAll();
         
         model.addAttribute("categories", categories);
         model.addAttribute("imageName", imageName);
         model.addAttribute("houseEditForm", houseEditForm);
         
         return "admin/houses/edit";
     }
     
     @PostMapping("/{id}/update")
     public String update(@ModelAttribute @Validated HouseEditForm houseEditForm, BindingResult bindingResult, RedirectAttributes redirectAttributes) {        
         if (bindingResult.hasErrors()) {
             return "admin/houses/edit";
         }
         
         houseService.update(houseEditForm);
         redirectAttributes.addFlashAttribute("successMessage", "店舗情報を編集しました。");
         
         return "redirect:/admin/houses";
     }
     
     @PostMapping("/{id}/delete")
     public String delete(@PathVariable(name = "id") Integer id, RedirectAttributes redirectAttributes) {        
         houseRepository.deleteById(id);
                 
         redirectAttributes.addFlashAttribute("successMessage", "店舗を削除しました。");
         
         return "redirect:/admin/houses";
     }  
 }
  
    	