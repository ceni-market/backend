(function () {

    async function requestPermission() {
        if (!('Notification' in window)) return;
        if (Notification.permission === 'default') {
            await Notification.requestPermission();
        }
    }

    function showNotification(data) {
        if (Notification.permission !== 'granted') return;
        if (location.pathname === '/mobile/chat/' + data.chatRoomId) return;
        console.log('알림 생성:', data.chatRoomId, location.pathname);
        const body = data.messageType === 'IMAGE' ? '이미지' : data.messagePreview;
        const notif = new Notification('세니마켓', {
            body,
            icon: '/images/logo.webp',
            tag: 'chat-' + data.chatRoomId,
        });
        notif.onclick = () => {
            window.focus();
            location.href = '/mobile/chat/' + data.chatRoomId;
            notif.close();
        };
        setTimeout(() => notif.close(), 5000);
    }

    function connect() {
        const socket = new SockJS('/connect', null, { withCredentials: true });
        window.stompClient = Stomp.over(socket);
        window.stompClient.debug = null;

        window.stompClient.connect({}, () => {
            window.stompClient.subscribe('/user/queue/notification', (frame) => {
                const data = JSON.parse(frame.body);
                console.log(data.chatRoomId);
                console.log(Notification.permission);
                console.log(location.pathname);
                showNotification(data);
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
    connect();
})();