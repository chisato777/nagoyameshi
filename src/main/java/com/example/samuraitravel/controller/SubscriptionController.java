package com.example.samuraitravel.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.samuraitravel.entity.User;
import com.example.samuraitravel.security.UserDetailsImpl;
import com.example.samuraitravel.service.StripeSubscriptionService;
import com.example.samuraitravel.service.UserService;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.model.PaymentMethod;
import com.stripe.model.Subscription;

@Controller
@RequestMapping("/subscription")
public class SubscriptionController {
	@Value("${stripe.premium-plan-price-id}")
    private String premiumPlanPriceId;
	
	private final StripeSubscriptionService stripeSubscriptionService;
	private final UserService userService;
	
	public SubscriptionController(StripeSubscriptionService stripeSubscriptionService, UserService userService) {
		this.stripeSubscriptionService = stripeSubscriptionService;
		this.userService = userService;
	}
	
	@GetMapping("/register")
	public String register() {
		return "subscription/register";
	}
	
	@PostMapping("/create")
	public String create(@AuthenticationPrincipal UserDetailsImpl userDetailsImpl, @RequestParam String paymentMethodId, RedirectAttributes redirectAttributes) {
        User user = userDetailsImpl.getUser();
        
     // ユーザーのstripeCustomerIdフィールドがnull、つまりそのユーザーが初めてサブスクリプションに加入する場合の処理
        if (user.getStripeCustomerId() == null) {
            try {
                // 顧客（StripeのCustomerオブジェクト）を作成する
                Customer customer = stripeSubscriptionService.createCustomer(user);

                // stripeCustomerIdフィールドに顧客IDを保存する
                userService.saveStripeCustomerId(user, customer.getId());
            } catch (StripeException e) {
                redirectAttributes.addFlashAttribute("errorMessage", "有料プランへの登録に失敗しました。再度お試しください。");

                return "redirect:/subscription/register";
            }
        }

        String stripeCustomerId = user.getStripeCustomerId();

        try {
            // フォームから送信された支払い方法（StripeのPaymentMethodオブジェクト）を顧客に紐づける
        	stripeSubscriptionService.attachPaymentMethodToCustomer(paymentMethodId, stripeCustomerId);

            // フォームから送信された支払い方法を顧客のデフォルトの支払い方法に設定する
        	stripeSubscriptionService.setDefaultPaymentMethod(paymentMethodId, stripeCustomerId);

            // サブスクリプション（StripeのSubscriptionオブジェクト）を作成する
        	stripeSubscriptionService.createSubscription(stripeCustomerId, premiumPlanPriceId);

        } catch (StripeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "有料プランへの登録に失敗しました。再度お試しください。");

            return "redirect:/subscription/register";
        }

        // ユーザーのロールを更新する
        userService.updateRole(user, "ROLE_PAID_MEMBER");
        userService.refreshAuthenticationByRole("ROLE_PAID_MEMBER");

        redirectAttributes.addFlashAttribute("successMessage", "有料プランへの登録が完了しました。");

        return "redirect:/user";
    }
	
	@GetMapping("/edit")
    public String edit(@AuthenticationPrincipal UserDetailsImpl userDetailsImpl, RedirectAttributes redirectAttributes, Model model) {
        User user = userDetailsImpl.getUser();

        try {
            // 顧客のデフォルトの支払い方法（StripeのPaymentMethodオブジェクト）を取得する
            PaymentMethod paymentMethod = stripeSubscriptionService.getDefaultPaymentMethod(user.getStripeCustomerId());

            model.addAttribute("card", paymentMethod.getCard());
            model.addAttribute("cardHolderName", paymentMethod.getBillingDetails().getName());
        } catch (StripeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "お支払い方法を取得できませんでした。再度お試しください。");

            return "redirect:/subscription/edit";
        }

        return "subscription/edit";
    }

    @PostMapping("/update")
    public String update(@AuthenticationPrincipal UserDetailsImpl userDetailsImpl, @RequestParam String paymentMethodId,RedirectAttributes redirectAttributes) {
        User user = userDetailsImpl.getUser();
        String stripeCustomerId = user.getStripeCustomerId();

        try {
            // 現在のデフォルトの支払い方法（StripeのPaymentMethodオブジェクト）のIDを取得する
            String currentDefaultPaymentMethodId = stripeSubscriptionService.getDefaultPaymentMethodId(stripeCustomerId);

            // フォームから送信された支払い方法を顧客（StripeのCustomerオブジェクト）に紐づける
            stripeSubscriptionService.attachPaymentMethodToCustomer(paymentMethodId, stripeCustomerId);

            // フォームから送信された支払い方法を顧客のデフォルトの支払い方法に設定する
            stripeSubscriptionService.setDefaultPaymentMethod(paymentMethodId, stripeCustomerId);

            // 以前のデフォルトの支払い方法と顧客の紐づけを解除する
            stripeSubscriptionService.detachPaymentMethodFromCustomer(currentDefaultPaymentMethodId);
        } catch (StripeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "お支払い方法の変更に失敗しました。再度お試しください。");

            return "redirect:/user";
        }

        redirectAttributes.addFlashAttribute("successMessage", "お支払い方法を変更しました。");

        return "redirect:/";
    }

    @GetMapping("/cancel")
    public String cancel() {
        return "subscription/cancel";
    }

    @PostMapping("/delete")
    public String delete(@AuthenticationPrincipal UserDetailsImpl userDetailsImpl, RedirectAttributes redirectAttributes) {
        User user = userDetailsImpl.getUser();

        try {
            // 顧客が契約中のサブスクリプション（StripeのSubscriptionオブジェクト）を取得する
            List<Subscription> subscriptions = stripeSubscriptionService.getSubscriptions(user.getStripeCustomerId());

            // 顧客が契約中のサブスクリプションをキャンセルする
            stripeSubscriptionService.cancelSubscriptions(subscriptions);

            // デフォルトの支払い方法（StripeのPaymentMethodオブジェクト）のIDを取得する
            String defaultPaymentMethodId = stripeSubscriptionService.getDefaultPaymentMethodId(user.getStripeCustomerId());

            // デフォルトの支払い方法と顧客の紐づけを解除する
            stripeSubscriptionService.detachPaymentMethodFromCustomer(defaultPaymentMethodId);
        } catch (StripeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "有料プランの解約に失敗しました。再度お試しください。");

            return "redirect:/";
        }

        // ユーザーのロールを更新する
        userService.updateRole(user, "ROLE_FREE_MEMBER");
        userService.refreshAuthenticationByRole("ROLE_FREE_MEMBER");

        redirectAttributes.addFlashAttribute("successMessage", "有料プランを解約しました。");

        return "redirect:/";
    }
}
