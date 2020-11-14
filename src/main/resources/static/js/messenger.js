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

function showChat(recipientId) {
    let chat = {};
    const regexp = /(.+?)messenger(.*)/;
    const url = document.URL.match(regexp)[1] + "messenger/" + senderId + "/chat/" + recipientId;
    $.ajax(
        {
            type: 'GET',
            url: url,
            beforeSend: (xhr) => xhr.setRequestHeader(header, token),
            success: (data) => chat = data,
            cache: false,
            async: false
        }
    )
    console.log(chat);
}

$(() => connect());