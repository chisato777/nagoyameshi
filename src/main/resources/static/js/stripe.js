const stripe = Stripe('pk_test_51Q6UcrP3g5YOCosXPpFHhhXfuWsob4rD71DgxAc0nLWjNAVo5zKF1cGsFAhzAAlfVf90sGR9k1uhOwP3Li3AlrVE00unqoKYqh');
 const paymentButton = document.querySelector('#paymentButton');
 
 paymentButton.addEventListener('click', () => {
   stripe.redirectToCheckout({
     sessionId: sessionId
   })
 });
 
 