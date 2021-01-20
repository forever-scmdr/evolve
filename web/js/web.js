/**
 * Created by E on 20/3/2018.
 */

/**
 * Показать меню первого уровня каталога продукции
 * @param menuTriggerSelector
 * @param menuSelector
 */

var _hoverHideMenuTimeout = 0;

function initCatalogPopupMenu(menuTriggerSelector, menuSelector) {
	// $(menuTriggerSelector).click(function (event) {
	// 	event.preventDefault();
	// 	$(menuSelector).toggle('fade', 150);
	// });

	$(menuTriggerSelector).mouseenter(function(event){
		clearTimeout(_hoverHideMenuTimeout);
		$(menuSelector).show('fade',150);
	});
	$(menuTriggerSelector).mouseleave(function(){
			_hoverHideMenuTimeout = setTimeout(function(){
			$(menuSelector).hide('fade',150);
		},500);
	});

	$(menuSelector).mouseenter(function(){
		clearTimeout(_hoverHideMenuTimeout);
		
	});
	$(menuSelector).mouseleave(function(){
		_hoverHideMenuTimeout = setTimeout(function(){
			$(menuSelector).hide('fade',150);
		},500);
	});

	$(document).click(function (event) {
		var target = $(event.target);
		if (target.closest(menuSelector).length == 0 && target.closest(menuTriggerSelector).length == 0) {
			$(menuSelector).hide('fade', 150);
		}
	});
}



var _catalogPopupMenuShowTimeout = 0;
var _catalogPopupMenuHideTimeout = 0;
var _catalogPopupMenuCurrentItem = 0;
/**
 * Показывает второй уровень меню для главного меню
 * Главное меню (все пункты) содержится в контейнере l1MenuContainerSelector
 * Множество всех меню второго уровня находятся в контейнерах l2MenuContainerSelector
 * Пункты главного меню (которые определюят, какое подменю показывать, определяются селектором l1MenuItemSelector
 * @param l1MenuContainerSelector
 * @param l1MenuItemSelector
 * @param l2MenuContainerSelector
 */
function initCatalogPopupSubmenu(l1MenuContainerSelector, l1MenuItemSelector, l2MenuContainerSelector) {
	$(l1MenuItemSelector).hover(
		function() {
			clearTimeout(_hoverHideMenuTimeout);
			clearTimeout(_catalogPopupMenuHideTimeout);
			if (_menuMouseMovedVertically) {
				$(l2MenuContainerSelector).hide();
				var submenuSelector = $(this).attr('rel');
				$(submenuSelector).show();
			} else {
				_catalogPopupMenuCurrentItem = $(this);
				_catalogPopupMenuShowTimeout = setTimeout(function() {
					$(l2MenuContainerSelector).hide();
					var submenuSelector = _catalogPopupMenuCurrentItem.attr('rel');
					$(submenuSelector).show();
				}, 500);
			}
		},
		function() {
			clearTimeout(_hoverHideMenuTimeout);
			clearTimeout(_catalogPopupMenuShowTimeout);
			if (_menuMouseMovedVertically) {
				$(l2MenuContainerSelector).hide();
			} else {
				_catalogPopupMenuHideTimeout = setTimeout(function() {
					$(l2MenuContainerSelector).hide();
				}, 500);
			}
		}
	);
	$(l2MenuContainerSelector).hover(
		function() {
			clearTimeout(_hoverHideMenuTimeout);
			console.log("l2MenuContainerSelector ENTER");			
			clearTimeout(_catalogPopupMenuHideTimeout);
		},
		function() {
			console.log("l2MenuContainerSelector LEAVE");
			clearTimeout(_hoverHideMenuTimeout);
			_catalogPopupMenuHideTimeout = setTimeout(function() {
				$(l2MenuContainerSelector).hide();
			}, 500);
		}
	);
	var _menuPrevX = 1000;
	var _menuPrevY = -1000;
	var _menuMouseMovedVertically = true;
	var _menuDeltaX = 0;
	var _menuDeltaY = 0;
	var _menuMovesCount = 0;
	var MENU_MAX_MOVES_COUNT = 5;
	var Y_QUOTIENT = 0.8;
	$(l1MenuContainerSelector).mousemove(
		function(event) {
			_menuDeltaX += Math.abs(event.pageX - _menuPrevX);
			_menuDeltaY += Math.abs(event.pageY - _menuPrevY);
			_menuMovesCount++;
			if (_menuMovesCount >= MENU_MAX_MOVES_COUNT) {
				_menuMouseMovedVertically = (Y_QUOTIENT * _menuDeltaY - _menuDeltaX) > 0;
				//console.log(_menuMouseMovedVertically + " Y:" + (Y_QUOTIENT * _menuDeltaY) + " X:" + _menuDeltaX);
				_menuMovesCount = 0;
				_menuDeltaX = 0;
				_menuDeltaY = 0;
			}
			_menuPrevX = event.pageX;
			_menuPrevY = event.pageY;
		}
	);
}

var activeShowSub = null;
$(document).on('click', '.show-sub',function(e){
	e.preventDefault();
	var href = $(this).attr("href");
	var trg = $(href);
	// $(".popup-text-menu").not(trg).hide();
	// var l = $(this).position().left - 50;
	// trg.css({"left": l});
	trg.toggle();
	if (trg != activeShowSub) {
		if (activeShowSub != null)
			activeShowSub.toggle();
		activeShowSub = trg;
	}
});

$(document).on("click", "body", function(e){
	var trg = $(e.target);
	if(trg.closest(".popup-text-menu").length == 0 && !trg.is(".show-sub") && trg.closest(".show-sub").length == 0) {
		$(".popup-text-menu").hide();
		activeShowSub = null;
	}
});


function showDetails(link) {
	$("#product-ajax-popup").show();
	insertAjax(link, 'product-ajax-content', function(){
		$("#fotorama-ajax").fotorama();
		$("#product-ajax-popup").find('a[data-toggle="tab"]').on('click', function(e){
			e.preventDefault();
			$("#product-ajax-popup").find('a[data-toggle="tab"]').removeClass("tabs__link_active");
			$("#product-ajax-popup").find('.tabs__content').removeClass("active");
			$("#product-ajax-popup").find('.tabs__content').hide();
			$(this).addClass("tabs__link_active");
			var href = $(this).attr("href");
			$(href).show();
			$(href).addClass("active");
		});
	});
}


function clearProductAjax() {
	var html = "<div class=\"popup__body\">\n" +
		"\t\t\t\t<div class=\"popup__content\">\n" +
		"\t\t\t\t\t<a class=\"popup__close\" onclck=\"clearProductAjax();\">×</a>\n" +
		"\t\t\t\t</div>\n" +
		"\t\t\t</div>";
	$("#product-ajax-popup").html(html);
	$("#product-ajax-popup").hide();
}

$("a.tab").click(function() {
	$("div.tab-container").hide(0);
	$("a.tab").removeClass("tab_active");
	var showSelector = $(this).attr("href");
	$(showSelector).show("fade", 150);
	$(this).addClass("tab_active");
	return false;
});

$(document).on("click", ".toggle", function(e){
	e.preventDefault();
	var $t = $($(this).attr("href"));
	$t.toggle();
	var rel = $(this).attr("rel");
	if(rel != '' || rel != null || typeof rel != "undefined"){
		var html = $(this).text();
		$(this).text(rel);
		$(this).attr("rel", html);
	}
});