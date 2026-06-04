(function () {
    const chatRoomId = location.pathname.split('/').pop();
    const currentUserEmail = document.querySelector('meta[name="current-user-email"]').content;
    const messagesScroll = document.querySelector('.chat-messages-scroll');
    const input = document.getElementById('message');
    const imageFileInput = document.getElementById('image-file');

    window.onStompConnect = function () {
        window.stompClient.subscribe('/queue/chat/' + chatRoomId, function (frame) {
            const msg = JSON.parse(frame.body);
            renderMessage(msg);
        });
    };

    function renderMessage(msg) {
        const isMe = msg.senderEmail === currentUserEmail;
        const time = new Date().toLocaleTimeString('ko-KR', { hour: '2-digit', minute: '2-digit' });

        const section = document.createElement('section');
        section.className = 'chat-message-list';
        section.setAttribute('aria-label', '메시지 목록');

        const row = document.createElement('div');
        row.className = 'chat-message-row ' + (isMe ? 'is-me' : 'is-partner');

        const timeEl = document.createElement('time');
        timeEl.className = 'chat-message-time';
        timeEl.textContent = time;

        let contentEl;
        if (msg.messageType === 'IMAGE') {
            contentEl = document.createElement('img');
            contentEl.className = 'chat-message-image';
            contentEl.src = msg.message;
            contentEl.alt = '채팅 이미지';
        } else {
            contentEl = document.createElement('p');
            contentEl.className = 'chat-bubble ' + (isMe ? 'chat-bubble-me' : 'chat-bubble-partner');
            contentEl.textContent = msg.message;
        }

        if (isMe) {
            row.appendChild(timeEl);
            row.appendChild(contentEl);
        } else {
            row.appendChild(contentEl);
            row.appendChild(timeEl);
        }

        const isNearBottom = messagesScroll.scrollHeight - messagesScroll.scrollTop - messagesScroll.clientHeight < 100;
        section.appendChild(row);
        messagesScroll.appendChild(section);
        if (isNearBottom) messagesScroll.scrollTop = messagesScroll.scrollHeight;
        updateTimes();
    }

    function updateTimes() {
        const rows = messagesScroll.querySelectorAll('.chat-message-row');
        rows.forEach((row, i) => {
            const timeEl = row.querySelector('.chat-message-time');
            if (!timeEl) return;
            timeEl.style.visibility = 'visible';

            const next = rows[i + 1];
            if (!next) return;
            const nextTimeEl = next.querySelector('.chat-message-time');
            if (!nextTimeEl) return;

            const sameDirection = row.classList.contains('is-me') === next.classList.contains('is-me');
            if (sameDirection && timeEl.textContent === nextTimeEl.textContent) {
                timeEl.style.visibility = 'hidden';
            }
        });
    }

    messagesScroll.scrollTop = messagesScroll.scrollHeight;
    updateTimes();

    function sendMessage() {
        const text = input.value.trim();
        if (!text || !window.stompClient?.connected) return;

        window.stompClient.send(
            '/publish/chat/' + chatRoomId,
            {},
            JSON.stringify({ message: text, senderEmail: currentUserEmail, messageType: 'TEXT' })
        );
        input.value = '';
    }

    async function sendImage(file) {
        const formData = new FormData();
        formData.append('files', file);

        const res = await fetch('/api/uploads/images',
            {
                method: 'POST',
                body: formData
        });
        const data = await res.json();
        const url = data.data.imageUrls[0];

        window.stompClient.send(
            '/publish/chat/' + chatRoomId,
            {},
            JSON.stringify({ message: url, senderEmail: currentUserEmail, messageType: 'IMAGE' })
        );
    }

    async function updateReadAt(){
        await fetch(`/mobile/chat/${chatRoomId}/readAt`);
    }

    document.querySelector('.chat-image-button').addEventListener('click', () => imageFileInput.click());
    imageFileInput.addEventListener('change', function () {
        if (this.files[0]) sendImage(this.files[0]);
        this.value = '';
    });

    document.querySelector('.chat-send-button').addEventListener('click', sendMessage);
    input.addEventListener('keypress', function (e) {
        if (e.key === 'Enter') {
            sendMessage();
            e.preventDefault();
        }
    });

    updateReadAt();
})();