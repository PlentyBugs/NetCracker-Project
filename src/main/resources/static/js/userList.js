$(() => {
    let userListContainer = $("#userList");
    let url = document.URL.match(/(https?:\/\/.+?\/)\/?.*/)[1];

    let teams = [];
    let id = $("#zzz").attr("value");
    $.ajax({
        type: 'GET',
        url: url + 'user/team/' + id,
        async: false,
        cache: false,
        success: (data) => teams = data
    });

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

                let card = $("<div class='brick bg-dark text-center' style='max-width: 15rem;'></div>");
                let image = $("<a href='/user/" + user.id + "'><img src='/img/" + fileName + "' class='card-img-top user-image-sm' /></a>");
                let bodyHeader = $("<h5 class='card-text m-3'>" + fullName + "</h5>");

                let writeButton = $("<button type='button' class='btn btn-primary btn-block write-button btn-card-group' data-recipientId='" + user.id + "'>Write</button>");
                let inviteButton = $("<button type='button' class='btn btn-warning btn-block btn-card-group'>Invite</button>");

                let teamBlock = $("<div class='teams-dropdown d-none btn-group-vertical'></div>");
                let filterTeamBlock = $("<input type='text' class='form-control' placeholder='User' autocomplete='off' autocapitalize='off' />");
                for (let team of teams) {
                    let t = $("<button type='button' class='btn btn-warning btn-block team-in-dropdown-" + user.id + "'>" + team.teamName + "</button>");
                    t.click(() => {
                        inviteUser(user.id, team.id, url);
                        t.text("Invited");
                        t.removeClass("btn-warning");
                        t.addClass("btn-success");
                        setTimeout(() => {
                            t.text(team.teamName);
                            t.removeClass("btn-success");
                            t.addClass("btn-warning");
                        }, 1500);
                    });
                    teamBlock.append(t);
                }
                filterTeamBlock.keyup(() => filter(filterTeamBlock.val(), $(".team-in-dropdown-" + user.id)));
                teamBlock.prepend(filterTeamBlock);

                writeButton.click(() => write(user.id));
                inviteButton.click(() => {
                    teamBlock.toggleClass("d-none");
                    teamBlock.toggleClass("d-block");
                });

                card.append(image);
                card.append(bodyHeader);
                card.append(inviteButton);
                card.append(teamBlock);
                card.append(writeButton);

                userListContainer.append(card);
            }
        }
    });

    let userFilter = $("#user-filter");
    userFilter.keyup(() => filter(userFilter.val(), $(".brick")));
});