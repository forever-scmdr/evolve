<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">
	<xsl:import href="common_page_base.xsl"/>
	<xsl:import href="templates.xsl"/>
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
		<div class="container" style="position: relateive; z-index: 2;">
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
	
	<!-- Новая шапка со слайдером для главной страницы -->
	<xsl:template name="INC_DESKTOP_HEADER">
		<div class="wrapper">
			<div class="slider fotorama" data-width="100%" data-height="100%" data-fit="cover" data-nav="false" data-autoplay="3000">
				<xsl:for-each select="page/main_page/main_slider_frame">
					<img src="{@path}{pic}" alt="{name}"/>
					<!-- <div data-img="{@path}{pic}"><a class="slider__link" href="{link}"></a></div> -->
					<!-- <div class="slider-item" data-img="img/desktop-placeholder.png" style="background-image: url({@path}{pic});">
						<div class="slider-item__block fotorama__select">
							<div class="slider-item__wrapper">
								<div class="slider-item__text">
									<xsl:value-of select="text" disable-output-escaping="yes"/>
								</div>
								<div class="slider-item__title"><xsl:value-of select="name" /></div>
								<a href="{link}" class="slider-item__button"><xsl:value-of select="link_name" disable-output-escaping="yes"/></a>
							</div>
						</div>
					</div> -->
				</xsl:for-each>
			</div>
			<div class="slider__gradient"></div>
			<div class="header header_main">
				<div class="top-stripe">
					<div class="top-stripe__nav">
						<xsl:apply-templates select="page/custom_pages/*[not(in_main_menu = ('да', 'нет'))]" mode="top_stripe"/>
						<!-- <a href="{page/contacts_link}">Contacts</a> -->
						
					</div>
					<div class="top-stripe__info">
						<!-- <div><strong>Minsk</strong> coordinator@mhr-gi.net</div>
						<div><strong>Damascus</strong> operations@mhr-gi.net</div> -->
						<div><strong>Minsk</strong> assistant_bel@mhr-gi.net</div>
						<div><strong>Damascus</strong> assistant@mhr-gi.net</div>
					</div>
				</div>
				<div class="navigation">
					<div class="logo">
						<a href="/index">
							<img src="img/logo.png" alt="" itemprop="image"/>
						</a>
					</div>
					<div class="main-menu">
						<xsl:apply-templates select="page/custom_pages/*[in_main_menu = 'да']" mode="menu_first"/>
						<!-- <a href="">Business Development Consultancy</a>
						<a href="">About Us</a>
						<a href="">Educational Programs</a>
						<a href="">Admission</a> -->
					</div>
				</div>
				<div class="menu">
					<a href=""><img src="img/assets/icon-bars.svg" alt=""/></a>
				</div>
				<div class="content">
					<!-- <div class="content__text">If you like to grow, we like to help</div> -->
					<div class="content__title">Management &amp; human resource group international bel</div>
					<div class="content__links">
