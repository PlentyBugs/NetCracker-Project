const messengerNavbar = $("#messenger-navbar");
const userId = $("#zzz").attr("value");

function messageReceive() {addClass();}

function updateChat() {addClass();}

function addClass() {
    messengerNavbar.addClass("messenger-notification");
    localStorage.setItem("notification", "true");
}

$(() => {
    if (localStorage.getItem("notification") === "true") {
        messengerNavbar.addClass("messenger-notification");
    } else {
        messengerNavbar.removeClass("messenger-notification");
    }
})