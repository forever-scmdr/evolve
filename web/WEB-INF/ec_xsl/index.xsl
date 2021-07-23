<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">
	<xsl:import href="common_page_base.xsl"/>
	<xsl:output method="html" encoding="UTF-8" media-type="text/xhtml" indent="yes" omit-xml-declaration="yes"/>
	<xsl:strip-space elements="*"/>

	<xsl:template name="MARKUP">
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
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
					"name": "Натуральная и искусственная кожа Тексимат"
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
				<div class="h3">Акции</div>
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


	<xsl:template name="CONTENT">
		<xsl:if test="$seo/h1 and $seo/h1 != ''">
			<h1 style="text-align: center;"><xsl:value-of select="$seo/h1"/></h1>
		</xsl:if>
		<div class="slider-container">
			<div class="fotorama" data-transition="crossfade" data-width="100%" data-maxwidth="100%" data-thumbheight="40" data-thumbwidth="40" data-autoplay="true" data-loop="true">
				<xsl:for-each select="page/main_page/main_slider_frame">
					<img src="{@path}{pic}" alt="{name}"/>
				</xsl:for-each>
			</div>
		</div>
		<div class="actions mobile">
			<h3>Акции</h3>
			<div class="actions-container">
				<a href="{page/common/link_link}"><xsl:value-of select="page/common/link_text"/></a>
			</div>
		</div>
	</xsl:template>

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

	<!-- <xsl:template name="BANNERS">
		<div class="container p-t">
			<div class="row">
				<div class="col-xs-12 banners">
					<div class="banners-container">
						<xsl:for-each select="page/main_page/main_promo_bottom">
							<a href="{link}" style="background-image: url({@path}{pic})">
								<h4><xsl:value-of select="text_big"/></h4>
								<p><xsl:value-of select="text_small"/></p>
							</a>
						</xsl:for-each>
					</div>
				</div>
			</div>
		</div>
	</xsl:template> -->

	<xsl:template name="BANNERS">
		<div class="container-fluid" style="background-color: #f5f1ea; padding: 50px 0;">
			<div class="container .container-tb">
				<div class="banners-container">
					<xsl:for-each select="page/main_page/main_promo_bottom">
						<div class="banner">
							<div class="banner__image" style="background-image: url({@path}{pic})"></div>
							<div class="banner__title"><xsl:value-of select="text_big"/></div>
							<div class="banner__text"><xsl:value-of select="text_small"/></div>
							<a class="banner__link" href="{link}"></a>
						</div>
					</xsl:for-each>
				</div>
			</div>
		</div>

		<div class="container container-tb">
			<div class="hero hero_center">
				<div class="hero-block hero-block_center">
					<!-- <div class="hero-block__icon"></div> -->
					<div class="hero-block__title">2002</div>
					<div class="hero-block__text">год начала работы</div>
				</div>
				<div class="hero-block hero-block_center">
					<!-- <div class="hero-block__icon"></div> -->
					<div class="hero-block__title">более 500</div>
					<div class="hero-block__text">довольных клиентов</div>
				</div>
				<div class="hero-block hero-block_center">
					<!-- <div class="hero-block__icon"></div> -->
					<div class="hero-block__title">19 лет</div>
					<div class="hero-block__text">безупречной работы</div>
				</div>
			</div>
		</div>
		<div class="separator"></div>
		<div class="container container-tb">
			<div class="quote quote_center">
				<p>Основное направление компании Тексимат — кожа оптом и в розницу. Мы любим свое дело и ценим наших клиентов, поэтому предлагаем только лучшие материалы от ведущих европейских производителей.</p>
			</div>
		</div>
		<!-- <div class="container-fluid contaner-tb">
			<div class="photo-stripe">
				<div class="photo-stripe__item">
					<a href="" class="photo-stripe__link photo-stripe__link_darken" data-toggle="modal" data-target="#modal-photo"></a>
					<div class="photo-stripe__image" style="background-image: url(../img/bp1_small.jpg);"></div>
					<div class="photo-stripe__text">Подпись к фото</div>
				</div>
				<div class="photo-stripe__item">
					<a href="" class="photo-stripe__link photo-stripe__link_darken" data-toggle="modal" data-target="#modal-photo"></a>
					<div class="photo-stripe__image" style="background-image: url(../img/bp2_small.jpg);"></div>
					<div class="photo-stripe__text">Подпись к фото</div>
				</div>
				<div class="photo-stripe__item">
					<a href="" class="photo-stripe__link photo-stripe__link_darken" data-toggle="modal" data-target="#modal-photo"></a>
					<div class="photo-stripe__image" style="background-image: url(../img/bp3_small.jpg);"></div>
					<div class="photo-stripe__text">Подпись к фото</div>
				</div>
				<div class="photo-stripe__item">
					<a href="" class="photo-stripe__link photo-stripe__link_darken" data-toggle="modal" data-target="#modal-photo"></a>
					<div class="photo-stripe__image" style="background-image: url(../img/bp4_small.jpg);"></div>
					<div class="photo-stripe__text">Подпись к фото</div>
				</div>
				<div class="photo-stripe__item">
					<a href="" class="photo-stripe__link photo-stripe__link_darken" data-toggle="modal" data-target="#modal-photo"></a>
					<div class="photo-stripe__image" style="background-image: url(../img/bp5_small.jpg);"></div>
					<div class="photo-stripe__text">Подпись к фото</div>
				</div>
			</div>
		</div> -->

		<div class="container container-tb">
			<div class="hero">
				<div class="hero-block hero-block_center">
					<div class="hero-block__icon">
						<i class="fa fa-star"></i>
					</div>
					<div class="hero-block__title hero-block__title_small">Самые низкие цены в РБ</div>
					<!-- <div class="hero-block__text hero-block__text_small"></div> -->
				</div>
				<div class="hero-block hero-block_center">
					<div class="hero-block__icon">
						<i class="fa fa-thumbs-up"></i>
					</div>
					<div class="hero-block__title hero-block__title_small">Огромный выбор</div>
					<!-- <div class="hero-block__text hero-block__text_small"></div> -->
				</div>
				<div class="hero-block hero-block_center">
					<div class="hero-block__icon">
						<i class="fa fa-trophy"></i>
					</div>
					<div class="hero-block__title hero-block__title_small">Отрез от 1 метра + 10 см.</div>
					<!-- <div class="hero-block__text hero-block__text_small"></div> -->
				</div>
				<div class="hero-block hero-block_center">
					<div class="hero-block__icon">
						<i class="fa fa-truck"></i>
					</div>
					<div class="hero-block__title hero-block__title_small">Офис, склад и парковка в одном месте</div>
					<!-- <div class="hero-block__text hero-block__text_small"></div> -->
				</div>
			</div>
		</div>
		<div class="separator"></div>
		<div class="container container-b">
			<h2 style="text-align: center;">Популярные товары</h2>
			<div class="catalog-items">
				<xsl:apply-templates select="page/current_section/product"/>
			</div>
		</div>
		<xsl:if test="$seo/text">
			<div class="separator"></div>
			<div class="container container-tb">
				<xsl:value-of select="$seo/text" disable-output-escaping="yes"/>
			</div>
		</xsl:if>
		<div class="separator"></div>
		<div class="container container-tb">
			<iframe src="https://yandex.ru/map-widget/v1/?um=constructor%3A8c0f10f05c12286c9fd48bd797a940c23e30643f5c9a624444d4a7901bc2b9f9&amp;source=constructor" width="100%" height="400" frameborder="0"></iframe>

		</div>
	</xsl:template>

	<xsl:template name="EXTRA_SCRIPTS">
		<script type="text/javascript" src="fotorama/fotorama.js"/>
		<xsl:call-template name="CART_SCRIPT"/>
	</xsl:template>

</xsl:stylesheet>
