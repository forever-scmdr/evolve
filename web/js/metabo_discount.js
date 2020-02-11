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
    const DURATION_COOKIE_NAME = "duration_";
    const START_COOKIE_NAME = "start_";
    const VISIT_TIME_COOKIE_NAME = "visit_";
    const USED_COOKIE_NAME = "used_";
    const WINDOW_CLOSED_COOKIE_NAME = "window_closed";

    var $dsc = $("#dsc-data");
    var $dscDevice = $("#dsc-device");
    var now = new Date().getTime();
    var pagesVisitedCookie = getCookie(VISITED_COOKIE_NAME);
    var windowClosedCookie = getCookie(WINDOW_CLOSED_COOKIE_NAME);
    var keys = [];
    var discount = {};
    var visit = {};
    var start = {};
    var duration = {};
    var lastStop = now + 10000;
    var discountUsed = {};
    var time = now;

    initVisited();
    step();

    function step() {
        if (typeof discountTimeout != "undefined") {
            clearTimeout(discountTimeout);
        }
        if (time < lastStop) {
            discountTimeout = setTimeout(step, 1000);
            for(var i=0; i< keys.length; i++){
                var v = visit[keys[i]];
                var s = start[keys[i]];
                var d = duration[keys[i]];
                var passed = time - v;
                //console.log(keys[i]+". passed: "+passed/1000+ "; start: "+ s/1000);
                if(passed > s && passed < d){
                    console.log(keys[i]+" active");
                }else if(passed >= d){
                    console.log(keys[i]+" expired");
                }else{
                    console.log(keys[i]+" left: "+(s-passed));
                }
            }
        } else {
            deleteCookie("window_closed");
            deleteCookie("discount_active");
            setTimeout(function(){document.location.reload();},1000);
        }
        time += 1000;
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
            visitedPage =  [...new Set(visitedPage)];
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
            //last
            setCookie(DURATION_COOKIE_NAME + "page", $dsc.attr("data-last"), COOKIE_LIFETIME);
            //start
            setCookie(START_COOKIE_NAME + "page", $dsc.attr("data-start"), COOKIE_LIFETIME);
            //discount
            setCookie(DISCOUNT_COOKIE_NAME + "page", $dsc.attr("data-discount").replace(',','.'), COOKIE_LIFETIME);
            //visit timeStamp
            if(getCookie(VISIT_TIME_COOKIE_NAME + "page") == null || getCookie(VISIT_TIME_COOKIE_NAME + "page").isEmpty()){
                setCookie(VISIT_TIME_COOKIE_NAME + "page", new Date().getTime(), COOKIE_LIFETIME);
            }
        }
        if ($dscDevice.length > 0) {
            var code = $dscDevice.attr("data-code");
            //last
            setCookie(DURATION_COOKIE_NAME + code, $dscDevice.attr("data-last"), COOKIE_LIFETIME);
            //start
            setCookie(START_COOKIE_NAME + code, $dscDevice.attr("data-start"), COOKIE_LIFETIME);
            //start
            setCookie(DISCOUNT_COOKIE_NAME + code, $dscDevice.attr("data-discount").replace(',','.'), COOKIE_LIFETIME);
            //visit timeStamp
            if(getCookie(VISIT_TIME_COOKIE_NAME + code) == null || getCookie(VISIT_TIME_COOKIE_NAME + code).isEmpty()){ 
                setCookie(VISIT_TIME_COOKIE_NAME + code, new Date().getTime(), COOKIE_LIFETIME);
            }
        }
        //initialize start times and durations
        for (i = 0; i < keys.length; i++) {
            var key = keys[i];
            var discountValue = getCookie(DISCOUNT_COOKIE_NAME + key)*1;
            var startValue = getCookie(START_COOKIE_NAME + key)*1;
            var durationValue = getCookie(DURATION_COOKIE_NAME + key)*1;
            var visitValue = getCookie(VISIT_TIME_COOKIE_NAME + key)*1;
            discount[key] = discountValue;
            start[key] = startValue;
            visit[key] = visitValue;
            duration[key] = durationValue;
            discountUsed[key] = typeof getCookie(USED_COOKIE_NAME + key) == 'string' && getCookie(USED_COOKIE_NAME + key) != "";
            var stop = visitValue;
            stop += startValue;
            stop += durationValue;
            lastStop = (lastStop < stop)? stop : lastStop;
        }
        // console.log(visit);
        // console.log(discount);
        // console.log(duration);
        // console.log(discountUsed);
        // console.log(lastStop);
    }
}

function closeDiscountWindow() {
    setCookie("window_closed", "yes", 60 * 60 * 1000);
    document.location.reload();
}

