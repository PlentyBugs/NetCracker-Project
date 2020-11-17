let userId = $("#zzz").attr("value");
let writeButtons = $(".write-button");

for (let button of writeButtons) {
    $(button).click(() => {
        write($(button).attr("data-recipientId"));
    });
}

function write(recipientId) {
    const regexp = /(.+?\/).*/;
    let token = $('#_csrf').attr('content');
    let header = $('#_csrf_header').attr('content');
    let url = document.URL.match(regexp)[1] + "messenger/";
    $.ajax({
        type: 'POST',
        url: url + userId + "/chat/" + recipientId,
        beforeSend: (xhr) => xhr.setRequestHeader(header, token),
        cache: false,
        async: false
    });
    window.location.href = url + recipientId;
}