let token = $('#_csrf').attr('content');
let header = $('#_csrf_header').attr('content');
let currentChatId = "";
let chatSearch = $("#search-chat");
let chats = $(".chat-in-menu");
let currentRecipientId = "";
let currentIsGroup = false;
const userId = $("#zzz").attr("value");
const username = $("#mmm").attr("value");
const regexp = /(.+?)messenger(.*)/;
const url = document.URL.match(regexp)[1] + "messenger/";
const userList = $(".user-modal");
const addUserList = $(".user-modal-add-new-user");
const userListInput = $("#user-list-input");
const addUserListInput = $("#user-list-input-add-new-user");
const chatList = $("#chat-list");

chatSearch.keyup(() => filter(chatSearch.val(), chats));

userListInput.keyup(() => filter(userListInput.val(), userList));

addUserListInput.keyup(() => filter(addUserListInput.val(), addUserList));

function processCheckboxes(checkboxes) {
    let userIds = [];
    for (let checkbox of checkboxes) {
        if ($(checkbox).is(':checked')) {
            userIds.push($(checkbox).data("user-id"));
        }
    }
    return userIds;
}

const messageReceive = (msg) => {
    const notification = JSON.parse(msg.body);

    if (currentChatId == notification.chatId) {
        printMessages(currentChatId);
        let chatWindow = document.getElementById("chat-window");
        chatWindow.scrollTop = chatWindow.scrollHeight;
    }
    $("#message-text").focus();
};

function createGroupChat() {
    let chatName = $("#group-chat-name");
    let checkboxes = $(".user-modal-checkbox-input");
    let userIds = processCheckboxes(checkboxes);
    $.ajax({
        type: 'POST',
        url: url + userId + "/chat/group?chatName=" + JSON.stringify(chatName.val()),
        beforeSend: (xhr) => xhr.setRequestHeader(header, token),
        dataType: 'json',
        contentType: "application/json; charset=utf-8",
        data: JSON.stringify(userIds),
        cache: false,
        async: false
    });

    chatName.empty();
    userListInput.val('');
    filter(userListInput.val(), userList);
    userList.each((user) => $(user).css("display", "block"));
    for (let checkbox of checkboxes) {
        $(checkbox).prop('checked', false);
    }
}

function addUsersToGroupChat() {
    let checkboxes = $(".user-modal-checkbox-input-add-new-user");
    let userIds = processCheckboxes(checkboxes);
    $.ajax({
        type: 'PUT',
        url: url + userId + "/chat/group/" + currentChatId + "/participant",
        beforeSend: (xhr) => xhr.setRequestHeader(header, token),
        dataType: 'json',
        contentType: "application/json; charset=utf-8",
        data: JSON.stringify(userIds),
        cache: false,
        async: false
    });

    addUserListInput.val('');
    filter(addUserListInput.val(), userList);
    addUserList.each((user) => $(user).css("display", "block"));
    for (let checkbox of checkboxes) {
        $(checkbox).prop('checked', false);
    }
    showChat(currentChatId, 'true');
}

