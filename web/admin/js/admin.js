function confirmLink(href) {
	if (confirm("Для подтверждения нажмите 'Ok'") && confirm("Для подтверждения нажмите 'Ok'")) {
		window.location.href = href;
	}
}
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
		$.ajax({
			url: url,
			dataType: "html",
			error: function(arg1, errorType, arg3) {
				$('#' + insertMessageId).html('Ошибка выполнения AJAX запроса: ' + errorType);
			},
			success: function(data, status, arg3) {
				// Вставка результата
				$('#' + pagePartId).html(data);
				// Вставка сообщения
				if (typeof insertMessageId != 'undefined' && insertMessageId != null) {
					var dom = $.parseHTML(data);
					$('#' + insertMessageId).html($(dom[0]).html());
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
	$('#' + formId).ajaxForm({
		error: function() {
			alert('Ошибка отправки формы');
		},
		success: function(data) {
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
		t = (key == "F8")? $("#save") : $("#save-and-exit");
		t.trigger("click");
	}
});

$(document).on("click", ".toggle-hidden", function (e) {
	e.preventDefault();
	t = $(this);
	$(t.attr("href")).toggle();
});
