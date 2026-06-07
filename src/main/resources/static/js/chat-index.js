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

    function attachTouchListeners(card) {
        card.addEventListener('contextmenu', (e) => e.preventDefault());
        card.addEventListener('touchstart', () => {
            timer = setTimeout(() => showDeleteButton(card), LONG_PRESS_MS);
        });
        card.addEventListener('touchmove', () => clearTimeout(timer));
        card.addEventListener('touchend', () => clearTimeout(timer));
        card.addEventListener('touchcancel', () => clearTimeout(timer));
    }

    function buildCardHTML(room) {
        const lastMsg = room.lastMessageInfo?.messageType === 'IMAGE'
            ? '이미지'
            : (room.lastMessageInfo?.content ?? '');
        const badge = room.unReadMessageCount > 0
            ? `<span class="chat-room-badge">${room.unReadMessageCount}</span>`
            : '';
        return `
            <a class="chat-room-card"
               href="/mobile/chat/${room.chatRoomId}"
               data-room-id="${room.chatRoomId}">
                <img class="chat-room-avatar"
                     src="${room.contactUserInfo.profileImageUrl ?? '/images/profile/default.png'}"
                     alt="대화 상대 프로필 이미지">
                <div class="chat-room-info">
                    <strong class="chat-room-name">${room.contactUserInfo.name}</strong>
                    <span class="chat-room-item">${room.listingInfo.title}</span>
                    <span class="chat-room-message">${lastMsg}</span>
                </div>
                <div class="chat-room-meta">
                    <time>${room.lastMessageAtConvert ?? ''}</time>
                    ${badge}
                </div>
            </a>`;
    }

    function refreshChatList() {
        fetch('/mobile/chat/mychat')
            .then(res => res.json())
            .then(json => {
                const rooms = json.data;
                const chatList = document.querySelector('.chat-list');
                if (!chatList) return;

                chatList.querySelectorAll('.chat-room-delete').forEach(el => el.remove());

                rooms.forEach(room => {
                    const existing = chatList.querySelector(`.chat-room-card[data-room-id="${room.chatRoomId}"]`);
                    if (existing) {
                        const wrapper = existing.parentElement;
                        wrapper.innerHTML = buildCardHTML(room);
                        attachTouchListeners(wrapper.querySelector('.chat-room-card'));
                    } else {
                        const wrapper = document.createElement('div');
                        wrapper.className = 'chat-room-card-wrapper';
                        wrapper.innerHTML = buildCardHTML(room);
                        chatList.prepend(wrapper);
                        attachTouchListeners(wrapper.querySelector('.chat-room-card'));
                    }
                });

                const roomMap = new Map(rooms.map(r => [String(r.chatRoomId), r]));
                const wrappers = [...chatList.querySelectorAll('.chat-room-card-wrapper')];
                wrappers.sort((a, b) => {
                    const idA = a.querySelector('.chat-room-card')?.dataset.roomId;
                    const idB = b.querySelector('.chat-room-card')?.dataset.roomId;
                    const timeA = roomMap.get(idA)?.lastMessageAt ?? '';
                    const timeB = roomMap.get(idB)?.lastMessageAt ?? '';
                    return timeB > timeA ? 1 : -1;
                });
                wrappers.forEach(w => chatList.appendChild(w));
            });
    }

    function searchChatList(keyword) {
        const q = keyword.trim().toLowerCase();
        document.querySelectorAll('.chat-room-card-wrapper').forEach(wrapper => {
            const name = wrapper.querySelector('.chat-room-name')?.textContent.toLowerCase() ?? '';
            const item = wrapper.querySelector('.chat-room-item')?.textContent.toLowerCase() ?? '';
            wrapper.style.display = (!q || name.includes(q) || item.includes(q)) ? '' : 'none';
        });
    }

    document.getElementById('chatKeyword')?.addEventListener('input', (e) => {
        searchChatList(e.target.value);
    });

    document.querySelector('.chat-search')?.addEventListener('submit', (e) => e.preventDefault());

    document.querySelectorAll('.chat-room-card').forEach(attachTouchListeners);

    document.addEventListener('click', (e) => {
        if (!e.target.closest('.chat-room-delete')) {
            document.querySelectorAll('.chat-room-delete').forEach(el => el.remove());
        }
    });

    window.addEventListener('chatNotification', () => refreshChatList());
})();