<!--						<a href="/about_us" class="content__link">Learn more</a>-->
						<a href="/contacts" class="content__link">Contact us</a>
					</div>
				</div>
				<div class="events">
					<!-- <div class="events__header">Join Our  Webinar Online!</div> -->
					<div class="events__content">
					
						<div class="events__item desktop-only" style="display: block;">
							<xsl:value-of select="//main_slider_frame[1]/text" disable-output-escaping="yes"/>
						</div>
					</div>
				</div>
				<div class="social">
					<a href="https://www.facebook.com/mhrgibel/notifications/" class="social__item"><img src="img/assets/icon-facebook.png" alt=""/></a>
					<a href="https://www.youtube.com/channel/UCyqKM5QXCwgsTi1JFcoPZPQ" class="social__item"><img src="img/assets/icon-youtube.png" alt=""/></a>
					<a href="https://www.instagram.com/p/BibsfJwHQcs/?utm_source=ig_embed" class="social__item"><img src="img/assets/icon-instagram.png" alt=""/></a>
				</div>
			</div>
		</div>
	</xsl:template>





	<xsl:template name="INDEX_BLOCKS">
		<!-- <section class="hero pb">
			<div class="container">
				<div class="fotorama" data-width="100%">
					<xsl:for-each select="page/main_page/main_slider_frame">
						<img src="{@path}{pic}" alt="{name}"/>
						<div data-img="{@path}{pic}"><a class="slider__link" href="{link}"></a></div>
						<div class="slider-item" data-img="img/desktop-placeholder.png" style="background-image: url({@path}{pic});">
							<div class="slider-item__block fotorama__select">
								<div class="slider-item__wrapper">
									<div class="slider-item__title"><xsl:value-of select="name" /></div>
									<div class="slider-item__text">
										<xsl:value-of select="text" disable-output-escaping="yes"/>
									</div>
									<a href="{link}" class="slider-item__button"><xsl:value-of select="link_name" disable-output-escaping="yes"/></a>
								</div>
							</div>
						</div>
					</xsl:for-each>
				</div>
			</div>
		</section> -->




				<div class="wrapper mobile">
					<div class="slider fotorama" data-width="100%" data-fit="cover" data-nav="false">
						<xsl:for-each select="page/main_page/main_slider_frame">
							<img src="{@path}{pic}" alt="{name}"/>
							
						</xsl:for-each>
					</div>
					<div class="slider__gradient"></div>
					<div class="header header_main">
						<!-- <div class="top-stripe">
							<div class="top-stripe__nav">
								<xsl:apply-templates select="page/custom_pages/*[not(in_main_menu = ('да', 'нет'))]" mode="top_stripe"/>
								<a href="{page/contacts_link}">Contacts</a>
							</div>
							<div class="top-stripe__info">
								<div><strong>Minsk</strong> katya@mhr-gi.net</div>
								<div><strong>Damascus</strong> hani@mhr-gi.net</div>
							</div>
						</div>
						<div class="navigation">
							<div class="logo">
								<a href="/index">
									<img src="img/logo.png" alt=""/>
								</a>
							</div>
							<div class="main-menu">
								<xsl:apply-templates select="page/custom_pages/*[in_main_menu = 'да']" mode="menu_first"/>
								<a href="">Business Development Consultancy</a>
								<a href="">About Us</a>
								<a href="">Educational Programs</a>
								<a href="">Admission</a>
							</div>
						</div>
						<div class="menu">
							<a href=""><img src="img/assets/icon-bars.svg" alt=""/></a>
						</div> -->
						<div class="content" >
							<!-- <div class="content__text">If you like to grow, we like to help</div> -->
							<div class="content__title" >Management &amp; human resource group international bel</div>
							<!-- <div class="content__links">
								<a href="/about_us" class="content__link">Learn more</a>
								<a href="/contacts" class="content__link">Contact us</a>
							</div> -->
						</div>
						
						<!-- <div class="events">
							<div class="events__header">Join Our  Webinar Online!</div>
							<div class="events__content">
								<div class="events__item">
									<a href="/corporate_leadership_and_strategy" class="events__title">Corporate Leadership and Strategy During COVID-19 Crisis.</a>
									<p>Thursday, May 28, 2020. 5 PM-6:30 PM - Minsk. Damascus. 4 PM - 5:30 PM - Rome</p>
								</div>
							</div>
						</div>
						<div class="social">
							<a href="https://www.facebook.com/mhr.gi/" class="social__item"><img src="img/assets/icon-facebook.png" alt=""/></a>
							<a href="https://www.youtube.com/channel/UCyqKM5QXCwgsTi1JFcoPZPQ" class="social__item"><img src="img/assets/icon-youtube.png" alt=""/></a>
							<a href="https://www.instagram.com/p/BibsfJwHQcs/?utm_source=ig_embed" class="social__item"><img src="img/assets/icon-instagram.png" alt=""/></a>
						</div> -->
					</div>
				</div>

				<section>
					<div class="container mobile-only" style="padding: 10px 16px 0 16px;">
						<div class="banner__text">
							<xsl:value-of select="//main_slider_frame[1]/text" disable-output-escaping="yes"/>
						</div>
					</div>
				</section>

		<!-- banners -->
		<xsl:apply-templates select="page/main_page/custom_block[1]"></xsl:apply-templates>
		<xsl:apply-templates select="page/main_page/custom_block[2]"></xsl:apply-templates>
		<xsl:apply-templates select="page/main_page/custom_block[3]"></xsl:apply-templates>
		<xsl:apply-templates select="page/main_page/custom_block[4]"></xsl:apply-templates>
		<xsl:apply-templates select="page/main_page/custom_block[5]"></xsl:apply-templates>
		<xsl:apply-templates select="page/main_page/custom_block[6]"></xsl:apply-templates>

		<section class="news pt">
			<div class="container">
				<div class="block__title block__title_left">
					<a href="/novosti" style="text-decoration: none; color: black">Events</a>
				</div>
				<div class="wrap">
					<xsl:for-each select="page//news_item">
						<div class="news__item">
							<a class="news__image-container" href="{show_news_item}"><img src="{@path}{main_pic}" alt="{name}" /></a>
							<div class="date"><xsl:value-of select="tokenize(date, ' ')[1]" /></div>
							<a class="news__title" href="{show_news_item}"><xsl:value-of select="header" /></a>
							<xsl:value-of select="short" disable-output-escaping="yes"/>
						</div>
					</xsl:for-each>
				</div>
			</div>
		</section>


		<xsl:apply-templates select="page/main_page/custom_block[7]"></xsl:apply-templates>
		<xsl:apply-templates select="page/main_page/custom_block[8]"></xsl:apply-templates> 

		<!-- <section class="s-info">
			<div class="container">
				<xsl:value-of select="$seo/bottom_text" disable-output-escaping="yes"/>
			</div>
		</section> -->

	</xsl:template>

	<xsl:template name="EXTRA_SCRIPTS">
		<script type="text/javascript" src="fotorama/fotorama.js"/>
	</xsl:template>

</xsl:stylesheet>
