<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:f="f:f" version="2.0">
	<xsl:import href="common_page_base.xsl"/>
	<xsl:output method="html" encoding="UTF-8" media-type="text/xhtml" indent="yes" omit-xml-declaration="yes"/>
	<xsl:strip-space elements="*"/>

	<xsl:variable name="title" select="'Документация'" />


	<xsl:template name="CONTENT">
		<!-- CONTENT BEGIN -->
		<div class="path-container">
			<div class="path">
				<a href="{$main_host}">Главная страница</a> &gt;
			</div>
			<xsl:call-template name="PRINT"/>
		</div>
		<h1>Документация</h1>

		<div class="page-content m-t">
			<h3>Расположение дилеров на карте</h3>
			<div class="map-container">
				<div id="map" style="width:100%; height:500px;"></div>
				<script src="//api-maps.yandex.ru/2.1/?lang=ru_RU" type="text/javascript"/>
				<script type="text/javascript">
					ymaps.ready(init);
					function init() {
					ymaps.geocode('Минск', {results: 1}).then(function(res) {
					var minsk = res.geoObjects.get(0);
					var coords = minsk.geometry.getCoordinates();
					var myMap = new ymaps.Map('map', {
					center: coords,
					zoom: 7
					});
					<xsl:for-each select="page/dealer_coords">
						ymaps.geocode('<xsl:value-of select="address"/>', {results: 1}).then(function(res) {
						var dealer = res.geoObjects.get(0);
						dealer.properties.set('balloonContentBody', '<xsl:value-of select="replace(info, '\n', '')" disable-output-escaping="yes"/>');
						dealer.properties.set('balloonContentHeader', '<xsl:value-of select="name"/>');
						dealer.properties.set('balloonContentFooter', '<xsl:value-of select="address"/>');
						myMap.geoObjects.add(dealer);
						});
					</xsl:for-each>
					});
					}
				</script>
			</div>
			<h3>Список дилеров</h3>
			<div class="catalog-items info dealers">
				<xsl:for-each select="page/dealer_coords">
					<div class="catalog-item">
						<div class="text">
							<h4><xsl:value-of select="name" /></h4>
							<p><xsl:value-of select="address"/></p>
							<xsl:value-of select="info" disable-output-escaping="yes"/>
						</div>
					</div>
				</xsl:for-each>
			</div>
		</div>

		<xsl:call-template name="ACTIONS_MOBILE"/>
	</xsl:template>

</xsl:stylesheet>