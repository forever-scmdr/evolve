$(document).ready(function() {
	// -- ссылки на скачивание
	$(".has_sub").mouseenter(function() {
		$(this).find(".lvl1").addClass("active");
		$(this).find(".submenu").show('blind');
	});
	$(".has_sub").mouseleave(function() {
		$(this).find(".lvl1").removeClass("active");
		$(this).find(".submenu").hide();
	});
	
	var $menu = $('#main-menu').find('.submenu');
	$menu.menuAim({
		activate: activateSubmenu,
		deactivate: deactivateSubmenu
	});
});

function activateSubmenu(row) {
	var $row = $(row),
    $submenu = $row.children('ul');
	$submenu.css({display: 'block'});
	$row.children("a").addClass("active");
	$submenu.addClass("active");
}
function deactivateSubmenu(row) {
	var $row = $(row),
    $submenu = $row.children('ul');
	$submenu.css({display: 'none'});
	$row.children("a").removeClass("active");
	$submenu.removeClass("active");
}