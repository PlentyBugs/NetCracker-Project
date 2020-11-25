let themeSetJQ = $('#themeSet');

Array.prototype.remove = function() {
    let what, a = arguments, L = a.length, ax;
    while (L && this.length) {
        what = a[--L];
        while ((ax = this.indexOf(what)) !== -1) {
            this.splice(ax, 1);
        }
    }
    return this;
};

themeSetJQ.bind('input', function () {
    let option = themeSetJQ.val();
    if(checkExists(option) === true){
        let op = $("#id-" + option.toUpperCase().replaceAll(" ", "_"));
        addTheme($(op).data('theme'), $(op).data('theme-id'));
        themeSetJQ.val("");
    }
});

function checkExists(inputValue) {
    let flag;
    let themesDataList = document.querySelector('#themesDatalist');
    for (let i = 0; i < themesDataList.options.length; i++) {
        if(inputValue === themesDataList.options[i].value){
            flag = true;
        }
    }
    return flag;
}

let themes = $("#themes");
let themeList = [];

function addTheme(theme, id) {
    if (!themeList.includes(theme)) {
        themes.append($("<button class='btn btn-warning m-1' type='button' id='" + id + "' onclick='removeTheme(`" + theme + "`, `" + id + "`);'>" + theme + "<input name='theme' type='hidden' value='" + id + "'/></button>"));
        themeList.push(theme);
    }
}

function removeTheme(theme, id) {
    themeList.remove(theme);
    $("#" + id).remove();
}

$(() => setDataChoice('themeSet', 'themesDatalist', 'themesTemplate'));