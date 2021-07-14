/*
**	URI: http://www.howtomake.com.ua/2012/stilizaciya-vsex-elementov-form-s-pomoshhyu-css-i-jquery.html 
*	
*/

$(document).on("click", ".text_ipt", function(){
	$(this).hide();
	$(this).closest(".file-load-block").find(".url").show();
	$(this).closest(".file-load-block").find(".url").focus();
});

$(document).on("change", "input.file", function(){
	//alert("change");
	tp = $(this).parents(".file-load-block");
	// Если файл прикрепили то заносим значение value в переменную
	var fileResult = $(this).val();
	// И дальше передаем значение в инпут который под загрузчиком
	tp.find('.text_ipt').val(fileResult);
	tp.find('.text_ipt').show();
	tp.find(".url").hide();
	tp.find(".url").val("");
	tp.addClass("upload").removeClass("url");
});

$(document).on("change", ".url", function(){
	//alert("url change");
	tp = $(this).parent();
	// Если файл прикрепили то заносим значение value в переменную
	var fileResult = $(this).val();
	tp.find('.text_ipt, .file').val("");
	tp.addClass("upload").removeClass("url");
	$(this).css({width: 190, position: "relative", zIndex: 10});
});

$(document).on("blur", ".url, .text_ipt", function(){
	$(this).css({width: "", position: "", zIndex: ""});
});
$(document).on("focus", ".url, .text_ipt", function(){
	$(this).css({width: 190, position: "relative", zIndex: 10});
});

function isValidUrl(url)
{
  var objRE = /(^https?:\/\/)?[a-zA-Zа-яА-Я0-9~_\-\.]+\.[a-zA-Zа-яА-Я]{2,9}(\/|:|\?[!-~]*)?$/i;
  return objRE.test(url);
}

		$.datepicker.setDefaults($.datepicker.regional["ru"]);
		$(".datepicker").datepicker();
		
		$('.date-time').on('change', function () {
			var $cnt = $(this).closest('.timeStamp');
			var $date = $cnt.find('.datepicker');
			var $time = $cnt.find('.time');
			var $whole = $cnt.find('.whole');
			var arr = $time.val().split(':');
			var d = $.datepicker.parseDate("dd.mm.yy", $date.val());
			d.setHours(parseInt(arr[0]));
			d.setMinutes(parseInt(arr[1]));
			var wholeVal = d.toLocaleString("ru", {timeZone : "UTC" }).substring(0,17).replace(',', '');;
			$whole.val(wholeVal);
			});

		
		// Открытие окна редактирования фильтра
		function openFilter(filterId, itemId, paramId) {
			var url = "admin_filter_init.afilter?input=" + filterId + "&itemId=" + itemId + "&paramName=" + paramId;
			var winW = 630, winH = 460;
			if (document.body && document.body.offsetWidth) {
				winW = document.body.offsetWidth;
				winH = document.body.offsetHeight;
			}
			if (document.compatMode=='CSS1Compat' &&
				document.documentElement &&
				document.documentElement.offsetWidth ) {
				winW = document.documentElement.offsetWidth;
				winH = document.documentElement.offsetHeight;
			}
			if (window.innerWidth && window.innerHeight) {
				winW = window.innerWidth;
				winH = window.innerHeight;
			}
			var w = winW - 300;
			var h = winH - 100;
			var x = 150;
			var y = 50;
			window.open(
					url, 
					"Filter", 
					"toolbar=no,scrollbars=yes,menubar=no,status=no,directories=no,width=" + w + ",height=" + h + ",left=" + x + ",top=" + y).focus();
		}
		
		// Открытие окна редактирования ассоциированных элементов
		function openAssoc(url) {
			var winW = 630, winH = 460;
			if (document.body && document.body.offsetWidth) {
				winW = document.body.offsetWidth;
				winH = document.body.offsetHeight;
			}
			if (document.compatMode=='CSS1Compat' &&
				document.documentElement &&
				document.documentElement.offsetWidth ) {
				winW = document.documentElement.offsetWidth;
				winH = document.documentElement.offsetHeight;
			}
			if (window.innerWidth && window.innerHeight) {
				winW = window.innerWidth;
				winH = window.innerHeight;
			}
			var w = winW - 300;
			var MAX_W = 800;
			w = w < MAX_W ? w : MAX_W;
			var h = winH - 100;
			var x = (winW - w) / 2;
			var y = 50;
			window.open(
					url, 
					"Associated", 
					"toolbar=no,scrollbars=yes,menubar=no,status=no,directories=no,width=" + w + ",height=" + h + ",left=" + x + ",top=" + y).focus();
		}