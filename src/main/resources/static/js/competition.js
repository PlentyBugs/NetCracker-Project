let current = "everyone";

function writeToCompetition() {
    if (current === "group") {
        writeToGroup($("#message-text").attr("data-chatId"));
    } else if (current === "everyone") {
        writeToEveryone();
    } else if (current === "teams") {
        let teamsDivs = $(".team-gci");
        let teams = [];
        for (let team of teamsDivs) {
            teams.push($(team).attr("data-gci"));
        }
        writeToEachTeam(teams);
    }
}

$(() => {
    let everyoneButton = $("#send-everyone");
    let groupButton = $("#send-to-group");
    let teamButton = $("#send-to-each-team");
    everyoneButton.addClass("active");
    everyoneButton.click(() => {
        if (current !== "everyone") {
            everyoneButton.addClass("active");
            groupButton.removeClass("active");
            teamButton.removeClass("active");
            current = "everyone";
        }
    });
    groupButton.click(() => {
        if (current !== "group") {
            groupButton.addClass("active");
            everyoneButton.removeClass("active");
            teamButton.removeClass("active");
            current = "group";
        }
    });
    teamButton.click(() => {
        if (current !== "teams") {
            teamButton.addClass("active");
            groupButton.removeClass("active");
            everyoneButton.removeClass("active");
            current = "teams";
        }
    });
});