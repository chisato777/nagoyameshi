const stripe = Stripe('pk_test_51Q6UcrP3g5YOCosXPpFHhhXfuWsob4rD71DgxAc0nLWjNAVo5zKF1cGsFAhzAAlfVf90sGR9k1uhOwP3Li3AlrVE00unqoKYqh');
const elements = stripe.elements();

// 統合型カード要素を作成
const card = elements.create('card', {
  style: {
    base: {
      fontSize: '16px',
      color: '#32325d',
      '::placeholder': {
        color: '#aab7c4',
      },
    },
    invalid: {
      color: '#fa755a',
    },
  },
});
card.mount('#cardElement'); // カード要素をHTMLの<div>にマウント

const cardButton = document.getElementById('cardButton');

cardButton.addEventListener('click', function(e) {
  e.preventDefault();

  // エラーメッセージを初期化
  document.getElementById('cardHolderNameError').innerHTML = '';
  document.getElementById('cardElementError').innerHTML = '';

  // カード名義人が未入力の場合はエラーメッセージを表示する
  const cardHolderName = document.getElementById('cardHolderName').value;
  if (!cardHolderName) {
    document.getElementById('cardHolderNameError').innerHTML = '<div class="text-danger small mb-2">カード名義人の入力は必須です。</div>';
    return;
  }

  // カード情報を使ってPaymentMethodを作成
     stripe.createPaymentMethod({
      type: 'card',
      card: card, // 統合型のカード要素を渡す
      billing_details: {
       name: cardHolderName, // カード名義人
     },
    }).then(function(result) {
      if (result.error) {
        // エラーメッセージを表示
        document.getElementById('cardElementError').innerHTML = '<div class="text-danger small mb-2">カード情報に不備があります。</div>';
      } else {
        // Payment MethodのIDをサーバーに送信
        stripePaymentIdHandler(result.paymentMethod.id);
      }
    });
  });

function stripePaymentIdHandler(paymentMethodId) {
  const form = document.getElementById('cardForm');

  const hiddenInput = document.createElement('input');
  hiddenInput.setAttribute('type', 'hidden');
  hiddenInput.setAttribute('name', 'paymentMethodId');
  hiddenInput.setAttribute('value', paymentMethodId);
  form.appendChild(hiddenInput);

  form.submit();
}