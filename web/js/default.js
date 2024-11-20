$(document).on('click', '.spoiler-toggle', function(e){
	$(this).next(".spoiler-text").toggle();
	$(this).toggleClass("spoiler-open");
	$(".sppinler, .spoiler *").removeAttr("contenteditable");
});
$(document).ready(function(){
	$(".spoiler, .spoiler *").removeAttr("contenteditable");
});
//-- Cookie functions
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

function deleteCookie(cName) {
	setCookie(cName, undefined, -1);
}