let toGroup = false;

function writeToCompetition() {
    if (toGroup) {
        writeToGroup($("#message-text").attr("data-chatId"));
    } else {
        writeToEveryone();
    }
}

$(() => {
    let everyoneButton = $("#send-everyone");
    let groupButton = $("#send-to-group");
    everyoneButton.addClass("active");
    everyoneButton.click(() => {
        if (toGroup) {
            everyoneButton.addClass("active");
            groupButton.removeClass("active");
            toGroup = false;
        }
    })
    groupButton.click(() => {
        if (!toGroup) {
            groupButton.addClass("active");
            everyoneButton.removeClass("active");
            toGroup = true;
        }
    })
});