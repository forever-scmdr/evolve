/**
 * Обновление различных (нескольких) частей страницы с помощью одного AJAX запроса.
 * 
 * Метод отправляет AJAX запрос на сервер, сервер возвращает часть HTML кода определенного формата.
 * Этот код содержит куски, которые нужно вставить в различные части исходной страницы.
 * Каждый кусок обернут в элемент (например, div), который соответствует определенному месту в исходной странице.
 * ID этого элемента соответствует ID элемента исходной страницы, содержимое которого надо заменить на полученное из AJAX запроса.
 * Дополнительно возвращаемый код может содержать элемент с id JSON. В этот элемент можно вставлять любой JSON код.
 * При наличии этого элемента код разбирается и передается в качестве параметра callback функции additionalHandling (третий аргумент 
 * функции ve_insertAjax)
 * 
 * Возвращаемый сервером текст может и не содержать элементы, а быть простой строкой или строкой JSON. В таком случае
 * функция пытается создать JSON объект и передать его callback функцию additionalHandling. Если во время разбора происходит
 * ислючение, в additionalHandling передается просто сам текст результата.
 * 
 * Результат, возвращаемый запросом:
 * 
 * <div id="first" style="result">
 * 		....... HTML 1 ......
 * </div>
 * <div id="second" style="result">
 * 		....... HTML 2 ......
 * </div>
 * <div id="third" style="result">
 * 		....... HTML 3 ......
 * </div>
 * <script id="JSON">
 * 		....... JSON код ......
 * </script>
 * 
 * Исходная обновляемая страница:
 * 
 * ....... HTML ......
 * <span id="first">
 * 		....... HTML 1 ......
 * </span>
 * ....... HTML ......
 * <div id="second">
 * 		....... HTML 2 ......
 * </div>
 * ....... HTML ......
 * <table id="third">
 * 		....... HTML 3 ......
 * </table>
 * ....... HTML ......
 * 
 * Метод берет из результата все элементы, style которых равен "result". Содержимое каждого такого элемента вставляется в 
 * элемент исходной страницы с таким же ID (как в примере).
 * 
 * На время работы метода часть блоков исходной страницы может быть блокирована, список ID этих блоков передается в метод.
 * 
 * @param url - ардес запроса
 * @param lockElementIds - jQuery селектор для блокируемых элементов
 * @additionalHandling - дополнительная функция, которая вызывается после успешного завершения всех действий по обработке запроса
 */
$(document).ready(function(){
	$(".summon-form").click(function(e){
		e.preventDefault();
		ve_insertAjax($(this).attr("href"), $(this).attr("rel"));
		
	});
	$(".popupButton").click(function(e){
		e.preventDefault();
		$("#popup").show();
		y = ($("#popup").height() - $(".popup-window").outerHeight())*0.5;
		$(".popup-window").css({marginTop : y});
	});
});
$(document).on('click', '.ajax-link', function(e){
	e.preventDefault();
	ve_insertAjax($(this).attr("href"));
});
$(document).on('click', '.show-popup', function(e){
	e.preventDefault();
	$("#popup-overlay").show();
});
$(document).on('click', '.ve-close-popup', function(e){
	e.preventDefault();
	$("#popup-overlay").hide();
});

function ve_insertAjax(url, lockElementIds, additionalHandling) {
	$.ajax({
		url: url,
		dataType: "html",
		cache: false,
		error: function(arg1, errorType, arg3) {
			$('#' + lockElementIds).html('Ошибка выполнения AJAX запроса: ' + errorType);
			// Разблокировка частей
			ve_unlock(lockElementIds);
		},
		success: function(data, status, arg3) {
			ve_processResult(data, additionalHandling, lockElementIds, status, arg3);
		}
	});
	// Блокировка частей
	ve_lock(lockElementIds);
}


function ve_insertAjaxSimple(url, targetElementId, lockElementIds, additionalHandling) {
	$.ajax({
		url: url,
		dataType: "html",
		cache: false,
		error: function(arg1, errorType, arg3) {
			$('#' + lockElementIds).html('Ошибка выполнения AJAX запроса: ' + errorType);
			// Разблокировка частей
			ve_unlock(lockElementIds);
		},
		success: function(data, status, arg3) {
			ve_processResultSimple(data, targetElementId, additionalHandling, lockElementIds, status, arg3);
		}
	});
	// Блокировка частей
	ve_lock(lockElementIds);
}

