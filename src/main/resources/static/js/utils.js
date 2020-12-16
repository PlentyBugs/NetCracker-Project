/**
 * Метод используется для фильтрации элементов items на основе содержания filter
 * @param filter Объект (input), на основе text() которого происходит фильтрация
 * @param items Множество фильтруемых объектов
 */
function filter(filter, items) {
    for (let item of items) {
        if ($(item).text().toLowerCase().includes(filter.toLowerCase())) {
            $(item).css("display", "block");
        } else {
            $(item).css("display", "none");
        }
    }
}

/**
 * Метод используется для получения url.
 * Он нужен, чтобы получить url сайта, на котором тот запущен.
 * Пример: <b>http://127.0.0.1:8080</b>/competition или <b>https://nethacker.herokuapp.com</b>/competition
 * @returns {string} URL сайта
 */
function getUrl() {
    return document.URL.match(/(https?:\/\/.+?\/)\/?.*/)[1];
}

/**
 * Метод используется для приглашения пользователя в команду
 * @param userId Id приглашаемого пользователя
 * @param teamId Id команды, в которую приглашают
 * @param url URL сайта на который отправляется запрос
 */
function inviteUser(userId, teamId, url) {
    let token = $('#_csrf').attr('content');
    let header = $('#_csrf_header').attr('content');
    $.ajax({
        type: 'PUT',
        url: url + "team/" + teamId + "/invite/" + userId,
        beforeSend: (xhr) => xhr.setRequestHeader(header, token),
        async: true,
        cache: false
    });
}

/**
 * Метод используется, чтобы добавить анимацию "успешного" нажатия на кнопку.
 * Обязательными полями являются только button и commonText.
 * Метод добавляет следующую анимацию: при нажатии на кнопку удаляется commonClass с неё, добавляется successClass,
 * затем текст кнопки меняется на successText, через timeout все возвращается назад: с кнопки удаляется класс
 * successClass и добавляется commonClass, а текст меняется с successText на commonText
 * @param button Кнопка, на которую добавляем анимацию
 * @param commonText Обычный текст кнопки, до нажатия
 * @param successText Текст кнопки после нажатия
 * @param timeout Время, через которое все вернется в исходное положения
 * @param commonClass Класс, с которым кнопка была до нажатия. Предполагается как графический, вроде btn-warning
 * @param successClass Класс, с которым кнопку будет после нажатия. Предполагается как графический, вроде btn-success
 */
function playSuccessButtonAnimation(button, commonText, successText = "Success", timeout = 1500, commonClass = "btn-warning", successClass = "btn-success") {
    button = $(button);
    button.text(successText);
    button.removeClass(commonClass);
    button.addClass(successClass);
    setTimeout(() => {
        button.text(commonText);
        button.removeClass(successClass);
        button.addClass(commonClass);
    }, timeout);
}

/**
 * Метод используется для генерации кнопки приглашения
 * @param teams Список команд, в которые можно будет пригласить
 * @param userId Id пользователя, который будет приглашать
 * @param inviteButton Кнопка, к которой будет генерироваться список команд с записью
 * @returns {*|Window.jQuery|HTMLElement} Блок с командами-приглашениями
 */
function buildInviteButton(teams, userId, inviteButton) {
    let url = getUrl();
    let teamBlock = $("<div class='teams-dropdown d-none btn-group-vertical'></div>");
    let filterTeamBlock = $("<input type='text' class='form-control' placeholder='User' autocomplete='off' autocapitalize='off' />");
    for (let team of teams) {
        let t = $("<button type='button' class='btn btn-warning btn-block team-in-dropdown-" + userId + "'>" + team.teamName + "</button>");
        t.click(() => {
            inviteUser(userId, team.id, url);
            playSuccessButtonAnimation(t, team.teamName, "Invited");
        });
        teamBlock.append(t);
    }
    filterTeamBlock.keyup(() => filter(filterTeamBlock.val(), $(".team-in-dropdown-" + userId)));
    teamBlock.prepend(filterTeamBlock);

    $(inviteButton).click(() => {
        teamBlock.toggleClass("d-none");
        teamBlock.toggleClass("d-block");
    });

    return teamBlock;
}

/**
 * Метод используется для запроса всех команд пользователя по его Id
 * @param userId Id пользователя, чьи команды мы запрашиваем
 * @returns {[]} Список команд пользователя
 */
function loadTeamsByUserId(userId) {
    let teams = [];
    $.ajax({
        type: 'GET',
        url: getUrl() + 'user/team/' + userId,
        async: false,
        cache: false,
        success: (data) => teams = data
    });

    return teams;
}

/**
 * Добавляет массивам функцию remove(), который позволяет удалять объект по нему же
 * @returns {Array} Массив без удаляемого элемента
 */
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