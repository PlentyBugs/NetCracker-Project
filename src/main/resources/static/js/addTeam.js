let userIds = [];
let invitedUsersContainer = $("#invited-users-container");

function addUser(id) {
    if (!userIds.includes(id)) {
        invitedUsersContainer.append($("<input class='d-none' id='user-invited-" + id + "' name='invited-user' type='hidden' value='" + id + "'/>"));
        userIds.push(id);
    }
}

function removeUser(id) {
    userIds.remove(id);
    $("#user-invited-" + id).remove();
}

$(() => {
    let userFilter = $("#user-list-input");
    userFilter.keyup(() => filter(userFilter.val(), $(".user-modal")));

    let checkboxes = $(".user-modal-checkbox");
    for (let checkbox of checkboxes) {
        let check = $(checkbox);
        check.change(() => {
            if (check.is(':checked')) {
                addUser(check.data("user-id"));
            } else {
                removeUser(check.data("user-id"));
            }
        });
    }
});