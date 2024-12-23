package com.example.samuraitravel.controller;

//@Controller
//public class StripeWebhookController {
//	private final StripeService stripeService;
//	 
//    @Value("${stripe.api-key}")
//    private String stripeApiKey;
//
//    @Value("${stripe.webhook-secret}")
//    private String webhookSecret;
//
//    public StripeWebhookController(StripeService stripeService) {
//        this.stripeService = stripeService;
//    }
//
//    @PostMapping("/stripe/webhook")
//    public ResponseEntity<String> webhook(@RequestBody String payload, @RequestHeader("Stripe-Signature") String sigHeader) {
//        Stripe.apiKey = stripeApiKey;
//        Event event = null;
//
//        try {
//            event = Webhook.constructEvent(payload, sigHeader, webhookSecret);
//        } catch (SignatureVerificationException e) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
//        }
//
//        if ("checkout.session.completed".equals(event.getType())) {
//            stripeService.processSessionCompleted(event);
//        }
//
//        return new ResponseEntity<>("Success", HttpStatus.OK);
//    }
//}
