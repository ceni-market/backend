(function () {
    let swRegistration = null;

    async function requestPermission() {
        if (!('Notification' in window)) return;
        if (Notification.permission === 'default') {
            await Notification.requestPermission();
        }
    }

    async function registerSW() {
        if (!('serviceWorker' in navigator)) return;
        try {
            swRegistration = await navigator.serviceWorker.register('/service-worker.js');
        } catch (e) {
            console.error('SW 등록 실패:', e);
        }
    }

    async function showNotification(data) {
        if (Notification.permission !== 'granted') return;
        if (location.pathname === '/mobile/chat/' + data.chatRoomId) return;

        const body = data.messageType === 'IMAGE' ? '이미지' : data.messagePreview;
        const options = {
            body,
            icon: '/images/logo.webp',
            tag: 'chat-' + data.chatRoomId,
            data: { url: '/mobile/chat/' + data.chatRoomId },
        };

        if (swRegistration) {
            swRegistration.showNotification('세니마켓', options);
        } else {
            const notif = new Notification('세니마켓', options);
            notif.onclick = () => {
                window.focus();
                location.href = '/mobile/chat/' + data.chatRoomId;
                notif.close();
            };
            setTimeout(() => notif.close(), 5000);
        }
    }

        function connect() {
            const socket = new SockJS('/connect', null, {withCredentials: true});
            window.stompClient = Stomp.over(socket);

            window.stompClient.connect({}, () => {
                window.stompClient.subscribe('/user/queue/notification', (frame) => {
                    const data = JSON.parse(frame.body);
                    showNotification(data);
                    window.dispatchEvent(new CustomEvent('chatNotification', { detail: data }));
                });
                window.onStompConnect?.();
            }, (error) => {
                console.error('알림 연결 실패:', error);
            });
        }

    window.addEventListener('beforeunload', () => {
        if (window.stompClient?.connected) window.stompClient.disconnect();
    });

    requestPermission();
    registerSW();
    connect();
})();