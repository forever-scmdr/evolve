/**
 * Обновление различных (нескольких) частей страницы с помощью одного AJAX запроса.
 * 
 * Метод отправляет AJAX запрос на сервер, сервер возвращает часть HTML кода определенного формата.
 * Этот код содержит куски, которые нужно вставить в различные части исходной страницы.
 * Каждый кусок обернут в элемент (например, div), который соответствует определенному месту в исходной странице.
 * ID этого элемента соответствует ID элемента исходной страницы, содержимое которого надо заменить на полученное из AJAX запроса.
 * Дополнительно возвращаемый код может содержать элемент с id JSON. В этот элемент можно вставлять любой JSON код.
 * При наличии этого элемента код разбирается и передается в качестве параметра callback функции additionalHandling (третий аргумент 
 * функции insertAjax)
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
		insertAjax($(this).attr("href"), $(this).attr("rel"));
		
	});
	$(".popupButton").click(function(e){
		e.preventDefault();
		$("#popup").show();
		y = ($("#popup").height() - $(".popup-window").outerHeight())*0.5;
		$(".popup-window").css({marginTop : y});
	});
});
$(document).on('click', '.close-popup', function(e){
	e.preventDefault();
	$("#popup").hide();
});

function insertAjax(url, lockElementIds, additionalHandling) {
	$.ajax({
		url: url,
		dataType: "html",
		cache: false,
		error: function(arg1, errorType, arg3) {
			$('#' + lockElementIds).html('Ошибка выполнения AJAX запроса: ' + errorType);
			// Разблокировка частей
			unlock(lockElementIds);
		},
		success: function(data, status, arg3) {
			processResult(data, additionalHandling, lockElementIds, status, arg3);
		}
	});
	// Блокировка частей
	lock(lockElementIds);
}
/**
 * Аналогичные действия insertAjax, только используется форма, а не просто урл
 * Для работы этой функции (для метода ajaxSubmit) нужна библиотека jquery.form
 * 
 * @param form - ID формы дл отправки или сам объект jquery форма
 * @param lockElementIds - jQuery селектор для блокируемых элементов
 * @additionalHandling - дополнительная функция, которая вызывается после успешного завершения всех действий по обработке запроса
 */
function postForm(form, lockElementIds, additionalHandling) {
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
	lock(lockElementIds);
}
function postFormView(form, lockElementIds, additionalHandling) {
	if (typeof form == 'string')
		form = $('#' + form);
	form.ajaxSubmit({
		error: function() {
			alert('Ошибка отправки формы');
			// Разблокировка частей
			unlock(lockElementIds);
		},
		success: function(data, status, arg3) {
			$("#subitems").html(data);
		}
	});
	// Блокировка частей
	lock(lockElementIds);
}

function processResult(data, additionalHandling, lockElementIds, status, arg3) {
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
	unlock(lockElementIds);
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
function addVariableToUrl(url, name, value) {
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
function lock(lockElementIds) {
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

function unlock(lockElementIds) {
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

function coverWithLoader (el){
	el = $(el);
	//el.each(function(i){
		//console.log("w="+width);
		var height = el.outerHeight();
		var width = el.outerWidth();
		var br1 = el.css("border-top-left-radius");
		var br2 = el.css("border-top-right-radius");
		var br3 = el.css("border-bottom-left-radius");
		var br4 = el.css("border-bottom-right-radius");
		var mt = -1*el.css("padding-top");
		var ml = -1*el.css("padding-bottom");
		var imgMT = el.outerHeight()*0.5; 
		loader = $('<div>',{
			style:"background: white center no-repeat; position:absolute; z-index: 200;"
		});
		//console.log('imgMt='+imgMt);
		img = $('<img>', {
			 src:'js/loader.gif'
			,alt: 'loading...'
			,style: 'display:inline-block; vertical-align: middle; margin-left:auto; margin-right:auto; max-height: 90%; max-width: 90%;'
		});
		
		loader.addClass('coverLoader');
		loader.append(img);
		$(el).prepend(loader);
		loader = $(el).children('.coverLoader');
		mli = 86;
		if(!$(el).is(".vote_button")){
			loader.css({
				 width: width
				,height:height
				,backgroundColor: "#fff"
				,textAlign:'center'
				,marginTop:mt
				,marginLeft:ml
				,borderTopLeftRadius: br1
				,borderTopRightRadius: br2
				,borderBottomLeftRadius: br3
				,borderBottomRightRadius: br4
			});
			mli = 16
		}
		else{
			loader.css({
				minWidth: 180, marginRight: -40, minHeight: 30, height: 34, marginTop: -1			
			});
			loader.find("img").height(30);
		}
		loader.find("img").css({marginTop: 0.5*(loader.height()-loader.find("img").height()), marginLeft: mli});
}
function destroyLoader(el){
	el = $(el);
	el.children('.coverLoader').remove();
}

$(".ajax-form").submit(function(e){
	e.preventDefault();
	postFormView($(this).attr("id"));
});

