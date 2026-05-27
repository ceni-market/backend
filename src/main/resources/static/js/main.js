document.addEventListener('DOMContentLoaded', () => {
    document.addEventListener('click', async (event) => {
        const button = event.target.closest('.listing-like[data-listing-id]');

        if (!button) {
            return;
        }

        const listingId = button.dataset.listingId;
        const liked = button.classList.contains('is-liked');
        const method = liked ? 'DELETE' : 'POST';

        button.disabled = true;

        try {
            const response = await fetch(`/mobile/listings/${listingId}/likes`, {
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

    const listingList = document.getElementById('listingList');
    const scrollTarget = document.getElementById('infiniteScrollTarget');

    if (!listingList || !scrollTarget) {
        return;
    }

    let nextPage = Number(scrollTarget.dataset.nextPage);
    let hasNext = scrollTarget.dataset.hasNext === 'true';
    let loading = false;

    const observer = new IntersectionObserver(async (entries) => {
        const entry = entries[0];

        if (!entry.isIntersecting || loading || !hasNext) {
            return;
        }

        loading = true;
        scrollTarget.classList.add('is-loading');

        try {
            const params = new URLSearchParams(window.location.search);
            params.set('page', String(nextPage));
            params.set('fragment', 'true');

            const response = await fetch(`/mobile/main?${params.toString()}`);

            if (!response.ok) {
                return;
            }

            const html = await response.text();
            const template = document.createElement('template');
            template.innerHTML = html.trim();
            const newCards = template.content.querySelectorAll('.listing-card');

            if (newCards.length === 0) {
                hasNext = false;
                scrollTarget.classList.add('is-end');
                observer.unobserve(scrollTarget);
                return;
            }

            newCards.forEach((card) => {
                listingList.insertBefore(card, scrollTarget);
            });
            nextPage += 1;
        } finally {
            loading = false;
            scrollTarget.classList.remove('is-loading');
        }
    }, {
        rootMargin: '120px',
        threshold: 0
    });

    if (hasNext) {
        observer.observe(scrollTarget);
    } else if (listingList.querySelector('.listing-card')) {
        scrollTarget.classList.add('is-end');
    } else {
        scrollTarget.hidden = true;
    }
});
