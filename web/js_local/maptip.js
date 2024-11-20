$(window).load(function() {
	window.clickedId = undefined;
	window.region = 'Беларусь';
	initSVG('termo_map_1');
	initSVG('termo_map_2');
	initSVG('termo_map_3');
	initSVG('termo_map_4');
	initSVG('termo_map_5');
	hoverRegion('termo_map_1');
	$(".scroll").click(function(e){
		e.preventDefault();
		rel = $(this).attr("rel");
		setRegion(rel);
		setTimeout(function(){
			v = $(".regData:visible").eq(0);
			v = $(v.length == 0)? $(".table-row:visible").eq(0) : v;
			if(v.length > 0){
				var vTop = v.position().top;
				var body = $("html, body");
				body.stop().animate({scrollTop:vTop}, 1000, 'swing');
			}
		},222);
	});

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


function hoverRegion(svgId) {
	var objId = window.region.replace(' ', '_');
	var svgAdm = document.getElementById(svgId);
	if (svgAdm != null) {
		var svgAdmDom = $(svgAdm.contentDocument);
//		var oldClass = $('.hover', svgAdmDom).attr('class');
//		$('.hover', svgAdmDom).attr('class', oldClass.replace(/hover/g, ''));
//		var newClass = $('#' + objId, svgAdmDom).attr('class');
//		$('#' + objId, svgAdmDom).attr(newClass + ' hover');
		$('.hover', svgAdmDom).removeClass('hover');
		$('#' + objId, svgAdmDom).addClass('hover');
	}
}



//function setRegion(aEl) {
//	//$('a[data-w-tab="' + $(aEl).closest('.w-tab-pane').attr('data-w-tab') + '"]').trigger('click');
//	$(aEl).closest('.subMenu').find('a').removeClass('active');
//	$(aEl).closest('.w-tab-pane').find('.regData').hide();
//	hoverRegion($(aEl).closest('.w-tab-pane').find('object').attr('id'), $(aEl).attr('rel'));
//	$(aEl).addClass('active');
//	$('div[rel="' + $(aEl).attr('rel') + '"]').fadeIn(200);
//}


function setRegion(regName) {
	window.region = regName;
	var pane = $('*[rel="' + regName + '"]').closest('.w-tab-pane');
	$('a[data-w-tab="' + pane.attr('data-w-tab') + '"]').trigger('click');
	pane.find('a').removeClass('active');
	pane.find('.regData').hide();
	setTimeout(function() {
		hoverRegion(pane.find('object').attr('id'));
	}, 300);
	pane.find('a[rel="' + regName + '"]').addClass('active');
	$('div[rel="' + regName + '"]').fadeIn(200);
}

function doInit(svgAdm, roomList) {
	var svgAdmDom = svgAdm.contentDocument;
	var rl = null;
	if (typeof roomList == 'undefined')
		rl = $("#roomList");
	else
		rl = roomList;
	if (svgAdmDom != null) {
		var svgAdmDom = $(svgAdm.contentDocument);
		var room = $('.room', svgAdmDom);
		room.click(function(e) {
			var id = $(this).attr("id").replace('_', ' ');
			setRegion(id);
			// -- TO DO --//
		});
	}
}
