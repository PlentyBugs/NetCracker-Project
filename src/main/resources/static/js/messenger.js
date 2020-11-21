let token = $('#_csrf').attr('content');
let header = $('#_csrf_header').attr('content');
let currentChatId = "";
let chatSearch = $("#search-chat");
let chats = $(".chat-in-menu");
let currentRecipientId = "";
const userId = $("#zzz").attr("value");
const username = $("#mmm").attr("value");
const regexp = /(.+?)messenger(.*)/;
const url = document.URL.match(regexp)[1] + "messenger/";

chatSearch.keyup(() => {
    filter(chatSearch.val());
});

function filter(filter) {
    for (let chat of chats) {
        if ($(chat).text().toLowerCase().includes(filter.toLowerCase())) {
            $(chat).css("display", "block");
        } else {
            $(chat).css("display", "none");
        }
    }
}

const messageReceive = (msg) => {
    const notification = JSON.parse(msg.body);

    if (currentChatId == notification.chatId) {
        showChat(currentChatId);
        let chatWindow = document.getElementById("chat-window");
        chatWindow.scrollTop = chatWindow.scrollHeight
    }
};

function send(chatId) {
    let input = $("#message-text");
    let content = input.val();
    input.val("");
    if (content !== "") {
        if (currentRecipientId === "") {
            sendMessageWithChatId(content, userId, chatId, username, chatId);
        } else {
            sendMessage(content, userId, currentRecipientId, username);
        }
    }
}

function getPosition(message) {
    let position = "yours";
    if (message.senderId === userId) {
        position = "mine";
    }
    return position;
}

function showChat(chatId, isGroup) {
    let chat = {};
    let chatName;
    currentChatId = chatId;
    let chatUrl = url;

    currentRecipientId = "";

    if (isGroup == "true") {
        chatUrl += userId + "/chat/group/" + chatId;
    } else {
        chatUrl += userId + "/chat/personal/" + chatId;
        currentRecipientId = $("#" + chatId).attr("data-recipientId");
    }

    $.ajax({
        type: 'GET',
        url: chatUrl,
        beforeSend: (xhr) => xhr.setRequestHeader(header, token),
        success: (data) => chat = data,
        cache: false,
        async: false
    });

    chatName = chat.chatName;

    let chatMessages = {};
    $.ajax({
        type: 'GET',
        url: url + "messages/" + chatId,
        beforeSend: (xhr) => xhr.setRequestHeader(header, token),
        success: (data) => chatMessages = data,
        cache: false,
        async: false
    });

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
    let sendIconBlock = $("<i class='fa fa-paper-plane' id='send-button' onclick='send(`" + chatId + "`);'></i>");

    inputMessageBlock.keyup(evt => {
        evt.preventDefault();
        if (evt.which === 13) {
            send(chatId);
        }
        return false;
    })

    inputMessageBlock.append(messageTextBlock);
    inputMessageBlock.append(sendIconBlock);

    chatWindow.after(inputMessageBlock);

    $("#chat-header").remove();

    let chatHeader = $("<span class='text-center' id='chat-header'>" + (typeof (chatName) == "undefined" ? "Nobody": chatName) + "</span>");

    chatWindow.before(chatHeader);
}

$(() => {
    const regexp = /(.+?)messenger\/?(.*)/;
    currentRecipientId = document.URL.match(regexp)[2];
    let found = false;
    let startChatId = "";

    for (let chat of chats) {
        let chatId = $(chat).attr("data-chatId");
        let isGroup = $(chat).attr("data-group");
        if (isGroup == "false") {
            if (!found) {
                startChatId = chatId
            }
            let recipientId = $(chat).attr("data-recipientId");
            if (recipientId == currentRecipientId) {
                found = true;
            }
        }
        $(chat).click(() => {
            showChat(chatId, isGroup);
        });
    }

    if (startChatId !== "") {
        showChat(startChatId);
    }

    $("#chats").outerHeight($("#chat-block").outerHeight);
});