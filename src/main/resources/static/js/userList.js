$(() => {
    let userListContainer = $("#userList");

    let id = $("#zzz").attr("value");
    let teams = loadTeamsByUserId(id);

    $.ajax({
        type: 'GET',
        url: document.URL.match(/(https?:\/\/.+?\/)\/?.*/)[1] + "user/simple",
        async: false,
        cache: false,
        success: (userList) => {
            for (let user of userList) {
                let fullName = user.surname + " " + user.name + " (" + user.username + ")";
                let fileName = user.avatarFilename;
                if (fileName === "" || typeof (fileName) === "undefined") {
                    fileName = "default.png";
                }

                let card = $("<div class='brick bg-dark text-center d-flex flex-column' style='max-width: 15rem;'></div>");
                let image = $("<a href='/user/" + user.id + "'><img src='/img/" + fileName + "' class='card-img-top user-image-sm' onerror='this.error = null; this.src=`/img/default.png`'/></a>");
                let bodyHeader = $("<h5 class='card-text m-3'>" + fullName + "</h5>");

                let writeButton = $("<button type='button' class='btn btn-primary btn-block write-button btn-card-group mt-auto' data-recipientId='" + user.id + "'>Write</button>");


                writeButton.click(() => write(user.id));

                card.append(image);
                card.append(bodyHeader);
                if (user.id != id) {
                    writeButton.removeClass("mt-auto");
                    let inviteButton = $("<button type='button' class='btn btn-warning btn-block btn-card-group mt-auto'>Invite</button>");
                    let teamBlock = $(buildInviteButton(teams, user.id, inviteButton));
                    card.append(inviteButton);
                    card.append(teamBlock);
                }
                card.append(writeButton);

                userListContainer.append(card);
            }
        }
    });

    let userFilter = $("#user-filter");
    userFilter.keyup(() => filter(userFilter.val(), $(".brick")));
});