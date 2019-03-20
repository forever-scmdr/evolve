/**
 * Created by user on 13.03.2019.
 */
discount();
Number.prototype.pad = function(size) {
    var s = String(this);
    while (s.length < (size || 2)) {s = "0" + s;}
    return s;
}
function discount() {
    var $data = $("#dsc-data");
    var now = $data.attr("data-now")*1;
    var start = $data.attr("data-start")*1;
    var expires = $data.attr("data-expires")*1;
    var showTime = $data.attr("data-show")*1;
    var discountUsed = getCookie("discount_used") != null;
    var time = now;
    var fontSize =  $(".price-highlight").css("font-size");
    var lineHeight =  $(".price-highlight").css("line-height");
    var state = false;

    var windowClosed = getCookie("window_closed") == "yes";

    step();
   // $(".price-highlight, .price").css({"line-height" : lineHeight});

    function step() {
       // console.log(getCookie(showTime));

        if(discountUsed)return;
        if(time < expires) {
            if(typeof timeout != "undefined"){clearTimeout(timeout);}
            var timeout = setTimeout(step, 1000);
        }else{
            clearTimeout(timeout);
            $("#dsc-data").remove();
            deleteCookie("window_closed");
            deleteCookie("discount_active");
        }
        // console.log(time - start);
        var delta = expires - time;
        if(!windowClosed && time > showTime){
            activateDiscount();
            showWindow();
        }
        time += 1000;
        setCookie("current_time", time+"", 24*60*60*1000);
       // bounceFont();
        var secondsFromNow = Math.round((delta)/1000);
        var min = Math.floor(secondsFromNow/60);
        var seconds = secondsFromNow % 60;
        //console.log(secondsFromNow);
        var t = ((min > 0)? pad(min)+"мин. " : "")+pad(seconds)+"сек.";
        $("#dsc-timer-1").text(t);
        $("#dsc-timer-2").text(t);
    }
    function activateDiscount() {
        setCookie("discount_active", "yes",expires-now);
    }
    function showWindow() {
        if(windowClosed)return;
        windowClosed = getCookie("window_closed") == "yes";
        if(!windowClosed) $("#discount-popup").show();
    }
    function bounceFont() {
        if(state){
            $(".price-highlight").css({"font-size": (parseFloat(fontSize)+1) +"px"});
        }else{
            $(".price-highlight").css({"font-size": fontSize});
        }
        state = !state;
    }

}
function closeDiscountWindow() {
    setCookie("window_closed","yes", 60*60*1000);
    document.location.reload();
    // $("#discount-popup").hide();
    // $("#discount-popup-2").show();
}

function setCookie(c_name, value, exMs) {
    var ms = new Date().getTime() + exMs;
    var exdate = new Date(ms);
    var c_value = escape(value)	+ ((exMs == null) ? "" : "; expires=" + exdate.toUTCString());
    document.cookie = c_name + "=" + c_value + "; path=/;";
}
function deleteCookie(c_name){setCookie(c_name, '', -1*24*60*60*1000);}
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
    var s = num+"";
    while (s.length < 2) s = "0" + s;
    return s;
}