var $menu = $(".side-menu");

function activateSubmenu(row) {
	var $row = $(row);
	$submenu = $row.find(".popup-menu");
	if($submenu.length == 1){
		$row.addClass("active");
		$submenu.fadeIn(400);
	}
}
function deactivateSubmenu(row) {
	var $row = $(row);
	$submenu = $row.find(".popup-menu");
	$submenu.fadeOut(200, function() { $row.removeClass("active"); });
}

$(document).ready(function(){
		if(typeof $.fn.menuAim == "function"){
		$menu.menuAim({
			 activate: activateSubmenu
			,deactivate: deactivateSubmenu
			,rowSelector: "> .level-1"
		});
	}
	$(".popup-menu").mouseleave(function(){
		deactivateSubmenu($(this).parent(".level-1"));
	});

	$(".compare-inner").css({marginLeft: 0, overflow: "hidden"});
	fx = $("table.compare").find(".fx");
	fx.css({position: "static"});
	for (var i = 0; i < fx.length; i++) {
		th = $(fx[i]);
		td = th.siblings(".first").find("div");
		th.css({height : th.height()});
		td.css({height : th.height()});
	}
	ua = navigator.userAgent;
	if(ua.indexOf("Chrome") != -1){
		fx.css({marginTop: 1});
	}
	fx.css({position: ""});
	$(".compare-inner").css({marginLeft: "", overflow: ""});
	if ($(".toggle-view-button").not('.order_link').length > 0) {
		catalogView = getCookie("catalog_view");
		$device = $(".devices");
		$device.attr("class", "devices");
		$device.addClass(catalogView);
		$(".toggle-view-button").removeClass("active");
		$((".toggle-view-button[href='"+catalogView+"']")).addClass("active");
	}

	//-- toggle menu

	$(".toggle-sec").click(function(e){	
		e.preventDefault();
		
		$(".side-menu-level").removeClass("to-toggle");
		h = $(this).attr("href");
		trg = $(h);
		if(trg.length == 0){
			document.location.href = $(this).next("a").attr("href");
		}
		hide = trg.is(":visible");
		if(!hide){
			trg.show();
			$(this).text("-");
		}else{
			$(this).text("+");
			trg.addClass("to-toggle");
			for (var i = 0; i < trg.length; i++) {
				ti = trg.eq(i);
				if(ti.is(".empty")){continue;}
				a = $(ti).children(".toggle-sec:eq(0)");
				if(a.length > 0){
					t2 = $(a.attr("href"));
					t2.addClass("to-toggle");
					for (var i = 0; i < t2.length; i++) {
						ti2 = t2.eq(i);
						if(ti2.is(".empty")){continue;}
						a = $(ti2).children(".toggle-sec:eq(0)");
						if(a.length > 0){
							t3 = $(a.attr("href"));
							t3.addClass("to-toggle");
						}
					}
				}
			}
			$(".to-toggle").hide();
		}
	});

	$(".toggle-sec-active").click(function(e){
		e.preventDefault();
		$(this).prev(".toggle-sec").trigger("click");
	});
	$(".toggle-l1").click(function(e){
		e.preventDefault();
		$($(this).attr("rel")).toggle();
	});
	
	//-- chosen!
	
	var CHOSEN_COOKIE = "chosen_coo";
	var SEP = "┌";
	var chK = getCookie(CHOSEN_COOKIE);
	if(chK != null && typeof chK == "string"){
		arr = chK.split(SEP);
		for(i = 0; i<arr.length; i++){
			if(arr[i].length>1){
				x = $("#"+arr[i]);
				x.next("a").css({display: 'inline'});
				x.remove();
			}
		}
	}
	
	$(document).on("click", ".chosen_ipt", function(e){
		id = $(this).attr("id");
		if(chK == null && typeof chK != "string"){
			chK = id+SEP;
		}else if(chK.indexOf(id+SEP) == -1){
			chK += id+SEP;
		}
		setCookie(CHOSEN_COOKIE, chK, 200);
		$(this).next("a").css({display: 'inline'});
		$(this).remove();
		var countChosen = $(".view_chosen:visible").length;
		countChosen = (countChosen > 0)? "("+countChosen+")" : "";
		$("#fvcnt").text(countChosen);
	});
	
	$(document).on("click", ".delete_chosen", function(e){
		e.preventDefault();
		id = $(this).attr("href")+SEP;
		if(chK != null && typeof chK == "string" && chK.indexOf(id) != -1){
			chK = chK.replace(id,"");
		}
		setCookie(CHOSEN_COOKIE, chK, 200);
		$(this).closest(".device").remove();
		var countChosen = ($(".delete_chosen").length > 0)? "("+ $(".delete_chosen").length + ")": "";
		$("#fvcnt").text(countChosen);
	});
	
	//-- END_chosen
});

$(document).ready(function(){
	$(".order").each(function(){
		t = 0;
		$sums = $(this).find(".new-sum");
		for(i=0;i<$sums.length;i++){
			txt = $sums.eq(i).text();
			txt = txt.replace(/[^0-9,]/g,"").replace(",",".")*1;
			if(typeof txt != 'number') continue;
			t+=txt;
		}
		t = (t+"").replace(/(\d)(?=(\d\d\d)+([^\d]|$))/g, '$1 ').replace(".", ",");
		$(this).find(".js-total").text(t);
		$("#"+$(this).attr("id").replace("ord_","")).text(t);
	});
});

$(document).on('click', '.toggle', function(e){
	e.preventDefault();
	$($(this).attr("href")).toggle();
	rel = $(this).attr("rel");
	if(typeof rel == "string"){
		$(this).attr("rel", $(this).text());
		$(this).text(rel);
	}
});
$(document).on('click', '.close-popup', function(e){
	e.preventDefault();
	$($(this).closest(".popup")).hide();
});
$(document).on('click', '.toggle-view-button', function(e){
	e.preventDefault();
	$(this).siblings(".toggle-view-button").removeClass("active");
	$(this).addClass("active");
	$device = $(".devices");
	$device.attr("class", "devices");
	aditional = $(this).attr("href");
	$device.addClass(aditional);
	deleteCookie("catalog_view");
	setCookie("catalog_view", aditional, 20);
});

// -- Cookie functions
function setCookie(c_name, value, exdays) {
	var exdate = new Date();
	exdate.setDate(exdate.getDate() + exdays);
	var c_value = encodeURIComponent(value);
	if (c_value == null)
		c_value = "";
	if (typeof exdays != 'undefined' && exdays != null)
		c_value += "; expires=" + exdate.toUTCString();
	document.cookie = c_name + "=" + c_value + "; path=/;";
}

function deleteCookie(c_name){
	n = c_name
	setCookie(n, "", -1);
}

function getCookie(c_name) {
	var c_value = " " + document.cookie;
	var c_start = c_value.indexOf(" " + c_name + "=");
	if (c_start == -1) {
		c_start = c_value.indexOf(c_name + "=");
	}
	if (c_start == -1) {
		c_value = null;
	} else {
		c_start = c_value.indexOf("=", c_start) + 1;
		var c_end = c_value.indexOf(";", c_start);
		if (c_end == -1) {
			c_end = c_value.length;
		}
		c_value = decodeURIComponent(c_value.substring(c_start, c_end));
	}
	return c_value;
}

// popup closer
$(document).on('click', function(e) {
	t = $(e.target);
	if (!t.is(".popup, .toggle") && t.closest(".popup, .toggle").length < 1) {
		$(".popup").hide();
	}
});