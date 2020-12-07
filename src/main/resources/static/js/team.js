let teamId = $("#team-name-header").data("team-id");
let url = getUrl();
let inviteButton = $("#invite-button-modal");

function inviteUsers() {
    let checkboxes = $(".user-modal-checkbox-input");
    for (let checkbox of checkboxes) {
        if ($(checkbox).is(":checked")) {
            inviteUser($(checkbox).data('user-id'), teamId, url);
            playSuccessButtonAnimation(inviteButton, "Invite", "Invited");
        }
    }
}

function addTeamRoles() {
    let token = $('#_csrf').attr('content');
    let header = $('#_csrf_header').attr('content');
    let checkboxes = $(".team-role-checkbox");
    let button = $("#add-team-role-button");
    let teamId = button.data("team-id");
    let userId = button.data("user-id");
    let teamRoles = []
    for (let checkbox of checkboxes) {
        if ($(checkbox).is(":checked")) {
            teamRoles.push($(checkbox).data("team-role"));
        }
    }
    playSuccessButtonAnimation(button, "Add team roles");

    $.ajax({
        type: 'PUT',
        beforeSend: (xhr) => xhr.setRequestHeader(header, token),
        url: url + "team/" + teamId + "/role/" + userId,
        dataType: 'json',
        contentType: "application/json; charset=utf-8",
        data: JSON.stringify(teamRoles),
        async: true,
        cache: false
    });
}

$(() => {
    let teamLogo = $("#team-logo");
    teamLogo.ready(() => {
        $("#join-group-chat").outerWidth(teamLogo.outerWidth());
        $("#invite-button").outerWidth(teamLogo.outerWidth());
        $("#image-uploadable-overlay-logo").outerWidth(teamLogo.outerWidth());
        $("#image-uploadable-container-id").outerWidth(teamLogo.outerWidth());
    });
    let userListInput = $("#user-list-input");
    userListInput.keyup(() => filter(userListInput.val(), $(".user-modal")));
});