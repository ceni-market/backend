document.addEventListener('DOMContentLoaded', () => {
    const likeButtons = document.querySelectorAll('.listing-like[data-listing-id]');

    likeButtons.forEach((button) => {
        button.addEventListener('click', async () => {
            const listingId = button.dataset.listingId;
            const liked = button.classList.contains('is-liked');
            const method = liked ? 'DELETE' : 'POST';

            button.disabled = true;

            try {
                const response = await fetch(`/api/listings/${listingId}/likes`, {
                    method
                });

                if (!response.ok) {
                    const result = await response.json();
                    alert(result.message || '관심 등록에 실패했습니다.');
                    return;
                }

                const result = await response.json();
                const like = result.data;

                button.classList.toggle('is-liked', like.liked);
                button.setAttribute('aria-pressed', String(like.liked));
                const icon = button.querySelector('i');
                icon.classList.toggle('bi-heart-fill', like.liked);
                icon.classList.toggle('bi-heart', !like.liked);
                button.querySelector('span').textContent = like.likeCount;
            } finally {
                button.disabled = false;
            }
        });
    });
});
