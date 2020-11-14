let stompClient = null;

function connect() {
    let socket = new SockJS("/ws");
    let userId = $("#zzz").attr("value");
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        console.log("connected");
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