document.addEventListener('DOMContentLoaded', () => {
    const cancelButton = document.getElementById('listingCancelButton');
    const typeInputs = document.querySelectorAll('input[name="type"]');
    const priceInput = document.getElementById('price');

    cancelButton.addEventListener('click', () => {
        const confirmed = confirm('작성 중인 내용이 사라집니다. 취소할까요?');

        if (confirmed) {
            window.location.href = cancelButton.dataset.cancelUrl || '/mobile/main';
        }
    });

    typeInputs.forEach((input) => {
        input.addEventListener('change', () => {
            if (input.value === 'GIVEAWAY' && input.checked) {
                priceInput.value = 0;
                priceInput.readOnly = true;
                return;
            }

            if (input.value === 'SALE' && input.checked) {
                priceInput.value = '';
                priceInput.readOnly = false;
            }
        });
    });
});
