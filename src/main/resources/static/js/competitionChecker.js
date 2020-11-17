let nnn = $("#nnn").attr("value");
let mmm = $("#mmm").attr("value");
let team = $("#team");
let teams = $.get("/user/team/" + mmm, (data) => teams = data);

function check() {
    let id = team.val();
    let flag = false;
    for (let t of Object.entries(teams)) {
        if (t.id == id && typeof (t.teammatesId) != "undefined") {
            for (let i of t.teammatesId) {
                if (i == nnn) {
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