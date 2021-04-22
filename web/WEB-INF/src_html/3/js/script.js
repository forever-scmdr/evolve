$(document).ready(function(){
  $('.best-price__slider').slick({
		autoplay: true,
		autoplaySpeed: 3000,
		arrows: false,
  });
  $('.slider').slick({
		autoplay: false,
		autoplaySpeed: 3000,
		arrows: false,
		dots: true,
		appendDots: '.slider-nav',
		fade: true,
  });
});
