let stompClient = null;

function connect() {
    let socket = new SockJS("/ws");
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        stompClient.subscribe("/user/" + userId + "/queue/messages", function (message) {
            messageReceive(message);
        });
    });
}

function disconnect() {
    if (stompClient != null) {
        stompClient.disconnect();
    }
    console.log("disconnected")
}

const sendMessage = (msg, senderId, recipientId, senderName) => {
    sendMessageWithChatId(msg, senderId, recipientId, senderName, "");
};

const sendMessageWithChatId = (msg, senderId, recipientId, senderName, chatId) => {
    if (chatId !== "") {
        chatId = "/" + chatId;
    }
    if (msg.trim() !== "") {
        const message = {
            senderId: senderId,
            recipientId: recipientId,
            senderName: senderName,
            content: msg,
            time: new Date()
        };

        stompClient.send("/app/chat" + chatId, {}, JSON.stringify(message));
    }
};

$(() => connect());