$(document)
		.ready(
				function() {

					$("#td-f").width($("#main-menu").width() + 200.5);
					// -- всплавающие окошки
					$('span.javascript')
							.click(
									function() {
										toShow = $(this).next('.hidden');
										toShow = ($(this).next('.hidden').length == 0) ? $(
												this).parent().next('.hidden')
												: toShow;
										$('body').unbind('click');
										$(toShow).toggle(100, function() {
											$('body').click(function() {
												$('.hidden').hide();
												$('body').unbind('click');
											});
											$('.hidden').hover(function() {
												$('body').unbind('click');
											}, function() {
												$('body').click(function() {
													$('.hidden').hide();
													$('body').unbind('click');
												});
											});
										});
										nad = $('.close2').nextAll('div');
										
									});
					
					$('.close').click(function() {
						$(this).parent('div').fadeOut('slow');
					})
					$('#mail').click(
							function() {
								$('.hidden2.mail').toggle();
								$('.hidden2.mail').css(
										'margin-left',
										'-' + ($('.hidden2.mail').width() / 2)
												+ 'px');
							});
					$('#calendar').click(function() {
						displayDatePicker('data', false, 'dmy', '.');
						$('.dpDiv:eq(0), #datepickeriframe').css({
							left : $('#calendar').position().left - 40,
							top : 72
						});
					});
					$(".toggle").click(function(e) {
						e.preventDefault();
						$($(this).attr("href")).toggle();
					});

					$('.close2').click(function() {
						$(this).parents('.frame').fadeOut(1000, function() {
							$('.page').css({
								height : ''
							});
						});
					});

				});

$(document).on("click", "#sbmt", function(e) {
	e.preventDefault();
});
function setDays(q, intervals){
	q = $(q);
	v = parseInt(q.val());
	
	$("#sel_denom").val(v);
	var selector = ".pi";
	if(typeof intervals != "string"){
		pref = "";
		for(i = 0; i<intervals.length; i++ ){
			pref += "td[price='"+intervals[i]+"'] "+selector;
			if(i<intervals.length-1){pref += ", ";}
		}
		selector = pref;		
	}
	else{
		selector = "td[price='"+intervals+"'] "+selector;
	}
	q.closest("table").find(selector).each(function(){
		alert(parseFloat($(this).attr("price"));
		p = Math.round(parseFloat($(this).attr("price"))*v*100)/100;
		$(this).text(p+"".replace(".",","));
	});
}
