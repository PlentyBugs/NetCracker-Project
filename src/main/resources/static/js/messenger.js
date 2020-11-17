let stompClient = null;
let token = $('#_csrf').attr('content');
let header = $('#_csrf_header').attr('content');
let userId = $("#zzz").attr("value");
let currentChatId = "";
let currentRecipientId = "";
let chatSearch = $("#search-chat");
let chats = $(".chat-in-menu");

chatSearch.keyup(() => {
    sort(chatSearch.val());
});

function sort(filter) {
    for (let chat of chats) {
        if ($(chat).text().toLowerCase().includes(filter.toLowerCase())) {
            $(chat).css("display", "block");
        } else {
            $(chat).css("display", "none");
        }
    }
}

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
    if (content !== "") {
        sendMessage(content, userId, recipientId, senderName, recipientName);

        setTimeout(() => {
            showChat(currentRecipientId);
            let chatWindow = document.getElementById("chat-window");
            chatWindow.scrollTop = chatWindow.scrollHeight
        }, 500);
    }
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
    senderName = chat.senderName;
    recipientName = chat.recipientName;

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
    let chatWindow2 = document.getElementById("chat-window");
    chatWindow2.scrollTop = chatWindow2.scrollHeight;

    $("#input-message").remove();

    let inputMessageBlock = $("<div id='input-message'></div>");
    let messageTextBlock = $("<textarea placeholder='Message...' name='message' id='message-text'></textarea>");
    let sendIconBlock = $("<i class='fa fa-paper-plane' id='send-button' onclick='send(`" + recipientId + "`, `" + senderName + "`, `" + recipientName + "`);'></i>");

    inputMessageBlock.keyup(evt => {
        evt.preventDefault();
        if (evt.which === 13) {
            send(recipientId, senderName, recipientName);
        }
        return false;
    })

    inputMessageBlock.append(messageTextBlock);
    inputMessageBlock.append(sendIconBlock);

    chatWindow.after(inputMessageBlock);

    $("#chat-header").remove();

    let chatHeader = $("<span class='text-center' id='chat-header'>" + (typeof (recipientName) == "undefined" ? "Nobody": recipientName) + "</span>");

    chatWindow.before(chatHeader);
}

$(() => {
    connect();
    const regexp = /(.+?)messenger\/?(.*)/;
    currentRecipientId = document.URL.match(regexp)[2];
    if (currentRecipientId === "") {
        showChat(userId);
    } else {
        showChat(currentRecipientId);
    }
    $("#chats").outerHeight($("#chat-block").outerHeight);
});