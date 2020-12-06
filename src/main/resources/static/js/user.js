let token = $('#_csrf').attr('content');
let header = $('#_csrf_header').attr('content');
let updateRolesButton = $("#update-roles-button");
let roleInput = $("#role-input");
let rolesInRow = $(".role-in-row");
let roles = $("#current-team-roles");
let roleList = [];
let roleListNames = [];

roleInput.keyup(() => filter(roleInput.val(), rolesInRow));

function addRole(role, id) {
    if (!roleList.includes(id)) {
        roles.append($("<button class='btn btn-warning m-1' type='button' id='" + id + "' onclick='removeRole(`" + role + "`, `" + id + "`);'>" + role + "<input name='role' type='hidden' value='" + id + "'/></button>"));
        roleList.push(id);
        roleListNames.push(role);
    }
}

function removeRole(role, id) {
    roleList.remove(id);
    roleListNames.remove(role);
    $("#" + id).remove();
}

function saveUserRoles() {
    $.ajax({
        type: 'PUT',
        url: document.URL.match(/.+\d+/)[0] + "/roles",
        beforeSend: (xhr) => xhr.setRequestHeader(header, token),
        dataType: 'json',
        contentType: "application/json; charset=utf-8",
        data: JSON.stringify(roleList),
        cache: false,
        async: false
    });
    updateRolesButton.removeClass("btn-warning");
    updateRolesButton.addClass("btn-success");
    updateRolesButton.text("Successfully updated");
    setTimeout(() => {
        updateRolesButton.removeClass("btn-success");
        updateRolesButton.addClass("btn-warning");
        updateRolesButton.text("Save changes");
    }, 1000);

    let roleHeader = $("#team-roles-header");
    let currentTeamRoles = $(".current-team-role");
    for (let role of currentTeamRoles) {
        role.remove();
    }
    for (let i = 0; i < roleList.length; i++) {
        let name = roleListNames[i];
        roleHeader.after("<i class='current-team-role mr-1' data-role='" + name + "' data-role-id='" + roleList[i] + "'>" + name.charAt(0).toUpperCase() + name.slice(1) + "</i>");
    }
}

$(() => {
    for (let role of rolesInRow) {
        $(role).click(() => {
            addRole($(role).data("role"), $(role).data("role-id"));
        });
    }
    let currentTeamRoles = $(".current-team-role");
    for (let role of currentTeamRoles) {
        addRole($(role).data("role"), $(role).data("role-id"));
    }

    let userAvatar = $("#user-avatar");
    userAvatar.ready(() => {
        let writeButtonUser = $("#write-button-user");
        let imageOverlay = $("#image-uploadable-overlay-avatar");
        writeButtonUser.outerWidth(userAvatar.outerWidth());
        imageOverlay.outerWidth(userAvatar.outerWidth());
        imageOverlay.outerHeight(userAvatar.outerHeight());
        $("#image-uploadable-container-id").outerWidth(userAvatar.outerWidth());
        let inviteButton = $("#invite-user-button");
        inviteButton.outerWidth(userAvatar.outerWidth());
        let userId = inviteButton.data("user-id");
        let teams = loadTeamsByUserId($("#zzz").attr("value"));
        let teamBlock = $(buildInviteButton(teams, userId, inviteButton));
        inviteButton.after(teamBlock);
    });
});