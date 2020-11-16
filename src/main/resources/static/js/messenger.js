let stompClient = null;
let token = $('#_csrf').attr('content');
let header = $('#_csrf_header').attr('content');
let userId = $("#zzz").attr("value");

function connect() {
    let socket = new SockJS("/ws");
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        stompClient.subscribe("/user/" + userId + "/queue/messages", function (message) {
            console.log
            (message);
        });
    });
}

function disconnect() {
    if (stompClient != null) {
        stompClient.disconnect();
    }
    console.log("disconnected")
}

const sendMessage = (msg, senderId, recipientId, senderName, recipientName) => {
    if (msg.trim() !== "") {
        const message = {
            senderId: senderId,
            recipientId: recipientId,
            senderName: senderName,
            recipientName: recipientName,
            content: msg,
            time: new Date()
        };

        stompClient.send("/app/chat", {}, JSON.stringify(message));
    }
};

function getPosition(message) {
    let position = "yours";
    if (message.senderId === userId) {
        position = "mine";
    }
    return position;
}

function showChat(recipientId) {
    let chat = {};
    const regexp = /(.+?)messenger(.*)/;
    const url = document.URL.match(regexp)[1] + "messenger/";
    $.ajax({
        type: 'GET',
        url: url + userId + "/chat/" + recipientId,
        beforeSend: (xhr) => xhr.setRequestHeader(header, token),
        success: (data) => chat = data,
        cache: false,
        async: false
    });
    console.log(chat);
    let chatMessages = {};
    $.ajax({
        type: 'GET',
        url: url + chat.chatId + "/messages",
        beforeSend: (xhr) => xhr.setRequestHeader(header, token),
        success: (data) => chatMessages = data,
        cache: false,
        async: false
    });
    console.log(chatMessages);
    let chatWindow = $("#chat-window");
    chatWindow.empty();
    let length = chatMessages.length;
    for (let i = 0; i < length; i++) {
        let message = chatMessages[i];
        let position = getPosition(message);

        let messages = $("<div class='" + position + " messages'></div>")

        while (i + 1 < length && position === getPosition(chatMessages[i + 1])) {
            position = getPosition(message);
            messages.append($("<div class='message'>" + message.content + "</div>"));
            message = chatMessages[++i];
        }

        messages.append($("<div class='message last'>" + message.content + "</div>"));

        chatWindow.append(messages);
    }

    $("#input-message").remove();

    chatWindow.after($("<div id='input-message'><textarea name='message' id='message-text'></textarea><i class='fa fa-paper-plane' id='send-button'></i></div>"));
}

$(() => connect());