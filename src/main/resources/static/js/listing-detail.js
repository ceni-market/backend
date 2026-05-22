document.addEventListener('DOMContentLoaded', () => {
    const mainImage = document.querySelector('.detail-main-image');
    const thumbnails = Array.from(document.querySelectorAll('.detail-thumbnail'));

    if (!mainImage || thumbnails.length === 0) {
        return;
    }

    let currentIndex = Math.max(
        thumbnails.findIndex((thumbnail) => thumbnail.classList.contains('is-active')),
        0
    );
    let touchStartX = 0;
    let touchStartY = 0;

    const changeImage = (nextIndex) => {
        if (nextIndex < 0 || nextIndex >= thumbnails.length) {
            return;
        }

        const nextThumbnail = thumbnails[nextIndex];

        mainImage.src = nextThumbnail.src;
        mainImage.alt = nextThumbnail.alt;

        thumbnails[currentIndex].classList.remove('is-active');
        nextThumbnail.classList.add('is-active');
        nextThumbnail.scrollIntoView({
            behavior: 'smooth',
            block: 'nearest',
            inline: 'center'
        });

        currentIndex = nextIndex;
    };

    thumbnails.forEach((thumbnail, index) => {
        thumbnail.addEventListener('click', () => {
            changeImage(index);
        });
    });

    mainImage.addEventListener('touchstart', (event) => {
        const touch = event.touches[0];
        touchStartX = touch.clientX;
        touchStartY = touch.clientY;
    });

    mainImage.addEventListener('touchend', (event) => {
        const touch = event.changedTouches[0];
        const diffX = touch.clientX - touchStartX;
        const diffY = touch.clientY - touchStartY;
        const minSwipeDistance = 50;

        if (Math.abs(diffX) < minSwipeDistance || Math.abs(diffX) < Math.abs(diffY)) {
            return;
        }

        if (diffX < 0) {
            changeImage(currentIndex + 1);
            return;
        }

        changeImage(currentIndex - 1);
    });
});
