/**
 * Created by E on 20/3/2018.
 */

/**
 * Показать меню первого уровня каталога продукции
 * @param menuTriggerSelector
 * @param menuSelector
 */
function initCatalogPopupMenu(menuTriggerSelector, menuSelector) {
	$(menuTriggerSelector).click(function (event) {
		event.preventDefault();
		$(menuSelector).toggle('fade', 150);
	});
	$(document).click(function (event) {
		var target = $(event.target);
		if (target.closest(menuSelector).length == 0 && target.closest(menuTriggerSelector).length == 0) {
			$(menuSelector).hide('fade', 150);
		}
	});
}


var _catalogPopupMenuShowInterval = 0;
var _catalogPopupMenuHideInterval = 0;
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
			clearInterval(_catalogPopupMenuHideInterval);
			if (_menuMouseMovedVertically) {
				$(l2MenuContainerSelector).hide();
				var submenuSelector = $(this).attr('rel');
				$(submenuSelector).show();
			} else {
				_catalogPopupMenuCurrentItem = $(this);
				_catalogPopupMenuShowInterval = setInterval(function() {
					$(l2MenuContainerSelector).hide();
					var submenuSelector = _catalogPopupMenuCurrentItem.attr('rel');
					$(submenuSelector).show();
				}, 500);
			}
		},
		function() {
			clearInterval(_catalogPopupMenuShowInterval);
			if (_menuMouseMovedVertically) {
				$(l2MenuContainerSelector).hide();
			} else {
				_catalogPopupMenuHideInterval = setInterval(function() {
					$(l2MenuContainerSelector).hide();
				}, 500);
			}
		}
	);
	$(l2MenuContainerSelector).hover(
		function() {
			clearInterval(_catalogPopupMenuHideInterval);
		},
		function() {
			_catalogPopupMenuHideInterval = setInterval(function() {
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
				console.log(_menuMouseMovedVertically + " Y:" + (Y_QUOTIENT * _menuDeltaY) + " X:" + _menuDeltaX);
				_menuMovesCount = 0;
				_menuDeltaX = 0;
				_menuDeltaY = 0;
			}
			_menuPrevX = event.pageX;
			_menuPrevY = event.pageY;
		}
	);
}

$(document).on('click', '.show-sub',function(e){
	e.preventDefault();
	var href = $(this).attr("href");
	var trg = $(href);
	$(".popup-text-menu").not(trg).hide();
	var l = $(this).position().left;
	trg.css({"left": l});
	trg.toggle();
});

$(document).on("click", "body", function(e){
	var trg = $(e.target);
	if(trg.closest(".popup-text-menu").length == 0 && !trg.is(".show-sub")){
		$(".popup-text-menu").hide();
	}
});