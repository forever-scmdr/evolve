$(window).load(function(){
	var scrollTop = $(document).scrollTop();
	var wh = $(window).height();

	if(scrollTop > 150){
		
		if(!$(".navbar-fixed-top").is(".scrolled")){

			$(".navbar-fixed-top").addClass("scrolled");
			var $img = $(".navbar-fixed-top").find(".navbar-brand > img");
			var src = $img.attr("src");
			var oldSrc = $img.attr("old-src");
			oldSrc = (isEmpty(oldSrc))? "img/logo_sputnik.jpg" : oldSrc;

			$img.attr({"src" : oldSrc, "old-src" : src});
		}	
	}
	$(".digits").each(function(){
		if($(this).is(".spincrement")){
			return;
		}
		var t = $(this).offset().top + 0.5*$(this).height();
		var scrollTop = $(document).scrollTop();
		if(scrollTop > t){

			$(this).spincrement({
				from: 0.0,
				decimalPlaces: 0,
				duration: 1500,
				thousandSeparator: ' '
			});
			$(this).addClass("spincrement");
		}
	});
	
});
$(window).scroll(function(e){
	var wh = $(window).height();
	var lim = 150;
	if($(document).height() > wh){
		var scrollTop = $(document).scrollTop();
		if(scrollTop > 150){
			if (!$(".navbar-fixed-top").is(".scrolled")) {
				$(".navbar-fixed-top").addClass("scrolled");
				var $img = $(".navbar-fixed-top").find(".navbar-brand > img");
				var src = $img.attr("src");
				var oldSrc = $img.attr("old-src");
				oldSrc = (isEmpty(oldSrc))? "img/logo_sputnik.jpg" : oldSrc;

				$img.attr({"src" : oldSrc, "old-src" : src});
			}
			
			$(".digits").each(function(i){
				if($(this).is(".spincrement")){
					return;
				}
				var t = $(this).offset().top + 0.5*$(this).height();
				if(scrollTop+wh > t){

					$(this).spincrement({
						from: 0,
						decimalPlaces: 0,
						duration: 1500,
						thousandSeparator: ' '
					});
					$(this).addClass("spincrement");
				}
			});
		}
		else{
			if($(".navbar-fixed-top").is(".scrolled")){
				var $img = $(".navbar-fixed-top").find(".navbar-brand > img");
				var src = $img.attr("src");
				var oldSrc = $img.attr("old-src");
				oldSrc = (isEmpty(oldSrc))? "img/logo.png" : oldSrc;

				$img.attr({"src" : oldSrc, "old-src" : src});
				$(".navbar-fixed-top").removeClass("scrolled");
			}		
		}
	}
});

function isEmpty(x){
 return typeof x === "undefined" || x == null || x == "";
}