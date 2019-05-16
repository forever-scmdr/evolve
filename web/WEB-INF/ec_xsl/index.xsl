<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">
	<xsl:import href="common_page_base.xsl"/>
	<xsl:output method="html" encoding="UTF-8" media-type="text/xhtml" indent="yes" omit-xml-declaration="yes"/>
	<xsl:strip-space elements="*"/>

	<xsl:template name="MARKUP">
		<script type="application/ld+json">
			{
				"@context":"http://schema.org",
				"@type":"Organization",
				"url":"<xsl:value-of select="$main_host"/>/",
				"name":"<xsl:value-of select="$title"/>",
				"logo":"<xsl:value-of select="concat($main_host, '/img/logo_big.svg')"/>",
				"aggregateRating": {
					"@type": "AggregateRating",
					"ratingCount": "53",
					"reviewCount": "53",
					"bestRating": "5",
					"ratingValue": "4,9",
					"worstRating": "1",
					"name": "TTD"
				},
				"contactPoint": [
					<xsl:for-each select="page/common/phone" >
						<xsl:if test="position() != 1">,</xsl:if>{
						"@type":"ContactPoint",
						"telephone":"<xsl:value-of select="tokenize(., '_')[1]"/>",
						"contactType":"<xsl:value-of select="tokenize(., '_')[2]"/>"
						}
					</xsl:for-each>
				]
				<xsl:if test="page/common/email != ''">
				,"email":[<xsl:for-each select="page/common/email" >
						<xsl:if test="position() != 1">, </xsl:if>"<xsl:value-of select="."/>"</xsl:for-each>]
				</xsl:if>
			}
		</script>
	</xsl:template>

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
		<xsl:if test="page/main_page/link_text and not(page/main_page/link_text = '')">
			<div class="actions">
				<h3>Акции</h3>
				<div class="actions-container">
					<a href="{page/common/link_link}"><xsl:value-of select="page/common/link_text"/></a>
				</div>
			</div>
		</xsl:if>
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


	<xsl:template name="CONTENT"></xsl:template>

	<xsl:template name="MAIN_CONTENT">
		<!-- MAIN COLOUMNS BEGIN -->
		<div class="container">
			<div class="row">

				<!-- RIGHT COLOUMN BEGIN -->
				<div class="col-md-12 col-xs-12 main-content">
					<div class="mc-container">
						<xsl:call-template name="INC_MOBILE_HEADER"/>
						<xsl:call-template name="CONTENT"/>
					</div>
				</div>
				<!-- RIGHT COLOUMN END -->
			</div>
		</div>
		<!-- MAIN COLOUMNS END -->
	</xsl:template>



	<xsl:template match="banner">
		<div class="banner {extra_style}">
			<div class="banner__background" style="{background}"></div>
			<div class="banner__title"><xsl:value-of select="header" /></div>
			<div class="banner__text"><xsl:value-of select="text" disable-output-escaping="yes" /></div>
			<div class="banner__image">
				<xsl:choose>
					<xsl:when test="image_code and not(image_code = '')"><xsl:value-of select="image_code" disable-output-escaping="yes" /></xsl:when>
					<xsl:otherwise>
						<xsl:if test="image_pic and not(image_pic = '')"><img src="{@path}{image_pic}"/></xsl:if>
					</xsl:otherwise>
				</xsl:choose>
			</div>
			<a href="{link}" class="banner__link"></a>
		</div>
	</xsl:template>


	<xsl:template name="BANNERS">
		<section class="hero">
			<div class="container">
				<div class="fotorama" data-width="100%" data-autoplay="true">
					<xsl:for-each select="page/main_page/main_slider_frame">
						<!-- <img src="{@path}{pic}" alt="{name}"/> -->
						 <div data-img="{@path}{pic}" alt="{name}"><a class="slider-link" href="{link}"></a></div>
					</xsl:for-each>
				</div>
				<div class="hero__banners">
					<xsl:apply-templates select="page/banner_section[1]/banner"/>
				</div>
			</div>
		</section>
		<section class="events">
			<div class="container">
				<div>
					<div class="events__banners">
						<xsl:apply-templates select="page/banner_section[2]/banner"/>
					</div>
					<div class="block-title events__title">Новости</div>
					<div class="events__news">
						<xsl:for-each select="page//news_item">
							<div class="news-item events__news-item">
								<div class="small-text"><xsl:value-of select="tokenize(date, ' ')[1]" /></div>
								<a href="{show_news_item}"><xsl:value-of select="header" /></a>
							</div>
						</xsl:for-each>
					</div>
				</div>
				<xsl:apply-templates select="page/banner_section[3]/banner"/>
			</div>
		</section>
		<section class="special-items">
			<div class="container" style="overflow: hidden;">
				<div class="block-title">Новинки</div>
				<div class="special-items__devices slick-slider">
					<xsl:apply-templates select="page/main_page/product[tag='Новинка']"/>
				</div>
			</div>
		</section>
		<section class="special-items">
			<div class="container" style="overflow: hidden;">
				<div class="block-title">Акции</div>
				<div class="special-items__devices slick-slider zu">
					<xsl:apply-templates select="page/main_page/product[tag='Акция']"/>
				</div>
			</div>
		</section>
		<section class="benefits">
			<div class="container">
				<div class="block-title">Почему нас выбирают клиенты</div>
				<div class="benefits__banners">
					<xsl:apply-templates select="page/banner_section[4]/banner"/>
				</div>
			</div>
		</section>
		<section class="s-info pt-4">
			<div class="container">
				<xsl:value-of select="$seo/bottom_text" disable-output-escaping="yes"/>
			</div>
		</section>

	</xsl:template>

	<xsl:template name="EXTRA_SCRIPTS">
		<script type="text/javascript" src="fotorama/fotorama.js"/>
	</xsl:template>

</xsl:stylesheet>
