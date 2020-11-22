function search() {
    let filter = {};
    filter["enableBeforeStart"] = $("#enableBeforeStart").prop('checked');
    filter["enableAfterStart"] = $("#enableAfterStart").prop('checked');
    filter["enableBeforeEnd"] = $("#enableBeforeEnd").prop('checked');
    filter["enableAfterEnd"] = $("#enableAfterEnd").prop('checked');
    filter["beforeStart"] = $("#beforeStart").val();
    filter["afterStart"] = $("#afterStart").val();
    filter["beforeEnd"] = $("#beforeEnd").val();
    filter["afterEnd"] = $("#afterEnd").val();
    filter["searchString"] = $("#searchString").val();
    const regexp = /(.+)\/competition/
    window.location.href = document.URL.match(regexp)[0] + "?"
        + "enableBeforeStart=" + filter["enableBeforeStart"] + "&"
        + "enableAfterStart=" + filter["enableAfterStart"] + "&"
        + "enableBeforeEnd=" + filter["enableBeforeEnd"] + "&"
        + "enableAfterEnd=" + filter["enableAfterEnd"] + "&"
        + "beforeStart=" + encodeURIComponent(filter["beforeStart"]) + "&"
        + "afterStart=" + encodeURIComponent(filter["afterStart"]) + "&"
        + "beforeEnd=" + encodeURIComponent(filter["beforeEnd"]) + "&"
        + "afterEnd=" + encodeURIComponent(filter["afterEnd"]) + "&"
        + "searchString=" + encodeURIComponent(filter["searchString"]);
}

$("body").keyup(evt => {
    evt.preventDefault();
    if (evt.which === 13) {
        search();
    }
    return false;
})