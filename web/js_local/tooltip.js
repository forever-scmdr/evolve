$(window).load(function() {
	fitHeight();
	$(".scroll").click(function(e) {});
});

$(document).on("click", ".close2", function(e) {
	e.preventDefault();
	$(this).closest("div").hide();
});

function initSVG(id, roomList) {
	var svgAdm = document.getElementById(id);
	svgAdm.addEventListener("load", function() {
		doInit(svgAdm, roomList);
	}, false);
	doInit(svgAdm, roomList);
}

function doInit(svgAdm, roomList) {
	var svgAdmDom = svgAdm.contentDocument;
	var tooltip = $("#tooltip");
	var rl = null;
	if (typeof roomList == 'undefined')
		rl = $("#roomList");
	else
		rl = roomList;
	if (svgAdmDom != null) {
		var svgAdmDom = $(svgAdm.contentDocument);
		var room = $('.room', svgAdmDom);
		$('.room', svgAdmDom)
				.each(
						function() {
							var id = $(this).attr("id");
							var trg = $(id);
							$(this)
									.attr(
											{
												"class" : "room "
														+ trg.attr("class"),
												title : "Нажмите чтобы узнать характеристики помещения и стоимость аренды."
											});

							$(this).css('display', 'none');
						});

		rl.find('div').each(function() {
			$('#' + $(this).attr('id'), svgAdmDom).css('display', 'inline');
		});

		room.click(function(e) {
			var id = "#" + $(this).attr("id");
			$("#tooltip_text").html("Нет сведений о помещении.");
			$("#tooltip_text").html($(id).html());
			var b102 = $(this).children("g:eq(0)");
			var prentTop = $(svgAdm).offset().top;
			var position = b102.offset();
			var top = position["top"];
			var svgPosition = $(svgAdm).offset();
			top += svgPosition["top"];
			left = position.left + svgPosition.left;
			h = tooltip.outerHeight();
			top -= (h + 10);
			left -= (tooltip.outerWidth() * 0.5) - 10;
			tooltip.css({
				top : top
				,
				left : left
				,
				display : 'block'
			});
		});
	}

}