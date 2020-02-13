/**
 * Created by user on 13.03.2019.
 */

function setCookie(c_name, value, exMs) {
	var ms = new Date().getTime() + exMs;
	var exdate = new Date(ms);
	var c_value = escape(value) + ((exMs == null) ? "" : "; expires=" + exdate.toUTCString());
	document.cookie = c_name + "=" + c_value + "; path=/;";
}

function deleteCookie(c_name) {
	setCookie(c_name, '', -1 * 24 * 60 * 60 * 1000);
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
		c_value = unescape(c_value.substring(c_start, c_end));
	}
	return c_value;
}

String.prototype.isEmpty = function () {
	return this.length == 0;
};

function pad(num) {
	var s = num + "";
	while (s.length < 2) s = "0" + s;
	return s;
}

$(document).ready(function () {
	discount();
});

Number.prototype.pad = function (size) {
	var s = String(this);
	while (s.length < (size || 2)) {
		s = "0" + s;
	}
	return s;
}

function discount() {
	const COOKIE_LIFETIME = 30 * 60 * 60 * 1000;
	const VISITED_COOKIE_NAME = "visited";
	const DISCOUNT_COOKIE_NAME = "discount_";
	const DEVICE_COOKIE_NAME = "device_";
	const DURATION_COOKIE_NAME = "duration_";
	const START_COOKIE_NAME = "start_";
	const VISIT_TIME_COOKIE_NAME = "visit_";
	const USED_COOKIE_NAME = "used_";
	const WINDOW_CLOSED_COOKIE_NAME = "window_closed";
	const DISCOUNT_ACTIVE_COOKIE_NAME = "discount_active";

	var $dsc = $("#dsc-data");
	var $dscDevice = $("#dsc-device");
	var now = new Date().getTime();
	var pagesVisitedCookie = getCookie(VISITED_COOKIE_NAME);
	var windowClosedCookie = getCookie(WINDOW_CLOSED_COOKIE_NAME);
	var keys = [];
	var deviceName = {};
	var discount = {};
	var visit = {};
	var start = {};
	var duration = {};
	var lastStop = now + 10000;
	var discountUsed = {};
	var time = now;
	var windowClosed;

	initVisited();
	step();

	function step() {
		if (typeof discountTimeout != "undefined") {
			clearTimeout(discountTimeout);
		}
		if (time < lastStop) {
			discountTimeout = setTimeout(step, 1000);
			for (var i = 0; i < keys.length; i++) {
				var v = visit[keys[i]];
				var s = start[keys[i]];
				var d = duration[keys[i]];
				var passed = time - v;
				if (passed > s && passed < d) {
					console.log("active: " + ((s + d) - passed));
					activateDiscount(keys[i]);
					showWindow();
				} else if (passed >= d) {
					deactivateDiscount(keys[i]);
					console.log("expired: " + (passed - d));
				} else {
					console.log("left:" + (s - passed));
				}
			}
		} else {
			deleteCookie(WINDOW_CLOSED_COOKIE_NAME);
			deleteCookie(DISCOUNT_ACTIVE_COOKIE_NAME);
			$("#discount-popup").hide();
			$("#discount-popup-2").hide();
			//setTimeout(function(){document.location.reload();},1000);
		}
		time += 1000;
	}

	function activateDiscount(key) {
		var cookie = getCookie(DISCOUNT_ACTIVE_COOKIE_NAME);
		if (cookie == null || typeof cookie == "undefined" || cookie == "") {
			setCookie(DISCOUNT_ACTIVE_COOKIE_NAME, key, COOKIE_LIFETIME);
		} else {
			var arr = new Array();
			arr = cookie.split(",");
			arr.push(key);
			arr = [...new Set(arr)];
			setCookie(DISCOUNT_ACTIVE_COOKIE_NAME, arr.join(","), COOKIE_LIFETIME);
		}
		var $dscContainer = $("#active_discounts_list");
		if ($dscContainer.find(".li-" + key).length == 0) {
			var $li = $("<li>", {
				"class": "li-" + key,
				"text": "До окончания действия скидки" + discount[key] * 100 + "% на " + deviceName[key] + " осталось "
			});
			var $span = $("<strong>", {"class": "dsc-timer-" + key});
			$li.append($span);
			$dscContainer.append($li);
		}
		var $dscContainer2 = $("#active_discounts_list-2");
		if ($dscContainer2.find(".li-" + key).length == 0) {
			var $li2 = $("<li>", {
				"class": "li-" + key,
				"text": "До окончания действия скидки " + discount[key] * 100 + "% на " + deviceName[key] + " осталось "
			});
			var $span2 = $("<strong>", {"class": "dsc-timer-" + key});
			$li2.append($span2);
			$dscContainer2.append($li2);
		}
		showTime(key);
	}

	function showTime(key) {
		var v = visit[key];
		var f = start[key] + duration[key];
		var passed = time - v;
		var left = f - passed;
		var msLeft = minSec(left);
		$(".dsc-timer-" + key).each(function (i) {
			$(this).text(msLeft);
		});
	}

	function deactivateDiscount(key) {
		var cookie = getCookie(DISCOUNT_ACTIVE_COOKIE_NAME);
		if (cookie == null || typeof cookie == "undefined" || cookie == "") return;
		var arr = new Array();
		arr = cookie.split(",");
		var newCookie = "";
		var z = 0;
		for (i = 0; i < arr.length; i++) {
			if (arr[i] == key) continue;
			if (z > 0) newCookie += ",";
			newCookie += arr[i];
			z++;
		}
		if (newCookie.length == 0) {
			deleteCookie(DISCOUNT_ACTIVE_COOKIE_NAME);
		} else {
			setCookie(DISCOUNT_ACTIVE_COOKIE_NAME, newCookie, COOKIE_LIFETIME);
		}
		$(".li-" + key).remove();
	}

	function initVisited() {
		//ensure main cookie
		if (pagesVisitedCookie == null || typeof pagesVisitedCookie == "undefined" || pagesVisitedCookie == "") {
			var visitedPage = new Array();
			if ($dsc.length > 0) {
				visitedPage.push("page");
			}
			if ($dscDevice.length > 0) {
				visitedPage.push($dscDevice.attr("data-code"));
			}
			visitedPage = [...new Set(visitedPage)];
			keys = visitedPage;
			setCookie(VISITED_COOKIE_NAME, visitedPage.join(','), COOKIE_LIFETIME);
		} else {
			//update main cookie
			var currentValue = getCookie(VISITED_COOKIE_NAME).split(',');
			if ($dsc.length > 0) {
				currentValue.push("page");
			}
			if ($dscDevice.length > 0) {
				currentValue.push($dscDevice.attr("data-code"));
			}
			currentValue = [...new Set(currentValue)];
			//console.log(currentValue);
			keys = currentValue;
			setCookie(VISITED_COOKIE_NAME, currentValue.join(','), COOKIE_LIFETIME);
		}
		//set individual cookies
		lastStop = 0;
		if ($dsc.length > 0) {
			//device name
			setCookie(DEVICE_COOKIE_NAME + "page", "все товары", COOKIE_LIFETIME);
			//last
			setCookie(DURATION_COOKIE_NAME + "page", $dsc.attr("data-last"), COOKIE_LIFETIME);
			//start
			setCookie(START_COOKIE_NAME + "page", $dsc.attr("data-start"), COOKIE_LIFETIME);
			//discount
			setCookie(DISCOUNT_COOKIE_NAME + "page", $dsc.attr("data-discount").replace(',', '.'), COOKIE_LIFETIME);
			//visit timeStamp
			if (getCookie(VISIT_TIME_COOKIE_NAME + "page") == null || getCookie(VISIT_TIME_COOKIE_NAME + "page").isEmpty()) {
				setCookie(VISIT_TIME_COOKIE_NAME + "page", new Date().getTime(), COOKIE_LIFETIME);
			}
		}
		if ($dscDevice.length > 0) {
			var code = $dscDevice.attr("data-code");
			//device name
			setCookie(DEVICE_COOKIE_NAME + code, $dscDevice.text(), COOKIE_LIFETIME);
			//last
			setCookie(DURATION_COOKIE_NAME + code, $dscDevice.attr("data-last"), COOKIE_LIFETIME);
			//start
			setCookie(START_COOKIE_NAME + code, $dscDevice.attr("data-start"), COOKIE_LIFETIME);
			//start
			setCookie(DISCOUNT_COOKIE_NAME + code, $dscDevice.attr("data-discount").replace(',', '.'), COOKIE_LIFETIME);
			//visit timeStamp
			if (getCookie(VISIT_TIME_COOKIE_NAME + code) == null || getCookie(VISIT_TIME_COOKIE_NAME + code).isEmpty()) {
				setCookie(VISIT_TIME_COOKIE_NAME + code, new Date().getTime(), COOKIE_LIFETIME);
			}
		}
		//initialize start times and durations
		for (i = 0; i < keys.length; i++) {
			var key = keys[i];
			var discountValue = getCookie(DISCOUNT_COOKIE_NAME + key) * 1;
			var startValue = getCookie(START_COOKIE_NAME + key) * 1;
			var durationValue = getCookie(DURATION_COOKIE_NAME + key) * 1;
			var visitValue = getCookie(VISIT_TIME_COOKIE_NAME + key) * 1;
			var nameValue = getCookie(DEVICE_COOKIE_NAME + key);
			discount[key] = discountValue;
			start[key] = startValue;
			visit[key] = visitValue;
			duration[key] = durationValue;
			deviceName[key] = nameValue;
			discountUsed[key] = typeof getCookie(USED_COOKIE_NAME + key) == 'string' && getCookie(USED_COOKIE_NAME + key) != "";
			var stop = visitValue;
			stop += startValue;
			stop += durationValue;
			lastStop = (lastStop < stop) ? stop : lastStop;
		}
		// console.log(visit);
		// console.log(discount);
		// console.log(duration);
		// console.log(discountUsed);
		// console.log(lastStop);
	}

	function showWindow() {
		console.log(windowClosed);
		if (windowClosed) {
			$("#discount-popup").remove();
			$("#discount-popup-2").show();
			return;
		}
		windowClosed = getCookie(WINDOW_CLOSED_COOKIE_NAME) == "yes";
		if (!windowClosed) {
			$("#discount-popup").show();
			$("#discount-popup-2").hide();
		}
	}
}

function closeDiscountWindow() {
	setCookie("window_closed", "yes", 60 * 60 * 1000);
	$("#discount-popup-2").show();
	// document.location.reload();
}

function minSec(ms) {
	var s = Math.round(ms / 1000);
	var min = Math.floor(s / 60);
	var sec = s % 60;
	return ((min > 0) ? pad(min) + "мин. " : "") + pad(sec) + "сек.";
}

