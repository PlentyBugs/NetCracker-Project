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

    var archList=getList(archiveComp);
    let runningList=getList(runningComp);
    let archive = $("#archive");
    let running = $("#running");
    let compArchive = $(
        "<div class='text-center'><ul class='list-group text-center'>" + archList + "</ul></div>" +
        "</div>" +
        "        </div>");
    let compRun = $(
        "<div class='text-center'><ul class='list-group text-center'>" + runningList + "</ul></div>" +
        "</div>" +
        "        </div>");
    archive.append(compArchive);
    running.append(compRun);
}

function getList(array){
    let name=[];
    let desc=[];
    var result=[];
    for(let i=0;i<array.length;i++){
        $.each(array[i],function (key,value){
            if (key=="compName"){
                name[i]=value;
            }
            if(key=="description"){
                desc[i]=value;
            }
        });
        result+='<div class="col-sm-8 align-self-center"><li class="list-group-item bg-dark">'+name[i]+'<br>'+desc[i]+'</li></div>';
    }
    return result;
}
$(() => getCompetitions())
