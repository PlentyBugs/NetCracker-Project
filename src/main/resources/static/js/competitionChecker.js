let organizerId = $("#nnn").attr("value");
let usrId = $("#zzz").attr("value");
let team = $("#team");
let teams = $.get("/user/team/" + usrId, (data) => teams = data);

function check() {
    let id = team.val();
    let flag = false;
    for (let t of Object.entries(teams)) {
        if (t.id == id && typeof (t.teammatesId) != "undefined") {
            for (let i of t.teammatesId) {
                if (i == organizerId) {
                    flag = true;
                    break;
                }
            }
        }
    }
    if (flag) {
        $("#org-danger-message").css("display", "none");
    } else {
        $("#org-danger-message").css("display", "block");
    }
}

team.change(() => check());

check();