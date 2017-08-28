var refreshMain = function() {
	refreshView('main_view');
}

function getInlineEditForm(itemId, formUrl) {
	$('#inline_view').append(
			"&lt;div id='inline_" + itemId + "'&gt;&lt;/div&gt;");
	$.ajax({
		url : formUrl,
		dataType : "html",
		error : function(arg1, errorType, arg3) {
			$('#message_main').html(
					'Ошибка выполнения AJAX запроса: ' + errorType);
		},
		success : function(data, status, arg3) {
			$('#inline_' + itemId).html(data);
		}
	});
}
//if(typeof $.draggable != 'undefined') {
    $(document).on("click", ".drag", function (e) {
        $(this).toggleClass("active");
        $(this).toggleClass("handle");
        if(!$(this).is(".handle")){
        	$(this).closest(".dragable").css({"z-index":"", "top":"", "left":""});
            $(this).closest(".dragable").removeClass("ui-draggable-dragging");
            $(this).closest(".dragable").draggable("disable");
            $(this).closest(".dragable").removeClass("active");
		}else{
            $(this).closest(".dragable").draggable("enable");
		}
    });
    $(".dragable").draggable({
        axis: "y",
        containment: ".drag_area",
        cursor: "n-resize",
        revert: true,
        revertDuration: 200,
        zIndex: 100,
        handle: ".handle"
    });
	$(".dragable").draggable("disable");
    $(".drop-zone").droppable({
        accept: ".dragable",
        hoverClass: "spacer_selected",
        drop: function (event, ui) {
            alert("Тыдыщ!");
            ui.draggable.find(".handle").trigger("click");
        }
        ,over: function(event, ui){
        	ui.draggable.addClass("active");
		}
		,out: function (event, ui) {
            ui.draggable.removeClass("active");
        }
    });
//}