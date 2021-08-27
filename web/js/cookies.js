function hideFixedBanner(lastModified) {
	setCookie("fixed_banner_updated", lastModified, 30);
	$("#banner-fixed-bottom").remove();
}
function hidуTelegramBanner(lastModified) {
	setCookie("tg_updated", lastModified, 30);
	$("#tg-link").remove();
}

function setCookie(c_name, value, exdays) {
	var exdate = new Date();
	exdate.setDate(exdate.getDate() + exdays);
	var c_value = escape(value)	+ ((exdays == null) ? "" : "; expires=" + exdate.toUTCString());
	document.cookie = c_name + "=" + c_value + "; path=/;";
}
function deleteCookie(c_name){setCookie(c_name, '', -1);}
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
		c_value = unescape(c_value.substring(c_start, c_end));
	}
	return c_value;
}