<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">
	<xsl:import href="common_page_base.xsl"/>
	<xsl:output method="xhtml" encoding="UTF-8" media-type="text/xhtml" indent="yes" omit-xml-declaration="yes"/>
	<xsl:strip-space elements="*"/>

	<xsl:template name="LEFT_COLOUMN">
		<div class="side-menu">
			<xsl:for-each select="page/catalog/section">
				<div class="level-1">
					<div class="capsule">
						<a href="{show_section}"><xsl:value-of select="name"/></a>
					</div>
					<xsl:if test="section">
						<div class="popup-menu" style="display:none">
							<div class="popup-coloumn">
								<xsl:for-each select="section[position() &lt;= 8]">
									<div><a href="{show_section}"><xsl:value-of select="name"/></a></div>
								</xsl:for-each>
							</div>
							<xsl:if test="count(section) &gt; 8">
								<div class="popup-coloumn">
									<xsl:for-each select="section[position() &gt; 8]">
										<div><a href="{show_section}"><xsl:value-of select="name"/></a></div>
									</xsl:for-each>
								</div>
							</xsl:if>
						</div>
					</xsl:if>
				</div>
			</xsl:for-each>
		</div>
		<div class="actions">
			<h3>Акции</h3>
			<div class="actions-container">
				<a href="">Что делать, если поломалась или разбилась сенсорная панель вашего телефона</a>
			</div>
		</div>
		<script>
			var _menuShowInterval = 0;
			var _menuHideInterval = 0;
			var _menuCurrentItem = 0;
			$(document).ready(function() {
				$('.level-1').hover(
					function(){
						clearInterval(_menuHideInterval);
						if (_menuMouseMovedVertically) {
							$('.popup-menu').hide();
							$(this).find('.popup-menu').show();
						} else {
							_menuCurrentItem = $(this);
							_menuShowInterval = setInterval(function() {
								$('.popup-menu').hide();
								_menuCurrentItem.find('.popup-menu').show();
							}, 500);
						}
					},
					function() {
						clearInterval(_menuShowInterval);
						if (_menuMouseMovedVertically) {
							$('.popup-menu').hide();
						} else {
							_menuHideInterval = setInterval(function() {
								$('.popup-menu').hide();
							}, 500);
						}
					}
				);
			<xsl:text disable-output-escaping="yes">
				var _menuPrevX = 1000;
				var _menuPrevY = -1000;
				var _menuMouseMovedVertically = true;
				$('.side-menu').mousemove(
					function(event) {
						_menuMouseMovedVertically = (Math.abs(event.pageY - _menuPrevY) - Math.abs(event.pageX - _menuPrevX)) &gt; 0;
						_menuPrevX = event.pageX;
						_menuPrevY = event.pageY;
						console.log(_menuMouseMovedVertically);
					}
				);
			</xsl:text>
			});
		</script>
		<!-- <div class="contacts">
			<h3>Заказ и консультация</h3>
			<p><a href="tel:+375 29 537-11-00">+375 29 537-11-00</a> - тел./Viber</p>
			<p>Email <a href="">info@beltesto.by</a></p>
			<p><a href="">Схема проезда к офису</a></p>
		</div> -->
	</xsl:template>


	<xsl:template name="CONTENT">
		<div class="slider-container">
			<div>СЛАЙДЕР</div>
		</div>
		<div class="actions mobile">
			<h3>Акции</h3>
			<div class="actions-container">
				<a href="">Что делать, если поломалась или разбилась сенсорная панель вашего телефона</a>
			</div>
		</div>
		<div class="news">
			<h3>Новости</h3>
			<div class="news-container">
				<div>
					<a href="">Что делать, если поломалась или разбилась сенсорная панель вашего телефона</a>
				</div>
				<div>
					<a href="">Что делать, если поломалась или разбилась сенсорная панель вашего телефона</a>
				</div>
				<div>
					<a href="">Что делать, если поломалась или разбилась сенсорная панель вашего телефона</a>
				</div>
				<div>
					<a href="">Что делать, если поломалась или разбилась сенсорная панель вашего телефона</a>
				</div>

			</div>
		</div>
	</xsl:template>


	<xsl:template name="BANNERS">
		<div class="container p-t">
			<div class="row">
				<div class="col-xs-12 banners">
					<!-- <h3>Специальные предложения</h3> -->
					<div class="banners-container">
						<a href="">
							<h4>Камеры для смартфонов</h4>
							<p>Большой выбор комплектующих</p>
						</a>
						<a href="">
							<h4>Камеры для смартфонов</h4>
							<p>Большой выбор комплектующих</p>
						</a>
						<a href="">
							<h4>Камеры для смартфонов</h4>
							<p>Большой выбор комплектующих</p>
						</a>
						<a href="">
							<h4>Камеры для смартфонов</h4>
							<p>Большой выбор комплектующих</p>
						</a>
					</div>
				</div>
			</div>
		</div>
	</xsl:template>

</xsl:stylesheet>