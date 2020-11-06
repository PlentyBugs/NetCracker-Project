$(() => {
    $('#loading-bg').hide();
    $('#loading-image').hide();

    $(window).on('beforeunload', function() {
        $('#loading-bg').show();
        $('#loading-image').show();
    });
});

function removeTeam(id) {
    $('#team-id-' + id).remove();
    let token = $('#_csrf').attr('content');
    let header = $('#_csrf_header').attr('content');
    $.ajax(
        {
            type: 'DELETE',
            url: document.URL + '/team/' + id,
            beforeSend: (xhr) => xhr.setRequestHeader(header, token),
            success: () => console.log("removed successfully"),
            cache: false,
            async: true
        }
    )
}