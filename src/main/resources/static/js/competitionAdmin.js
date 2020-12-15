let buttonAddSponsor = $("#newSpotted");
let c = 0;

buttonAddSponsor.click(() => {
    let newSpotted = $("<div class='input-group d-inline-flex col-sm-12 m-1' id='spotted-" + c + "'>" +
        "                <div class='input-group-prepend'>" +
        "                    <div class=\"input-group-text group-prepend\" class='prepend-text'>" +
        "                        Spotted:" +
        "                    </div>" +
        "                </div>" +
        "                <input type='text' class='form-control spotted' placeholder='Spotted' name='spotted' list='teamList'/>" +
        "                <button class='btn btn-danger' onclick='remove(" + c++ + ")'>Remove</button>" +
        "            </div>");
    buttonAddSponsor.after(newSpotted);
})

function remove(id) {
    $("#spotted-" + id).remove();
}

function grade() {
    let token = $('#_csrf').attr('content');
    let header = $('#_csrf_header').attr('content');
    const regexp = /(.+)\/competition\/\d+/;
    let url = document.URL.match(regexp)[0] + "/grade?";
    let winner = $("#winner").val().replace(" ", "---___---");
    let second = $("#second").val().replace(" ", "---___---");
    let third = $("#third").val().replace(" ", "---___---");
    if (winner !== "") {
        winner = $("#team-name-" + winner).data("team-id");
        url += "winner=" + encodeURIComponent(winner) + "&";
    }
    if (second !== "") {
        second = $("#team-name-" + second).data("team-id");
        url += "second=" + encodeURIComponent(second) + "&";
    }
    if (third !== "") {
        third = $("#team-name-" + third).data("team-id");
        url += "third=" + encodeURIComponent(third);
    }
    let spotted = $(".spotted");
    for (let s of spotted) {
        let x = $("#team-name-" + $(s).val().replace(" ", "---___---")).data("team-id");
        url += "&spotted=" + encodeURIComponent(x);
    }
    $.ajax({
        type: "PUT",
        url: url,
        beforeSend: (xhr) => xhr.setRequestHeader(header, token),
        cache: false,
        async: true
    });

    let btnGrade = $("#btn-grade");
    playSuccessButtonAnimation(btnGrade, "Grade", "Successfully graded");
}