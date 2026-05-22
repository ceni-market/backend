document.addEventListener('DOMContentLoaded', () => {
    const cancelButton = document.getElementById('listingCancelButton');
    const typeInputs = document.querySelectorAll('input[name="type"]');
    const priceInput = document.getElementById('price');
    const imageUploadButton = document.getElementById('imageUploadButton');
    const imageUploadText = document.getElementById('imageUploadText');
    const imageInput = document.getElementById('images');
    const defaultImageUploadText = imageUploadText.textContent.trim();
    const maxImageSize = 5 * 1024 * 1024;

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

    imageUploadButton.addEventListener('click', () => {
        imageInput.click();
    });

    imageInput.addEventListener('change', () => {
        const selectedCount = imageInput.files.length;

        if (selectedCount > 10) {
            alert('이미지는 최대 10장까지 등록할 수 있습니다.');
            imageInput.value = '';
            imageUploadText.textContent = defaultImageUploadText;
            return;
        }

        const oversizedFile = Array.from(imageInput.files).find((file) => file.size > maxImageSize);

        if (oversizedFile) {
            alert('이미지는 개당 5MB 이하만 등록할 수 있습니다.');
            imageInput.value = '';
            imageUploadText.textContent = defaultImageUploadText;
            return;
        }

        if (selectedCount > 0) {
            const firstFileName = imageInput.files[0].name;

            if (selectedCount === 1) {
                imageUploadText.textContent = `${firstFileName} 선택됨`;
                return;
            }

            imageUploadText.textContent = `${firstFileName} 외 ${selectedCount - 1}개 선택됨`;
            return;
        }

        imageUploadText.textContent = defaultImageUploadText;
    });
});
