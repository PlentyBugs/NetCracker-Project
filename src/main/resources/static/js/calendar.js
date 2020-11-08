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

let days = ["Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"];
let months = ["January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"];
let date = (new Date());

let dateToday = date || new Date(),
    month = date.getMonth(),
    year = date.getFullYear(),
    first = new Date(year, month, 1),
    last = new Date(year, month + 1, 0),
    startingDay = first.getDay(),
    thedate = new Date(year, month, 1 - startingDay)
    today = new Date();

$("#monthYear").append(months[month] + " " + year)

let weeksHead = $("#weeksHead");
for (let i = 0; i < 7; i++) {
    let dayInHead = $("<th class='c-name'>" + days[i] + "</th>");
    weeksHead.append(dayInHead);
}

let daysTable = $("#days");

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
            "<td class=\"calendar-day " + dayClass + " " + thedate.toDateCssClass() + " " + (date.toDateCssClass() === thedate.toDateCssClass() ? 'selected':'') + " js-cal-option\" data-date='" + thedate.toISOString() + "'>" +
            "<div class='date'>" + thedate.getDate() + "</div></td>"
        );
        thedate.setDate(thedate.getDate() + 1);
        rowOfDays.append(day);
    }
    daysTable.append(rowOfDays);
}
