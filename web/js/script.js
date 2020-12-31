$(document).ready(function(){
	$('.slider').slick( {
		arrows:false,
		dots: true,
		appendDots:$('.slider-nav'),
		autoplay: false,
		autoplaySpeed: 3000
	});
	$('.device-carousel').slick( {
		arrows:false,
		dots: true,
		appendDots:$('.device-nav'),
		// autoplay: false,
		// autoplaySpeed: 3000
		slidesToShow: 5,
		slidesToScroll: 5,
		responsive:[
			{
				breakpoint: 1024,
				settings: {
					slidesToShow: 3,
					slidesToScroll: 3
				}
			},
			{
				breakpoint: 768,
				settings: {
					slidesToShow: 1,
					slidesToScroll: 1
				}
			},
		]
	});
	$('.device-carousel-similar').slick( {
		arrows:false,
		dots: true,
		appendDots:$('.device-nav-similar'),
		// autoplay: false,
		// autoplaySpeed: 3000
		slidesToShow: 4,
		slidesToScroll: 4,
		responsive:[
			{
				breakpoint: 1024,
				settings: {
					slidesToShow: 3,
					slidesToScroll: 3
				}
			},
			{
				breakpoint: 768,
				settings: {
					slidesToShow: 1,
					slidesToScroll: 1
				}
			},
		]
	});
	$('.device-carousel-colors').slick( {
		arrows:false,
		dots: true,
		appendDots:$('.device-nav-colors'),
		// autoplay: false,
		// autoplaySpeed: 3000
		slidesToShow: 8,
		slidesToScroll: 8,
		responsive:[
			{
				breakpoint: 1024,
				settings: {
					slidesToShow: 8,
					slidesToScroll: 8
				}
			},
			{
				breakpoint: 768,
				settings: {
					slidesToShow: 3,
					slidesToScroll: 3
				}
			},
		]
	});
})



