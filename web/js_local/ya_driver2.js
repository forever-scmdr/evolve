/** * */
if (typeof ymaps != 'undefined') {
	ymaps.ready(init);
}

function init() {
	// Данные о местоположении, определённом по IP
	if (typeof ymaps != 'undefined') {
		var geolocation = ymaps.geolocation;
		var userCoords = coords = [geolocation.latitude, geolocation.longitude];
	
		var seedKey = pickSeedKey(mapSeeds, geolocation.country, coords);
		var seed = mapSeeds[seedKey];
		initFromSeed(seed);
	}
}

function initFromSeed(seed) {

	// build map
	window.myMap = new ymaps.Map('yandex_map', {
		center : seed.center,
		zoom : seed.zoom
	});
	myMap.controls.add('zoomControl', {
		left : 5,
		top : 5
	})
//	.add('typeSelector').add('mapTools', {
//		left : 35,
//		top : 5
//	});

	// add markres
	places = seed.places;
	
	var collection = new ymaps.GeoObjectCollection(null, {
		preset : 'twirl#blueIcon'
	});
	myMap.geoObjects.add(collection);

	for ( var k in places) {
		place = places[k];
		craeteLink(place, collection, myMap);
	}

	function craeteLink(place, collection, map) {
		var mark = new ymaps.Placemark(place.coords, {
			balloonContentHeader : place.header,
			balloonContent : place.body,
			balloonContentFooter : place.footer,
			hintContent : place.header
		}, place.imgAndSettings)

		collection.add(mark);
	}

}

function pickSeedKey(seeds, countryName, coords) {
	var seed;
	i = 0;
	for ( var k in seeds) {
		if (seeds[k].region == countryName) {
			return k;
		}
		i++;
	}
	// Найти ближайший к юзеру регион если в его регионе нет дилера
	d = -1;
	for ( var k in seeds) {
		x = ymaps.coordSystem.geo.getDistance(seeds[k].center, coords);
		if (x < d || d == -1) {
			d = x;
		}
	}
	return k;
}