function ve_insertIframe(url, targetElementId) {
	var iframe = $('iframe#' + targetElementId);
	iframe.height($(window).height() * 0.9);
//	alert(container.width() + " x " + container.height());
	iframe.attr('src', url);
}

/**
 * Аналогичные действия ve_insertAjax, только используется форма, а не просто урл
 * Для работы этой функции (для метода ajaxSubmit) нужна библиотека jquery.form
 * 
 * @param form - ID формы дл отправки или сам объект jquery форма
 * @param lockElementIds - jQuery селектор для блокируемых элементов
 * @additionalHandling - дополнительная функция, которая вызывается после успешного завершения всех действий по обработке запроса
 */
function ve_postForm(form, lockElementIds, additionalHandling) {
	if (typeof form == 'string')
		form = $('#' + form);
	form.ajaxSubmit({
		error: function() {
			alert('Ошибка отправки формы');
			// Разблокировка частей
			unlock(lockElementIds);
		},
		success: function(data, status, arg3) {
			processResult(data, additionalHandling, lockElementIds, status, arg3);
		}
	});
	// Блокировка частей
	ve_lock(lockElementIds);
}

function ve_processResult(data, additionalHandling, lockElementIds, status, arg3) {
	// Вставка результатов (если это возможно)
	var possibleJsonData = null;
	if (data.indexOf('<') == 0) {
		try {
			var parsedData = $("<div>" + data + "</div>");
			parsedData.find('.result').each(function() {
				id = $(this).attr('id');
				//if ($('#' + id).length == 0) alert("Не найден элемент с id='" + id + "' в родительском документе");
				$('#' + id).html($(this).html());
			});
			if (!parsedData.find('#JSON').length != 0)
				possibleJsonData = parsedData.find('#JSON').html();
		} catch (e) {
			possibleJsonData = data;
		}
	} else {
		possibleJsonData = data;
	}
	// Разбор JSON данных (если это возможно)
	var argData = null;
	if (possibleJsonData != null) {
		try {
			argData = $.parseJSON(possibleJsonData);
		} catch (e) {
			argData = possibleJsonData;
		}
	}
	// Разблокировка частей
	ve_unlock(lockElementIds);
	// Вызов дополнительной обработки и передача дополнительных данных
	if (typeof additionalHandling == 'function')
		additionalHandling(argData);
}


function ve_processResultSimple(data, targetElementId, additionalHandling, lockElementIds, status, arg3) {
	// Вставка результатов (если это возможно)
	$('#' + targetElementId).html(data);
	// Разблокировка частей
	ve_unlock(lockElementIds);
	// Вызов дополнительной обработки и передача дополнительных данных
	if (typeof additionalHandling == 'function')
		additionalHandling(argData);
}

/**
 * Добавить переменную к указанному урлу
 * @param url
 * @param name
 * @param value
 * @returns {String}
 */
function ve_addVariableToUrl(url, name, value) {
	if (value == null || value.trim() == '')
		return url;
	if (url.indexOf('?') > 0)
		return url + '&' + name + '=' + value;
	else
		return url + '?' + name + '=' + value;
}
/**
 * Для работы этой функции нужна картинка images/loader.gif
 * @param lockElementIds
 */
function ve_lock(lockElementIds) {
	if (Object.prototype.toString.call(lockElementIds) === '[object Array]') {
		for (var i = 0; i < lockElementIds.length; i++) {
			if ($('#' + lockElementIds[i]).length == 1)
				coverWithLoader($('#' + lockElementIds[i]));
		}
	} else {
		if ($('#' + lockElementIds).length == 1) 
			coverWithLoader($('#' + lockElementIds));
	}
}

function ve_unlock(lockElementIds) {
	if (Object.prototype.toString.call(lockElementIds) === '[object Array]') {
		for (var i = 0; i < lockElementIds.length; i++) {
			if ($('#' + lockElementIds[i]).length == 1)
				destroyLoader($('#' + lockElementIds[i]));
		}
	} else {
		if ($('#' + lockElementIds).length == 1) 
			destroyLoader($('#' + lockElementIds));
	}
}

