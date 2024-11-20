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

$(".dragable").draggable({
	axis: "y",
	containment: ".drag_area",
	cursor: "n-resize",
	revert: true,
	revertDuration: 200,
	zIndex: 100
});
$(".drop-zone").droppable({
	accept: ".dragable",
	hoverClass: "spacer_selected",
	drop: function(event, ui) {
		var itemNums = ui.draggable.attr('id').substring(4).split(':');
		var posNums = $(this).attr('id').substring(5).split(':');
		var itemId = itemNums[0]; // number conversion http://www.jibbering.com/faq/faq_notes/type_convert.html
		var itemWeight = itemNums[1];
		var weightBefore = posNums[0];
		var weightAfter = posNums[1]; 
		if (weightBefore != itemWeight && weightAfter != itemWeight) {
			reorderLink = reorderLink.replace(":id:", itemId);
			reorderLink = reorderLink.replace(":wb:", weightBefore);
			reorderLink = reorderLink.replace(":wa:", weightAfter);
			defaultView(reorderLink, 'subitems');
			//alert(reorderLink);
		}
	}
});