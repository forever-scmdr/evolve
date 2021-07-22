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
	if(!trg.is("#confirm-dialog") && trg.closest("#confirm-dialog").length == 0 && !trg.is(".call-function") && trg.closest(".call-function").length == 0
		&& !trg.is(".confirm-select") && trg.closest(".confirm-select").length == 0) {
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
 * Вывести диалоговое окно с произвольной функцией.
 * @param el - элемент по которому позиционируется диалоговое окно
 * @param function - функция, которая вызывается при клике на кнопку "да"
 * */
function confirmAjaxViewCustom(el,title, onConfirm) {
    destroyDialog();
    buildDialog(title);
    positionDialog(el);
    $("#dialog-yes-button").click(function (e) {
        e.preventDefault();
        destroyDialog();
        onConfirm.call();
    });

    $("#dialog-no-button").click(function (e) {
        e.preventDefault();
        $("#dialog-yes-button, #dialog-no-button").unbind("click");
        destroyDialog();
    });
}

/**
 * Отправить форму с заданным экшеном.
 * @param action - атрибут "action"
 * @param el - форма
 * @param lockElementIds - jQuery селектор для блокируемых элементов
 * */
function postFormViewWithAction(el, action, lockElementIds, totalRefresh){
	$("#"+el).attr({"action" : action});
	console.log(lockElementIds);

        postForm(el, lockElementIds, function () {
            if(!totalRefresh == true) {
                highlightSelected("#pasteBuffer", "#multi-item-action-form-ids-buffer");
                highlightSelected("#primary-item-list", "#multi-item-action-form-ids");
            }else{
                //TODO make normal ajax block replacement
            	document.location.reload();
			}
        });

}


$(document).on('click', '.set-action', function (e) {
	e.preventDefault();
	var $t = $(this);
	var formId = $t.attr("rel");
	var action = $t.attr("href");
	var id = $t.attr("id");
	var title = $t.attr("title")+"?";
    confirmAjaxViewCustom(this, title, callback);
    function callback() {
        postFormViewWithAction(formId, action, id, $t.is(".total-replace"));
    }
});

/**
 * Отправка AJAX запроса для обновления указанной части страницы
 */
function simpleAjaxView(link, viewId, postProcess) {
	insertAjaxView(link, viewId, false, "hidden_mes", "message_main", postProcess);
}
/**
 * Отправка формы через AJAX запрос с обновлением указанной части страницы
 */
function prepareSimpleFormView(formId, postProcess) {
	prepareForm(formId, "main_view", "hidden_mes", "message_main", postProcess);
}
/**
 *
 * @param el
 * @param message
 * @param href
 * @param type - fancybox (ajax fancybox), iframe (iframe fancybox), ajax (insertAjax), simple or no value (redirect)
 */
function positionOnly(el, message, href, type){
    destroyDialog();
    buildDialog(message);
    positionDialog(el);
    $("#dialog-yes-button").click(function (e) {
        e.preventDefault();
        destroyDialog();
	    if (typeof href == 'string') {
	    	if (!(typeof type == 'string') || type == 'simple') {
			    window.location.replace(href);
		    } else if (type == 'fancybox') {
	    		$.fancybox.open({
	    			src: href,
				    type: 'ajax'
			    });
		    } else if (type == 'iframe') {
			    $.fancybox.open({
				    src: href,
				    type: 'iframe'
			    });
		    } else if (type == 'ajax') {
			    insertAjax(href);
		    } else {
			    alert("Вы нажали кнопку \"" + $(this).text() + "\".");
		    }
	    } else {
		    alert("Вы нажали кнопку \"" + $(this).text() + "\".");
	    }
    });
    $("#dialog-no-button").click(function (e) {
        e.preventDefault();
        $("#dialog-yes-button, #dialog-no-button").unbind("click");
        destroyDialog();
        //alert("Вы нажали кнопку \""+$(this).text()+"\".");
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
						$('#' + insertMessageId).html(message).effect("highlight", 1000);
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
	$('#' + formId).ajaxForm({
		beforeSubmit: function() {
			lock(pagePartId);
		},
		error: function() {
			unlock(pagePartId);
			alert('Ошибка отправки формы');
		},
		success: function(data) {
			unlock(pagePartId);
			// Вставка результата
			$('#' + pagePartId).html(data);
			// Вставка сообщения
			if (typeof messageId != 'undefined' && messageId != null) {
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
$(document).on("click", "#mass-selection-trigger", function (e) {
	e.preventDefault();
	$(".selection-actions, .selection-overlay").toggle();
});
$(document).on("click", ".selection-overlay", function (e) {
	var $t = $(this);
	$t.toggleClass("selected");
	var $ipt = ($t.is(".buffer"))?  $("#multi-item-action-form-ids-buffer") : $("#multi-item-action-form-ids");
    var v = $ipt.val();
    var id = $t.attr("data-id");
	if($t.is(".selected")){
		select();
	}else if(typeof v != "undefined"){
		deselect();
	}

	function select() {
        if(typeof v == "undefined" || v == ""){
            $ipt.val(id);
        }else{
            $ipt.val(v+","+id);
        }
    }
    function deselect() {
        var re = new RegExp(id+",?");
        v = v.replace(re, "").replace(/,$/, "");
        $ipt.val(v);
    }

});

function selectAll() {
	$("#primary-item-list").find(".selection-overlay").not(".selected").trigger("click");
}
function selectNone() {
    $("#primary-item-list").find(".selection-overlay.selected").trigger("click");
}
function invertSelection() {
    $("#primary-item-list").find(".selection-overlay").trigger("click");
}

//Highlights selected. Removes not found by id from inputs
function highlightSelected(container, ipt) {
	var $ipt = $(ipt);
	if(typeof $ipt.val() != "undefined"){
		var arr1 = $ipt.val().split(",");
		var clearedVal = $ipt.val();
		if(arr1.length > 0 && arr1[0] != ""){
			$(".selection-actions, .selection-overlay").show();
		}
		for(i=0; i<arr1.length; i++){
            if(arr1[i] == "")continue;
			var $el = $(container).find(".selection-overlay[data-id="+arr1[i]+"]");
			if($el.length == 0){
				var regex = new RegExp(arr1[i]+',?');
				clearedVal = clearedVal.replace(regex, '');
			}
			else {
                $el.addClass("selected");
            }
		}
		$ipt.val(clearedVal);
	}
}