$(document).ready(function(){
  $('.best-price__slider').slick({
		autoplay: true,
		autoplaySpeed: 3000,
		arrows: false,
  });
  $('.slider').slick({
		autoplay: true,
		autoplaySpeed: $(".slider").attr("timeout") * 1000,
		arrows: false,
		dots: true,
		appendDots: '.slider-nav',
		fade: true,
  });
  $('select[value]').each(function () {
	  $(this).val($(this).attr('value'));
  });
});
