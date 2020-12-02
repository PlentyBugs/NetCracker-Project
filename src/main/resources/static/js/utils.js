function filter(filter, items) {
    for (let item of items) {
        if ($(item).text().toLowerCase().includes(filter.toLowerCase())) {
            $(item).css("display", "block");
        } else {
            $(item).css("display", "none");
        }
    }
}

function getUrl() {
    return document.URL.match(/(https?:\/\/.+?\/)\/?.*/)[1];
}

function inviteUser(userId, teamId, url) {
    let token = $('#_csrf').attr('content');
    let header = $('#_csrf_header').attr('content');
    $.ajax({
        type: 'PUT',
        url: url + "team/" + teamId + "/invite/" + userId,
        beforeSend: (xhr) => xhr.setRequestHeader(header, token),
        async: true,
        cache: false
    });
}

function buildInviteButton(teams, userId, inviteButton) {
    let url = getUrl();
    let teamBlock = $("<div class='teams-dropdown d-none btn-group-vertical'></div>");
    let filterTeamBlock = $("<input type='text' class='form-control' placeholder='User' autocomplete='off' autocapitalize='off' />");
    for (let team of teams) {
        let t = $("<button type='button' class='btn btn-warning btn-block team-in-dropdown-" + userId + "'>" + team.teamName + "</button>");
        t.click(() => {
            inviteUser(userId, team.id, url);
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
    filterTeamBlock.keyup(() => filter(filterTeamBlock.val(), $(".team-in-dropdown-" + userId)));
    teamBlock.prepend(filterTeamBlock);

    $(inviteButton).click(() => {
        teamBlock.toggleClass("d-none");
        teamBlock.toggleClass("d-block");
    });

    return teamBlock;
}

function loadTeamsByUserId(userId) {
    let teams = [];
    $.ajax({
        type: 'GET',
        url: getUrl() + 'user/team/' + userId,
        async: false,
        cache: false,
        success: (data) => teams = data
    });

    return teams;
}

Array.prototype.remove = function() {
    let what, a = arguments, L = a.length, ax;
    while (L && this.length) {
        what = a[--L];
        while ((ax = this.indexOf(what)) !== -1) {
            this.splice(ax, 1);
        }
    }
    return this;
};