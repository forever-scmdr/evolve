$(document).ready(function(){
	generateBirthDates();
	$(".birth-date").find(".years, .months").change(function(){
		disableDays($(this).closest(".birth-date"));
	});
	// $("#pin").keyup(function(e){
	// 	v = $(this).val();
	// 	if(v.length > 3){
	// 		$("#get-pin").hide();
	// 		$("#set-pin").show();
	// 	}else{
	// 		$("#get-pin").show();
	// 		$("#set-pin").hide();
	// 	}
	// });
	// $("#pin").trigger("keyup");
	$.datepicker.setDefaults($.datepicker.regional["ru"]);

	$(".datepicker").each(function(){
		min = $(this).attr("min-date");
		max = $(this).attr("max-date");
		$(this).datepicker({
			 minDate: min
			,maxDate: max
		});

	});

});

function generateBirthDates(){
	now = new Date();
	y = now.getFullYear();
	var years = $("<select>");
	min = y - 121;
	for (var i = 121; i > 0; i--) {
		v = min + i;
		o = $("<option>", {value: v, text: v});
		years.append(o);
	}
	$(".birth-date").find(".years").each(function(){
		if($(this).is("input")){
			$(this).val(y);
		}
		else{
			$(this).find("option").remove();
			$(this).html(years.html());
		}
	});
}
function disableDays(el){
	y = el.find(".years").val();
	m = el.find(".months").val();
	m = el.find(".months").find("option[value='"+m+"']");
	m = el.find(".months").find("option").index(m);
	sel = new Date(y, m, 1);
	diM = sel.daysInMonth();
	days = el.find(".days");
	days.find("option").removeAttr("disabled").css("display","");
	if(diM < 31){
		days.find("option:gt("+(diM-1)+")").attr("disabled", "disabled").css("display", "none");	
		if(days.val() > diM || days.val() == null){
			days.val(diM);
			//days.find("option:eq("+(diM-1)+")").click();	
		}
	}
}


Date.prototype.daysInMonth = function() {
		return 33 - new Date(this.getFullYear(), this.getMonth(), 33).getDate();
};