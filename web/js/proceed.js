$(document).on("change", "#delivery input", function(e){
	var toShow = $(this).closest("label").attr("data-show");
	var $s = $(toShow);
	var $hideable = $(".delivery-hideable");
	$hideable.not($s).hide();
	$s.show();
	$hideable.each(function(){
		if(!$(this).is(":visible")){
			$(this).find(":checked").prop("checked", false);
			$(this).find("input[type='text']").val("");
		}
	});

	$("ul").each(function(){
		if($(this).find("input[type = radio]").length > 0){
			if($(this).find(":checked").length == 0){
				$(this).find("input[type = radio]:visible").eq(0).prop("checked", true);
			}
		}
	});

	var c = $(this).closest("label").attr("data-country").split(',');
	var $sel = $("#country-list").find("option");
	console.log($sel);
	$sel.each(function(){
		$t = $(this);
		if(c.indexOf($t.text()) > -1){
			$t.show();
		}else{
			$t.hide();
			if($("#country-list").val() == $t.text()){
				$("#country-list").val("Беларусь");
			}
		}
	});
});

$(document).ready(function(){
	var $checked = $('#delivery').find(":checked");
	if($checked.length == 0){
		$checked = $('#delivery').find("input").eq(0);
		$checked.prop("checked", true);
	}
	$checked.trigger("change");
});

function myTrim(x) {
    return x.replace(/^\s+|\s+$/gm,'');
}