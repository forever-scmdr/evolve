function confirmLink(href , el) {
	destroyDialog();
	buildDialog("Вы уверены?");
	positionDialog(el);
	$("#dialog-yes-button").click(function (e) {
		e.preventDefault();
		destroyDialog();
        window.location.href = href;
    });
	$("#dialog-no-button").click(function (e) {
		e.preventDefault();
		$("#dialog-yes-button, #dialog-no-button").unbind("click");
		destroyDialog();
    });
}

$(document).on("click","body", function (e) {
	var trg = $(e.target);
	if(!trg.is("#confirm-dialog") && trg.closest("#confirm-dialog").length == 0 && !trg.is(".call-function") && trg.closest(".call-function").length == 0){
        destroyDialog();
	}
});

function destroyDialog() {
	$("#confirm-dialog").remove();
	$(".left-col").find(".controls").css({display : ""});
}

function buildDialog(message) {
	var dialog = $("<div>", {"class" : "dialog-tip", id : "confirm-dialog"});
	var content = $("<div>", {"class": "dialog-message"});
	var yes = $("<span>", {"class" : "button yes", id : "dialog-yes-button", text : "Да"});
    var no = $("<span>", {"class" : "button no", id : "dialog-no-button", text : "Нет"});

    content.html(message);
	dialog.append(content);
	dialog.append(yes);
	dialog.append(no);
	dialog.appendTo("body");
}

function positionDialog(el) {
	el = $(el);
	var ctrls = el.closest(".controls");
	ctrls.css({display : "block"});

	var dialog = $("#confirm-dialog");

	var top = el.offset()["top"];
	var left = el.offset()["left"];

	var h = dialog.outerHeight();
	var w = dialog.outerWidth();
	var elW = el.outerWidth();

	dialog.css({
		 top: top - h - 10
		,left: left - (0.5*w) + (0.5*elW)
	});
}

/**
 * Отправка AJAX запроса для обновления указанной части страницы
 * Отдельно выводится сообщение для
 пользователя
 */
function confirmAjaxView(link, viewId, postProcess, el) {
	destroyDialog();
	buildDialog("Вы уверены?");
	positionDialog(el);
    $("#dialog-yes-button").click(function (e) {
        e.preventDefault();
        destroyDialog();
        insertAjaxView(link, viewId, false, "hidden_mes", "message_main", postProcess);
    });

	$("#dialog-no-button").click(function (e) {
        e.preventDefault();
        $("#dialog-yes-button, #dialog-no-button").unbind("click");
        destroyDialog();
    });
}

/**
 * Отправка AJAX запроса для обновления указанной части страницы
 */
function simpleAjaxView(link, viewId, postProcess) {
	insertAjaxView(link, viewId, false, "hidden_mes", "message_main", postProcess);
}

function positionOnly(el, message){
    destroyDialog();
    buildDialog(message);
    positionDialog(el);
    $("#dialog-yes-button").click(function (e) {
        e.preventDefault();
        destroyDialog();
        alert("Вы нажали кнопку \""+$(this).text()+"\".");
    });
    $("#dialog-no-button").click(function (e) {
        e.preventDefault();
        $("#dialog-yes-button, #dialog-no-button").unbind("click");
        destroyDialog();
        alert("Вы нажали кнопку \""+$(this).text()+"\".");
    });
}

$(document).on("change", ".call-function", function (e) {
	positionOnly(this, $(this).attr("data-message"));
});

/************************************************
 * Состояние страницы
 * ID части страницы => URL, содержимое которого сейчас отображается в данной части страницы
 */
var currentState = {'subitems': '', 'main_view': ''};

/**
 * Выполняет AJAX запрос по укзазанному урлу и вставляет полученный результат в указанное место в HTML документе
 * Также отдельно вставляется сообщение.
 * Подразумевается, что сообщение присутствует в результирующем HTML в скрытом состоянии (display: none).
 * Содержимое этого сообщения копируется в указанное место сиходного HTML документа
 * 
 * @param url - ардес запроса
 * @param pagePartId - ID элемента, в который вставляется результат
 * @param confirm - требуется ли подтверждение выполнения действия
 * @param messageId - ID элемента результирующего документа, который содержит сообщение в скрытом виде
 * @param insertMessageId - ID элемента исходного (или результирующего) документа, в который нужно вставить сообщение
 * @param additionalHandling - функция, которая выполняет какие-то дополнительные действия
 */
function insertAjaxView(url, pagePartId, confirm, messageId, insertMessageId, additionalHandling) {
	if (typeof confirm == 'undefined')
		confirm = false;
	if (!confirm || (window.confirm("Для подтверждения нажмите 'Ok'") && window.confirm("Для подтверждения нажмите 'Ok'"))) {
		lock(pagePartId);
		$.ajax({
			url: url,
			dataType: "html",
			error: function(arg1, errorType, arg3) {
				unlock(pagePartId);
				$('#' + insertMessageId).html('Ошибка выполнения AJAX запроса: ' + errorType);
			},
			success: function(data, status, arg3) {
				unlock(pagePartId);
				// Вставка результата
				$('#' + pagePartId).html(data);
				// Вставка сообщения
				if (typeof messageId != 'undefined' && messageId != null) {
					var dom = $.parseHTML(data);
					var message = $(dom[0]).html();
					if (message != null && !(message == '')) {
						$('#' + insertMessageId).html(message);
					}
				}
				// Обновить текущее состояние
				currentState[pagePartId] = url;
				// Дополнительные действия
				if (typeof additionalHandling == 'function')
					additionalHandling();
			}
		});
	}
}
/**
 * Обновить часть страницы (перезагрузить URL, содержимое которого выводится в той части в данный момент)
 * @param pagePartId
 */
function refreshView(pagePartId) {
	insertAjaxView(currentState[pagePartId], pagePartId, false);
}
/**
 * Подготавливает отправку AJAX POST-запроса указанной формы и вставку полученного результата в указанное место в HTML документе
 * 
 * @param formId - ID формы дл отправки
 * @param pagePartId - ID элемента, в который вставляется результат
 * @param messageId - ID элемента результирующего документа, который содержит сообщение в скрытом виде
 * @param insertMessageId - ID элемента исходного (или результирующего) документа, в который нужно вставить сообщение
 * @param additionalHandling - функция, которая выполняет какие-то дополнительные действия
 */
function prepareForm(formId, pagePartId, messageId, insertMessageId, additionalHandling) {
	lock(pagePartId);
	$('#' + formId).ajaxForm({
		error: function() {
			unlock(pagePartId);
			alert('Ошибка отправки формы');
		},
		success: function(data) {
			unlock(pagePartId);
			// Вставка результата
			$('#' + pagePartId).html(data);
			// Вставка сообщения
			if (typeof insertMessageId != 'undefined' && insertMessageId != null) {
				//$('#' + insertMessageId).html($('#' + messageId).html());
				var dom = $.parseHTML(data);
				$('#' + insertMessageId).html($(dom[0]).html());
			}
			// Дополнительные действия
			if (typeof additionalHandling == 'function')
				additionalHandling();
		}
	});
}

$(document).on("keypress", "body", function(e){
	key = e.key
	if(key == "F9" || key == "F8"){
        e.preventDefault();
		t = (key == "F8")? $("#save") : $("#save-and-exit");
		t.trigger("click");
	}
});

$(document).on("click", ".toggle-hidden", function (e) {
	e.preventDefault();
	t = $(this);
	$(t.attr("href")).toggle();
});
