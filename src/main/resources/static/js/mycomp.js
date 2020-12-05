function getCompetitions() {
    const regexp = /(.+?)mycomp(.*)/;
    const url = document.URL.match(regexp)[1] + "mycomp";
    let runningComp = {};
    $.ajax({
        type: 'GET',
        url: url + "/running",
        success: (data) => runningComp = data,
        cache: false,
        async: false
    });
    let archiveComp = {};
    $.ajax({
        type: 'GET',
        url: url + "/archive",
        success: (data) => archiveComp = data,
        cache: false,
        async: false
    });

    var archList = getList(archiveComp);
    let runningList = getList(runningComp);
    let archive = $("#archive");
    let running = $("#running");
    let compArchive = $(
        "<div><ul class='list-group' id='archList'>" + archList + "</ul></div>" +
        "</div>" +
        "        </div>");
    let compRun = $(
        "<div><ul class='list-group' id='runningList'>" + runningList + "</ul></div>" +
        "</div>" +
        "        </div>");
    archive.append(compArchive);
    running.append(compRun);
}

function getList(array) {
    let name = [];
    let desc = [];
    let titleFile = [];
    var result = [];
    for (let i = 0; i < array.length; i++) {
        $.each(array[i], function (key, value) {
            if (key == "compName") name[i] = value;
            if (key == "description") desc[i] = value;
            if (key == "titleFilename") titleFile[i] = value;
        });
        result += "<div class='container'>" +
            "<div class='card-body bg-dark'>" +
            "<h4 class='card-title'>" + name[i] + "</h4>" +
            "<p class='card-text'>" + desc[i] + "</p>" +
            "</div></div>";
    }
    return result;
}

$(() => getCompetitions())