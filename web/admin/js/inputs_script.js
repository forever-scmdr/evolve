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
		var now = new Date().toLocaleString("ru").substring(0,17).replace(',', '');
		$(".timeStamp").each(function() {
			var targ = $(this).find(".whole");
			var date = $(this).find(".datepicker");
			var time = $(this).find(".time");
		
			var dv = $(targ).val();
			var tls = (dv == "")? now : dv;
			if(tls.indexOf(".") == -1){
				nd =  new Date(tls*1);
				tls = $.datepicker.formatDate("dd.mm.yy",nd);
				time.val(nd.getHours()+":"+nd.getMinutes());
			}
			date.val(tls.substring(0,10));
			if(dv == "") {
				targ.val(tls);
			}
			if(time.val() == ""){
				time.val("0:0");
			}
			date.change(function() {
				makeVal(targ, date, time);
			});
			time.change(function() {
				validateTime(this);
				makeVal(targ, date, time);
			});
		});
		function validateTime(el) {
			tv = $(el).val().substring(0,5);
			arr = tv.split(':');
			arr[0]=(arr[0]*1 > 23)? 23 : arr[0];
			arr[1]=(arr[1]*1 > 59)? 59 : arr[1];
			arr[0]=(arr[0]*1 < 0)? 0 : arr[0];
			arr[1]=(arr[1]*1 > 0)? 0 : arr[1];
	
			$(el).val(arr.join(":"));
		}
		function makeVal(target, date, time) {
			v = (time.val() == undefined)? $(date).val() : $(date).val()+' '+$(time).val();
			$(target).val(v);
		}
		
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
		function openAssoc(paramId) {
			var url = openAssocUrl + paramId;
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