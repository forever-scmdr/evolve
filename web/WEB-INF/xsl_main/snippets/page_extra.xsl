<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:f="f:f" version="2.0">
	<xsl:import href="../utils/utils.xsl"/>
	<xsl:import href="custom_blocks.xsl" />

	
	<xsl:template match="*" mode="content">
		<xsl:value-of select="text" disable-output-escaping="yes"/>
		<xsl:apply-templates select="*[@type != 'custom_page']" mode="content"/>
	</xsl:template>

	<xsl:template match="map_part" mode="content">

		<xsl:variable name="point" select="current()//point"/>
		<xsl:variable name="type_id" select="1828"/>
		<xsl:variable name="center" select="concat('[',center,']')"/>
		<xsl:variable name="zoom" select="zoom"/>
		<xsl:variable name="map_id" select="concat('map-', @id)"/>

		<h2><xsl:value-of select="name"/></h2>

		<xsl:if test="region">
			<div class="region-links">
				<a class="region-link active" data-center="{center}" data-zoom="{$zoom}">Все</a>
				<xsl:for-each select="region">
					<span style="padding: 0 0.7rem;">|</span>
					<a class="region-link" data-center="{center}" data-zoom="{zoom}" data-region="{@id}" id="region-link-{@id}">
						<xsl:value-of select="name" />
					</a>
				</xsl:for-each>
			</div>
		</xsl:if>
		<div id="{$map_id}" style="width:100%; height:500px;">
			<script type="text/javascript">

				function parseCoordinates(c){
					var re = /,\s*/;
					var arr = c.split(re);
					var c0 = parseFloat(arr[0]);
					var c1 = parseFloat(arr[1]);
					return [c0,c1];
				}

				var placemarks = {
				"type": "FeatureCollection",
				features :[
				<xsl:for-each select="$point[coords != '']">
					<xsl:if test="position() != 1">,</xsl:if>
					{
					"type": "Feature"
					,"id" : <xsl:value-of select="@id"/>
					,"geometry":{"type" : "Point","coordinates": <xsl:value-of select="concat('[',coords, ']')"/>}
					,"properties" : {
					'balloonContentHeader': '<xsl:value-of select="name"/>'
					,'balloonContentBody': '<xsl:value-of select="replace(info, '\n', '')" />'
					,'balloonContentFooter': '<xsl:value-of select="address"/>'
					,"regionId" : <xsl:value-of select="../@id"/>
					}
					}
				</xsl:for-each>
				]
				};

				ymaps.ready(init);
				var mapId = '<xsl:value-of select="concat('map-', @id)"/>';

				var missing = [
				<xsl:for-each select="$point[not(coords != '')]">
					<xsl:if test="position() != 1">,</xsl:if>
					{
					"link" : '<xsl:value-of select="concat(/page/base, '/admin_set_item.action?itemId=', @id, '&amp;itemType=', $type_id)"/>'
					,"address" : '<xsl:value-of select="address"/>'
					}
				</xsl:for-each>
				];


				function init() {
				var objectManager = new ymaps.ObjectManager({
				clusterize: true,
				gridSize: 32,
				clusterDisableClickZoom: false
				});
				objectManager.clusters.options.set('preset', 'islands#blueClusterIcons');
				objectManager.objects.options.set('preset', 'islands#blueIcon');

				var yMap = new ymaps.Map(mapId,{
				center: <xsl:value-of select="$center"/>,
				zoom: <xsl:value-of select="$zoom"/>
				},{searchControlProvider: 'yandex#search'});

				var links = document.getElementsByClassName('region-link');
				var events = ['click'];
				for(i =0; i &lt; links.length; i++){
					ymaps.domEvent.manager.group(links[i]).add(events,
						function (event) {
							var a = event.get("target");
							var zoom = a.getAttribute("data-zoom") * 1;
							var coordinates = parseCoordinates(a.getAttribute("data-center"));
							yMap.setCenter(coordinates, zoom, {checkZoomRange: true});
							a.classList.add("active");
							for(i = 0; i &lt; links.length; i++){
								if(links[i] == a) continue;
								links[i].classList.remove("active");
							}
					});
				}
				<xsl:if test="f:num(show_places) = 1">

				var pointLinks = document.getElementsByClassName('point-pointer');
				for(i =0; i &lt; pointLinks.length; i++){
					ymaps.domEvent.manager.group(pointLinks[i]).add(events, function (event){
						var a = event.get("target");
						var coordinates = parseCoordinates(a.getAttribute("data-coordinates"));
						yMap.setZoom(13.5);
						yMap.panTo(coordinates);

					});
				}
				</xsl:if>
				objectManager.add(placemarks);
				yMap.geoObjects.add(objectManager);

				for (i = 0; i &lt; missing.length; i++) {
				if(i == 0 &amp;&amp; missing.length &gt; 0){
				console.log(" ");
				console.log("MISSING COORDINATES LIST:");
				}
				var addr = missing[i].address;
				var link = missing[i].link;
				ymaps.geocode(addr,{results: 1}).then(function(res){

				var firstGeoObject = res.geoObjects.get(0);
				var coords = firstGeoObject.geometry.getCoordinates();
				console.log(addr);
				console.log(link);
				console.log(coords[0] + ',' + coords[1]);
				});
				}
				};
			</script>
		</div>

		<xsl:if test="f:num(show_places) = 1">
			<xsl:if test="point">
				<div class="map-points-list">
					<xsl:for-each select="point">
						<xsl:apply-templates select=".">
							<xsl:with-param name="map_id" select="$map_id" />
						</xsl:apply-templates>
					</xsl:for-each>
				</div>
			</xsl:if>
				<xsl:for-each select="region">
					<div class="title title_2">
						<a onclick="$(document).scrollTop($('#{$map_id}').offset().top - 100);" class="region-link" data-center="{center}" data-zoom="{zoom}" data-region="{@id}">
							<xsl:value-of select="name"/>
						</a>
					</div>
					<div class="map-points-list">
						<xsl:apply-templates select="point">
							<xsl:with-param name="map_id" select="$map_id" />
						</xsl:apply-templates>
					</div>
				</xsl:for-each>

		</xsl:if>

	</xsl:template>

	<xsl:template match="point">

		<xsl:param name="map_id"/>

		<div class="point">
			<xsl:if test="coords != ''">
				<a class="point-pointer" data-coordinates="{coords}" onclick="$(document).scrollTop($('#{$map_id}').offset().top - 100);">
					<xsl:value-of select="name"/>
				</a>
			</xsl:if>
			<xsl:if test="not(coords != '')">
				<a><xsl:value-of select="name"/></a>
			</xsl:if>
			<xsl:if test="info | address" >
				<div class="text">
					<xsl:if test="info" >
						<p><xsl:value-of select="info"/></p>
					</xsl:if>
					<xsl:if test="address" >
						<p><xsl:value-of select="address"/></p>
					</xsl:if>
				</div>
			</xsl:if>
		</div>
	</xsl:template>

	<xsl:template match="advanced_spoiler" mode="content">
		<div class="advanced-spoiler single">
			<div class="spoiler__title"><strong><xsl:value-of select="header"/></strong></div>
			<div class="spoiler__content" style="display: none;">
				<xsl:value-of select="text" disable-output-escaping="yes"/>
				<xsl:apply-templates select="*" mode="content"/>
			</div>
		</div>
	</xsl:template>

	<xsl:template match="page_text" mode="content">
		<xsl:if test="f:num(spoiler) &gt; 0">
			<div><a class="toggle" href="#spoiler-{@id}" rel="Свернуть ↑">Подробнее ↓</a></div>
		</xsl:if>
		<div id="spoiler-{@id}" style="{if(f:num(spoiler) &gt; 0) then 'display: none;' else ''}">
			<xsl:value-of select="text" disable-output-escaping="yes"/>
		</div>
	</xsl:template>

	<xsl:template match="page_extra_code" mode="content">
		<xsl:if test="f:num(spoiler) &gt; 0">
			<div><a class="toggle" href="#spoiler-{@id}" rel="Свернуть ↑">Подробнее ↓</a></div>
		</xsl:if>
		<div id="spoiler-{@id}" style="{if(f:num(spoiler) &gt; 0) then 'display: none;' else ''}">
			<xsl:value-of select="text" disable-output-escaping="yes"/>
		</div>
	</xsl:template>

	<xsl:template match="common_gallery" mode="content">
		<xsl:if test="f:num(spoiler) &gt; 0">
			<div><a class="toggle" href="#spoiler-{@id}"  onclick="initNanoCommon{@id}(); return false;" rel="Скрыть галерею ↑">Галерея ↓</a></div>
		</xsl:if>
		<div id="spoiler-{@id}" style="{if(f:num(spoiler) &gt; 0) then 'display: none;' else ''}">
			<div id="nanogallery{@id}">
				<script>
					<xsl:if test="f:num(spoiler) = 0">
						$(document).ready(function(){
							initNanoCommon<xsl:value-of select="@id"/>();
						});
					</xsl:if>
					function initNanoCommon<xsl:value-of select="@id"/>() {
						<xsl:if test="f:num(spoiler) &gt; 0">
						if(!$("<xsl:value-of select="concat('#nanogallery', @id)"/>").is(":visible")){
						</xsl:if>
							$("#nanogallery<xsl:value-of select="@id"/>").nanogallery2( {
								// ### gallery settings ###
								thumbnailHeight:  <xsl:value-of select="height"/>,
								thumbnailWidth:   <xsl:value-of select="width"/>,
								thumbnailBorderHorizontal :   <xsl:value-of select="border"/>,
								thumbnailBorderVertical :   <xsl:value-of select="border"/>,
								thumbnailGutterWidth :   <xsl:value-of select="gutter"/>,
								thumbnailGutterHeight :   <xsl:value-of select="gutter"/>,
								viewerToolbar: { display: false },
								galleryLastRowFull:  false,

								// ### gallery content ###
								items: [
								<xsl:for-each select="picture">
									{
										src: '<xsl:value-of select="concat(@path, pic)"/>',
										srct: '<xsl:value-of select="concat(@path, pic)"/>',
										title: '<xsl:value-of select="header"/>'
									},
								</xsl:for-each>
								]
							});
						<xsl:if test="f:num(spoiler) &gt; 0">
						}
						</xsl:if>
					}
				</script>
			</div>
		</div>
	</xsl:template>


	<xsl:template match="simple_gallery" mode="content">
		<xsl:if test="f:num(spoiler) &gt; 0">
			<a class="toggle" href="#spoiler-{@id}" onclick="initNano{@id}(); return false;" rel="Скрыть галерею ↑">Галерея ↓</a>
		</xsl:if>
		<div id="spoiler-{@id}" style="{if(f:num(spoiler) &gt; 0) then 'display: none;' else ''}">
			<div id="nanogallery{@id}">
				<script>
					<xsl:if test="f:num(spoiler) = 0">
						$(document).ready(function(){
							initNano<xsl:value-of select="@id"/>();
						});
					</xsl:if>
					function initNano<xsl:value-of select="@id"/>(){
						<xsl:if test="f:num(spoiler) &gt; 0">
						if(!$("<xsl:value-of select="concat('#nanogallery', @id)"/>").is(":visible")){
						</xsl:if>
							$("#nanogallery<xsl:value-of select="@id"/>").nanogallery2( {
								// ### gallery settings ###
								thumbnailHeight:  <xsl:value-of select="height"/>,
								thumbnailWidth:   <xsl:value-of select="width"/>,
								thumbnailBorderHorizontal :   <xsl:value-of select="border"/>,
								thumbnailBorderVertical :   <xsl:value-of select="border"/>,
								thumbnailGutterWidth :   <xsl:value-of select="gutter"/>,
								thumbnailGutterHeight :   <xsl:value-of select="gutter"/>,
								viewerToolbar: { display: false },
								galleryLastRowFull:  false,

								// ### gallery content ###
								items: [
								<xsl:for-each select="pic">
								{ src: '<xsl:value-of select="concat(../@path, .)"/>', srct: '<xsl:value-of select="concat(../@path, .)"/>' },
								</xsl:for-each>
								]
							});
						<xsl:if test="f:num(spoiler) &gt; 0">
						}
						</xsl:if>
					}
				</script>
			</div>
		</div>
	</xsl:template>



	<xsl:template match="custom_block" mode="content">
		<xsl:apply-templates select="."/>
	</xsl:template>
</xsl:stylesheet>