$(() => {
    $.ajax({
        type: 'GET',
        url: document.URL.match(/(https?:\/\/.+?\/)\/?.*/)[1] + "user/simple",
        async: false,
        cache: false,
        success: (userList) => {
            let containers = $(".user-list-container");
            for (let container of containers) {
                let containerUserId = $(container).data("input-id-suffix");
                let containerUserClass = $(container).data("input-class");
                for (let user of userList) {
                    let id = user.id + containerUserId;
                    let userId = user.id;
                    let fullName = user.surname + " " + user.name + " (" + user.username + ")";
                    $(container).append($("" +
                        "<div class='row m-2 user-modal' style='display: flow-root'>" +
                        "    <label for='" + id + "' data-id='" + userId + "' class='user-modal-checkbox'>" +
                        "        <span>" + fullName + "</span>" +
                        "        <input type='checkbox' id='" + id + "' data-user-id='" + userId + "' class='user-modal-checkbox " + containerUserClass + "' />" +
                        "        <span class='user-modal-checkbox-mark'></span>" +
                        "    </label>" +
                        "</div>")
                    );
                }
            }
        }
    });
})