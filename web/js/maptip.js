$(window).load(function() {
	window.clickedId = undefined;
	window.region = 'Беларусь';
	initSVG('termo_map_1');
});


function initSVG(id, roomList) {
	var svgAdm = document.getElementById(id);
	if (svgAdm != null) {
		svgAdm.addEventListener("load", function() {
			doInit(svgAdm, roomList);
		}, false);
		doInit(svgAdm, roomList);
	}
}


function setRegion(regName) {
	window.region = regName;
	$('*[rel="' + regName + '"]').first().trigger('click');
}

function doInit(svgAdm) {
	var svgAdmDom = svgAdm.contentDocument;
	if (svgAdmDom != null) {
		var svgAdmDom = $(svgAdm.contentDocument);
		var room = $('.room', svgAdmDom);
		room.click(function(e) {
			var id = $(this).attr("id").replace('_', ' ');
			setRegion(id);
			// -- TO DO --//
		});
		room.mouseenter(function(e){
			var trg = $("#legend-"+$(this).attr("id"));
			//$(".map-legend").not(trg).hide();
			trg.show();
		});
		room.mouseleave(function(e){
			//$(".map-legend").hide();
		});
	}
}
