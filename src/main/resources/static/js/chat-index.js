(function () {
    const LONG_PRESS_MS = 500;
    let timer = null;

    function showDeleteButton(card) {
        document.querySelectorAll('.chat-room-delete').forEach(el => el.remove());

        const btn = document.createElement('button');
        btn.className = 'chat-room-delete';
        btn.textContent = '삭제';
        btn.addEventListener('click', (e) => {
            e.stopPropagation();
            deleteRoom(card);
        });
        card.parentElement.appendChild(btn);
    }

    function deleteRoom(card) {
        const roomId = card.dataset.roomId;
        fetch('/mobile/chat/' + roomId, { method: 'DELETE' })
            .then(res => {
                if (res.ok) card.parentElement.remove();
            });
    }

    document.querySelectorAll('.chat-room-card').forEach(card => {
        card.addEventListener('touchstart', () => {
            timer = setTimeout(() => showDeleteButton(card), LONG_PRESS_MS);
        });
        card.addEventListener('touchmove', () => clearTimeout(timer));
        card.addEventListener('touchend', () => clearTimeout(timer));
        card.addEventListener('touchcancel', () => clearTimeout(timer));
    });

    document.addEventListener('click', (e) => {
        if (!e.target.closest('.chat-room-delete')) {
            document.querySelectorAll('.chat-room-delete').forEach(el => el.remove());
        }
    });
})();