function search() {
    let filter = {};
    filter["alreadyInTheGroup"] = $("#alreadyInTheGroup").prop('checked');
    filter["removeEmpty"] = $("#removeEmpty").prop('checked');
    filter["minMembersOn"] = $("#minMembersOn").prop('checked');
    filter["maxMembersOn"] = $("#maxMembersOn").prop('checked');
    filter["minMembers"] = $("#minMembers").val();
    filter["maxMembers"] = $("#maxMembers").val();
    filter["searchName"] = $("#searchName").val();
    const regexp = /(.+)\/team/
    window.location.href = document.URL.match(regexp)[0] + "?"
        + "alreadyInTheGroup=" + filter["alreadyInTheGroup"] + "&"
        + "removeEmpty=" + filter["removeEmpty"] + "&"
        + "minMembersOn=" + filter["minMembersOn"] + "&"
        + "maxMembersOn=" + filter["maxMembersOn"] + "&"
        + "minMembers=" + filter["minMembers"] + "&"
        + "maxMembers=" + filter["maxMembers"] + "&"
        + "searchName=" + encodeURIComponent(filter["searchName"]);
}

$("body").keyup(evt => {
    evt.preventDefault();
    if (evt.which === 13) {
        search();
    }
    return false;
})