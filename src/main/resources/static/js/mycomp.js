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
    let result = [];
    for (let i = 0; i < array.length; i++) {
        $.each(array[i], function (key, value) {
            if (key == "compName") name[i] = value;
            if (key == "description") desc[i] = value;
            if (key == "titleFilename") titleFile[i] = value;
            console.log(titleFile[i]);
        });
        result += "<li style='list-style-type: none'><div class='container w-75 h-75'>" +
            "<img src='/img/" + titleFile[i] + "'class='card-img-top'/>" +
            "<div class='card-body mb-3 bg-dark'>" +
            "<h5 class='card-title' style='font-style: italic'>" + name[i] + "</h5>" +
            "<p class='card-text'>" + desc[i] + "</p>"+
            "</div></div></li>";
    }
    return result;
}

$(() => getCompetitions())


