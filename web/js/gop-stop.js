function gopRedirect(){
	var URL = "/termobrest/context_form";
	var GOP_COOKIE_NAME = "chotki_patsan";
	var cookie = getCookie(GOP_COOKIE_NAME);
	console.log(document.location);
	if(cookie == "neh"){
		if(document.location != URL){
			document.location.replace(URL);
		}
	}
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