const updateChat = (chat) => {
    const notification = JSON.parse(chat.body);

    if (notification.status == "ADD") {
        let newChat = $("<div class='chat-in-menu text-center m-1' id='" + notification.chatId + "' data-recipientId='" + notification.recipientId + "' data-chatId='" + notification.chatId + "' data-group='" + notification.group + "'>" + notification.chatName + "</div>");
        newChat.click(() => showChat(notification.chatId, notification.group ? 'true' : 'false'));
        chatList.prepend(newChat);
    } else if (notification.status == "REMOVE") {
        $("#" + notification.chatId).remove();
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

function write(recipientId) {
    $.ajax({
        type: 'POST',
        url: url + userId + "/chat/personal/" + recipientId,
        beforeSend: (xhr) => xhr.setRequestHeader(header, token),
        cache: false,
        async: false
    });
    window.location.href = url + recipientId;
}

function kick(chatId, recipientId) {
    $.ajax({
        type: 'POST',
        url: url + userId + "/chat/group/" + chatId + "/kick/" + recipientId,
        beforeSend: (xhr) => xhr.setRequestHeader(header, token),
        cache: false,
        async: false
    });
}

function getPosition(message) {
    let position = "yours";
    if (message.senderId === userId) {
        position = "mine";
    }
    return position;
}

function printMessages(chatId) {
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

        messages.append($("<div class='message last'>" + message.content + "<div class='message-author'>By " + message.senderName + "</div></div>"));

        chatWindow.append(messages);
    }
    let chatWindow2 = document.getElementById("chat-window");
    chatWindow2.scrollTop = chatWindow2.scrollHeight;

    return chatWindow;
}

function showChat(chatId, isGroup) {
    let chat = {};
    let chatName;
    let chatUrl = url;
    let isAdmin = false;
    currentChatId = chatId;
    currentIsGroup = isGroup;

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

    if (isGroup == "true") {
        isAdmin = chat.adminId == userId;
    }

    let chatWindow = printMessages(chatId);

    $("#input-message").remove();

    let inputMessageBlock = $("<div id='input-message'></div>");
    let messageTextBlock = $("<textarea placeholder='Message...' name='message' id='message-text' autofocus></textarea>");
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

    let chatHeader = $("<div class='text-center' id='chat-header'></div>");
    let chatNameHeader = $("<span>" + (typeof (chatName) == "undefined" ? "Nobody": chatName) + "</span>");
    let chatMenuHeader = $("<i class='fa fa-bars float-right' id='messenger-menu-button'></i>");
    $("#messenger-participants-parent").remove();
    let participantsMenuBlock = $("<div class='w-100' style='display: none; height: 0' id='messenger-participants-parent'></div>");
    let participantsMenu = $("<div class='p-2 d-flex flex-column' id='messenger-participants'></div>");

    let chatSubHeader = $("<h5 class='text-center mb-3'></h5>");
    let chatSubHeaderSpan = $("<span>Participants</span>");
    if (isGroup == 'true') {
        let chatSubHeaderAddUserPlus = $("<i class='fa fa-plus float-right add-plus' data-toggle='modal' data-target='#add-new-user-modal'></i>");
        chatSubHeaderSpan.append(chatSubHeaderAddUserPlus);
        chatSubHeaderSpan.css("margin-right", "-1.4em");
    }
    chatSubHeader.append(chatSubHeaderSpan);

    participantsMenu.append(chatSubHeader);

    let participantsBlock = $("<div class='container'></div>");

    let participantsURL = "";

    if (isGroup == "true") {
        participantsURL = url + "users/group/" + chatId;
    } else {
        participantsURL = url + "users/personal/" + chatId;
    }

    $.ajax({
        type: "GET",
        url: participantsURL,
        beforeSend: (xhr) => xhr.setRequestHeader(header, token),
        success: (participants) => {
            for (let user of participants) {
                let row = $("<div class='row'></div>");
                let username = $("<span>" + user.surname + " " + user.name + " (" + user.username + ")</span>");
                row.append(username);
                let buttonGroup = $("<div class='btn-group' id='participants-admin-button-group' role='group'></div>");
                let writeButton = $("<button type='button' class='btn btn-primary'>Write</button>");
                writeButton.click(() => write(user.id));
                buttonGroup.append(writeButton);
                if (isAdmin) {
                    let kickButton = $("<button type='button' class='btn btn-danger'>Kick</button>");
                    kickButton.click(() => {
                        kick(chatId, user.id);
                        row.remove();
                    });
                    buttonGroup.append(kickButton);
                }
                row.append(buttonGroup);
                participantsBlock.append(row);
            }
        },
        cache: false,
        async: false
    });

    participantsMenu.append(participantsBlock);

    if (isGroup == "true") {
        let leaveChatButton = $("<button class='btn btn-danger btn-block mt-auto'>Leave</button>");
        leaveChatButton.click(() => $.ajax({
            type: 'DELETE',
            url: getUrl() + "messenger/" + chat.adminId + "/chat/group/" + chatId + "/participant/" + userId,
            beforeSend: (xhr) => xhr.setRequestHeader(header, token),
            success: () => {
                $("#chat-header").empty();
                $("#chat-window ").empty();
                $("#input-message").empty();
                $("#messenger-participants-parent").empty();
            },
            cache: false,
            async: false
        }));
        participantsMenu.append(leaveChatButton);
    }

    participantsMenuBlock.append(participantsMenu);

    chatMenuHeader.click(() => {
        participantsMenuBlock.toggle();
    })

    chatHeader.append(chatMenuHeader);
    chatHeader.append(chatNameHeader);

    chatWindow.before(chatHeader);
    chatHeader.after(participantsMenuBlock);
}

$(() => {
    const regexp = /(.+?)messenger\/?(.*)/;
    currentRecipientId = document.URL.match(regexp)[2];
    let found = false;
    let startChatId = "";
    let startIsGroup = false;

    for (let chat of chats) {
        let chatId = $(chat).attr("data-chatId");
        let isGroup = $(chat).attr("data-group");
        if (!found) {
            startChatId = chatId
            startIsGroup = isGroup;
        }
        if (isGroup == "false") {
            let recipientId = $(chat).attr("data-recipientId");
            if (recipientId == currentRecipientId) {
                found = true;
            }
        } else {
            if (currentRecipientId == chatId) {
                found = true;
            }
        }
        $(chat).click(() => {
            showChat(chatId, isGroup);
        });
    }

    if (startChatId !== "") {
        showChat(startChatId, startIsGroup);
    }
    let $chats = $("#chats");
    let $chat = $("#chat-block");
    $chats.outerHeight($chat.outerHeight())
    $(window).resize(() => $chats.outerHeight($chat.outerHeight()));

    let addGroupClassButton = $("#add-group-class-button");
    let chatSearchHeight = chatSearch.outerHeight();
    addGroupClassButton.outerWidth(chatSearchHeight);
    addGroupClassButton.outerHeight(chatSearchHeight);
    addGroupClassButton.css('line-height', chatSearchHeight + 'px');

    $("#messenger-navbar").removeClass("messenger-notification");
    localStorage.setItem("notification", "false");
});