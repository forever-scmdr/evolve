require.config({
	paths : {
		// SVG
		"d3" : "d3.min"
		,"c3" : "c3"
	}
	, waitSeconds : 25
});

var createChart = null;

require([ "d3", "c3" ], function(d3, c3) {

	createChart = function(button, justTotal) {
		var table = $(button).closest('.chart_button_container').prev().find('table').get(0);
		var chart = $(button).closest('.chart_button_container').next().get(0);
		var dataPointSelector = '.chart_data_point';
		if (justTotal)
			dataPointSelector += '.total';
		var columns = new Array();
		var categories = new Array();
		$(table).find('.chart_data_x').each(function () {
			categories.push($(this).text());
		});
		$(table).find('.chart_data_line').each(function () {
			var points = new Array($(this).find('.chart_data_header').text());
			$(this).find(dataPointSelector).each(function () {
				points.push($(this).attr('value') * 1);
			});
			columns.push(points);
		});

		var chartType = 'all';
		if (justTotal)
			chartType = 'total';
		if ($(chart).attr('type') == chartType)
			$(chart).toggle();
		else
			$(chart).show();
		$(chart).attr('type', chartType);

		c3.generate({
			bindto : chart,
			data: {
				columns: columns
			},
			axis: {
				x: {
					type: 'category',
					categories: categories
				}
			},
			// zoom : {
			// 	enabled : true
			// },
		});

	};

	/*
	for (var i = 0; i < chartData.charts.length; i++) {

		//var w = d3.select(chartData.element_ids[i]).node().offsetWidth;
		//var h = d3.select(chartData.element_ids[i]).node().offsetHeight;

		//alert(chartData.charts[i].columns[1]);

		var chart = c3.generate({
			bindto : chartData.element_ids[i],

			data : chartData.charts[i],
			axis : {
				x : {
					type : 'category',//'timeseries',
					// tick : {
					// 	format : chartData.formats[i]
					// }

				},
				y : {
					label : chartData.units[i],
			        // tick: {
			        //     format: d3.format('.2f')
			        // }
				}
			},
			zoom : {
				enabled : true
			},

			// size : {
			// 	height : h-20,
			// 	width : w-25
			// }

		});
	}
	*/
});

