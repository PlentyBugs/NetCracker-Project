let teamId = $("#team-name-header").data("team-id");
let url = document.URL.match(/(https?:\/\/.+?\/)\/?.*/)[1];
let inviteButton = $("#invite-button-modal");

function inviteUsers() {
    let checkboxes = $(".user-modal-checkbox-input");
    for (let checkbox of checkboxes) {
        if ($(checkbox).is(":checked")) {
            inviteUser($(checkbox).data('user-id'), teamId, url);
            inviteButton.text("Invited");
            inviteButton.removeClass("btn-warning");
            inviteButton.addClass("btn-success");
            setTimeout(() => {
                inviteButton.text("Invite");
                inviteButton.removeClass("btn-success");
                inviteButton.addClass("btn-warning");
            }, 1500);
        }
    }
}

$(() => {
    let teamLogo = $("#team-logo");
    $("#join-group-chat").outerWidth(teamLogo.outerWidth());
    $("#invite-button").outerWidth(teamLogo.outerWidth());
    $("#image-uploadable-overlay-logo").outerWidth(teamLogo.outerWidth());
    $("#image-uploadable-container-id").outerWidth(teamLogo.outerWidth());
    let userListInput = $("#user-list-input");
    userListInput.keyup(() => filter(userListInput.val(), $(".user-modal")));
});