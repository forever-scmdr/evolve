$(document).ready(function(){
	$('.slider').slick( {
		arrows:false,
		dots: false,
		autoplay: true,
		autoplaySpeed: 3000
	});
	$('.device-carousel').slick( {
		arrows:true,
		dots: false,
		// appendDots:$('.device-nav'),
		// autoplay: false,
		// autoplaySpeed: 3000
		slidesToShow: 5,
		slidesToScroll: 5,
		responsive:[
			{
				breakpoint: 1280,
				settings: {
					slidesToShow: 4,
					slidesToScroll: 4
				}
			},
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
					arrows: false,
					slidesToShow: 2,
					slidesToScroll: 2
				}
			},
			{
				breakpoint: 375,
				settings: {
					arrows: false,
					slidesToShow: 1,
					slidesToScroll: 1
				}
			},
		]
	});
	$('.similar-carousel').slick( {
		arrows:true,
		dots: false,
		// appendDots:$('.device-nav'),
		// autoplay: false,
		// autoplaySpeed: 3000
		slidesToShow: 4,
		slidesToScroll: 1,
		responsive:[
			{
				breakpoint: 1025,
				settings: {
					slidesToShow: 3,
					slidesToScroll: 3
				}
			},
			{
				breakpoint: 769,
				settings: {
					slidesToShow: 2,
					slidesToScroll: 2
				}
			},
		]
	});
	$('.brands-block__wrap').slick( {
		arrows:false,
		dots: false,
		// appendDots:$('.brands-nav'),
		autoplay: true,
		autoplaySpeed: 3000,
		slidesToShow: 9,
		slidesToScroll: 1,
		responsive:[
			{
				breakpoint: 1024,
				settings: {
					slidesToShow: 5,
					slidesToScroll: 1
				}
			},
			{
				breakpoint: 768,
				settings: {
					slidesToShow: 3,
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
