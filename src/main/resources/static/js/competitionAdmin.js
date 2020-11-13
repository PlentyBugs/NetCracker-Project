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
    url += "winner=" + encodeURIComponent($("#winner").val()) + "&";
    url += "second=" + encodeURIComponent($("#second").val()) + "&";
    url += "third=" + encodeURIComponent($("#third").val());
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
    // window.location.href = document.URL.match(regexp)[0];
}