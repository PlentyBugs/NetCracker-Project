let $currentPopover = null;
$(document).on('shown.bs.popover', function (ev) {
    let $target = $(ev.target);
    if ($currentPopover && ($currentPopover.get(0) !== $target.get(0))) {
        $currentPopover.popover('toggle');
    }
    $currentPopover = $target;
}).on('hidden.bs.popover', function (ev) {
    let $target = $(ev.target);
    if ($currentPopover && ($currentPopover.get(0) === $target.get(0))) {
        $currentPopover = null;
    }
});

$.extend(Date.prototype, {
    toDateCssClass:  function () {
        return '_' + this.getFullYear() + '_' + (this.getMonth() + 1) + '_' + this.getDate();
    }
});

function addCompetition(competition) {
    let $event = $('<div/>', {'class': 'competition', text: competition.compName, title: competition.compName, 'data-index': competition.id}),
        e = new Date(competition.startDate),
        dateClass = e.toDateCssClass(),
        day = $('.' + e.toDateCssClass()),
        empty = $('<div/>', {'class':'clear competition', html:' '}),
        numbCompetitions = 0,
        endDay = competition.endDate && $('.' + competition.endDate.toDateCssClass()).length > 0,
        checkAnyway = new Date(e.getFullYear(), e.getMonth(), e.getDate() + 40),
        existing,
        i;
    if (!competition.endDate) {
        $event.addClass('begin end');
        $('.' + competition.startDate.toDateCssClass()).append($event);
        return;
    }

    while (e <= competition.endDate && (day.length || endDay || date < checkAnyway)) {
        if(day.length) {
            existing = day.find('.competition').length;
            numbCompetitions = Math.max(numbCompetitions, existing);
            for(i = 0; i < numbCompetitions - existing; i++) {
                day.append(empty.clone());
            }
            let link = $("<a href='/competition/" + competition.id + "'></a>");
            link.append($event.
                toggleClass('begin', dateClass === competition.startDate.toDateCssClass()).
                toggleClass('end', dateClass === competition.endDate.toDateCssClass())
            );
            day.append(
                link
            );
            $event = $event.clone();
            $event.html(' ');
        }
        e.setDate(e.getDate() + 1);
        dateClass = e.toDateCssClass();
        day = $('.' + dateClass);
    }
}

let days = ["Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"];
let months = ["January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"];
let date = (new Date());

let dayToday = date.toDateCssClass();
const regexp = /(.+?)calendar(.+)/;
const url = "/competition/calendar" + document.URL.match(regexp)[2];

function draw() {
    $.get(url, (data) => {
        for (let comp of data) {
            comp.startDate = new Date(comp.startDate);
            comp.endDate = new Date(comp.endDate);
            addCompetition(comp);
        }
    });

    let dateToday = date || new Date(),
        month = date.getMonth(),
        year = date.getFullYear(),
        first = new Date(year, month, 1),
        last = new Date(year, month + 1, 0),
        startingDay = first.getDay(),
        thedate = new Date(year, month, 1 - startingDay),
        today = new Date();

    let monthYear = $("#monthYear");
    monthYear.empty();
    monthYear.append("<i class='fa fa-chevron-circle-left' id='prevMonth'></i> " + months[date.getMonth()] + " " + date.getFullYear() + " <i class='fa fa-chevron-circle-right' id='nextMonth'></i>")

    let weeksHead = $("#weeksHead");
    weeksHead.empty();
    for (let i = 0; i < 7; i++) {
        let dayInHead = $("<th class='c-name'>" + days[i] + "</th>");
        weeksHead.append(dayInHead);
    }

    let daysTable = $("#days");
    daysTable.empty();

    for (let j = 0; j < 5; j++) {
        let rowOfDays = $("<tr id='rowOfDays'></tr>");
        for (let i = 0; i < 7; i++) {
            let dayClass;
            if (thedate > last) {
                dayClass = "outside";
            } else if (thedate >= first) {
                dayClass = "current";
            }
            let day = $(
                "<td class=\"calendar-day " + dayClass + " " + thedate.toDateCssClass() + " " + (dayToday === thedate.toDateCssClass() ? 'selected':'') + " js-cal-option\" data-date='" + thedate.toISOString() + "'>" +
                "<div class='date'>" + thedate.getDate() + "</div></td>"
            );
            thedate.setDate(thedate.getDate() + 1);
            rowOfDays.append(day);
        }
        daysTable.append(rowOfDays);
    }
    daysTable.append($("<tr id='rowOfDays'><td></td><td></td><td></td><td></td><td></td><td></td><td></td></tr>"));

    $("#prevMonth").click(() => {
        date.setMonth(date.getMonth() - 1);
        draw();
    });
    $("#nextMonth").click(() => {
        date.setMonth(date.getMonth() + 1);
        draw();
    });
}

let whoseCalendar = $("#whoseCalendar");
if (url.includes("user")) {
    const regex = /.+\/calendar\/user\/(.+)/
    let id = document.URL.match(regex)[1];
    $.get("/user/name/" + id, (name) => {
        whoseCalendar.append("<a class='hidden-link' href='/user/" + id + "'><h1>" + name + "</h1></a>");
    });
} else if (url.includes("team")) {
    const regex = /.+\/calendar\/team\/(.+)/
    let id = document.URL.match(regex)[1];
    $.get("/team/name/" + id, (name) => {
        whoseCalendar.append("<a class='hidden-link' href='/team/" + id + "'><h1>" + name + "</h1></a>");
    });
} else {
    whoseCalendar.append("<h1>All Competitions</h1>");
}

draw();