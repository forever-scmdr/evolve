/**
 * Created by user on 13.03.2019.
 */
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
    const DURATION_COOKIE_NAME = "duration_";
    const START_COOKIE_NAME = "start_";
    const WINDOW_CLOSED_COOKIE_NAME = "window_closed";

    var $dsc = $("#dsc-data");
    var $dscDevice = $("#dsc-device");
    var now = new Date().getTime();
    var pagesVisitedCookie = getCookie("visited");
    var windowClosedCookie = getCookie("window_closed");
    var keys = [];
    var discount = {};
    var start = {};
    var duration = {};
    var lastStop = now + 10000;
    var discountUsed = {};
    var time = now;

    initVisited();


    step();

    function step() {
        console.log("SNAFU_" + ((time - now) / 1000));
        if (typeof discountTimeout != "undefined") {
            clearTimeout(discountTimeout);
        }
        if (time < lastStop) {
            discountTimeout = setTimeout(step, 1000);
        } else {
            deleteCookie("window_closed");
            deleteCookie("discount_active");
            //setTimeout(function(){document.location.reload();},1000);
        }
        time += 1000;
    }

    function initVisited() {
        var vis;
        //ensure main cookie
        if (pagesVisitedCookie == null || typeof pagesVisitedCookie == "undefined" || pagesVisitedCookie == "") {
            var visitedPage = new Array();
            if ($dsc.length > 0) {
                visitedPage.push("page");
            }
            if ($dscDevice.length > 0) {
                visitedPage.push($dscDevice.attr("data-code"));
            }
            vis = visitedPage;
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
            vis = currentValue;
            setCookie(VISITED_COOKIE_NAME, currentValue.join(','), COOKIE_LIFETIME);
        }
        //set individual cookies
        if ($dsc.length > 0) {
            currentValue.push("page");
            //last
            setCookie(DURATION_COOKIE_NAME+"page", $dsc.attr("data-last") ,COOKIE_LIFETIME);
            //start
            setCookie(START_COOKIE_NAME+"page", $dsc.attr("data-start") ,COOKIE_LIFETIME);
        }
        if ($dscDevice.length > 0) {
            var code = $dscDevice.attr("data-code");
            //last
            setCookie(DURATION_COOKIE_NAME  + code, $dsc.attr("data-last") ,COOKIE_LIFETIME);
            //start
            setCookie(START_COOKIE_NAME + code, $dsc.attr("data-start") ,COOKIE_LIFETIME);
        }
        //

    }
}

function closeDiscountWindow() {
    setCookie("window_closed", "yes", 60 * 60 * 1000);
    document.location.reload();
}

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

function pad(num) {
    var s = num + "";
    while (s.length < 2) s = "0" + s;
    return s;
}