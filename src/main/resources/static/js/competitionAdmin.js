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
    let grades = {};
    let url = document.URL.match(regexp)[0] + "/grade?";
    let winner = $("#winner").val();
    let second = $("#second").val();
    let third = $("#third").val();
    if (winner !== "") url += "winner=" + encodeURIComponent(winner) + "&";
    if (second !== "") url += "second=" + encodeURIComponent(second) + "&";
    if (third !== "") url += "third=" + encodeURIComponent(third);
    let spotted = $(".spotted");
    for (let s of spotted) {
        url += "&spotted=" + encodeURIComponent($(s).val());
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