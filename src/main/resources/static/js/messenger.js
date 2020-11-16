let stompClient = null;
let token = $('#_csrf').attr('content');
let header = $('#_csrf_header').attr('content');
let userId = $("#zzz").attr("value");
let currentChatId = "";
let currentRecipientId = "";

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

const messageReceive = (msg) => {
    const notification = JSON.parse(msg.body);

    if (currentRecipientId == notification.senderId) {
        showChat(currentRecipientId);
    }
};

function send(recipientId, senderName, recipientName) {
    let input = $("#message-text");
    let content = input.val();
    input.val("");
    sendMessage(content, userId, recipientId, senderName, recipientName);

    setTimeout(() => {
        showChat(currentRecipientId);
        let chatWindow = document.getElementById("chat-window");
        chatWindow.scrollTop = chatWindow.scrollHeight
    }, 500);
}

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
    let senderName = "";
    let recipientName = "";
    currentRecipientId = recipientId;

    $.ajax({
        type: 'GET',
        url: url + userId + "/chat/" + recipientId,
        beforeSend: (xhr) => xhr.setRequestHeader(header, token),
        success: (data) => chat = data,
        cache: false,
        async: false
    });
    let chatMessages = {};
    $.ajax({
        type: 'GET',
        url: url + chat.chatId + "/messages",
        beforeSend: (xhr) => xhr.setRequestHeader(header, token),
        success: (data) => chatMessages = data,
        cache: false,
        async: false
    });

    currentChatId = chat.chatId;

    let chatWindow = $("#chat-window");
    chatWindow.empty();
    let length = chatMessages.length;

    for (let i = 0; i < length; i++) {
        let message = chatMessages[i];
        if (senderName === "" && recipientName === "") {
            senderName = message.senderName;
            recipientName = message.recipientName;
        }
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
    let chatWindow2 = document.getElementById("chat-window");
    chatWindow2.scrollTop = chatWindow2.scrollHeight;

    $("#input-message").remove();

    chatWindow.after($("<div id='input-message'><textarea placeholder='Message...' name='message' id='message-text'></textarea><i class='fa fa-paper-plane' id='send-button' onclick='send(`" + recipientId + "`, `" + senderName + "`, `" + recipientName + "`);'></i></div>"));
}

$(() => connect());