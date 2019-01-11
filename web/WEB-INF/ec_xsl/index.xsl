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
		<section class="hero pb">
			<div class="container">
				<div class="fotorama" data-width="100%" data-height="315px">
					<xsl:for-each select="page/main_page/main_slider_frame">
						<!-- <img src="{@path}{pic}" alt="{name}"/> -->
						<div class="slider-item" data-img="img/desktop-placeholder.png" style="background-image: url({@path}{pic});">
							<div class="slider-item__block fotorama__select">
								<div class="slider-item__wrapper">
									<div class="slider-item__title"><xsl:value-of select="name" /></div>
									<!-- <div class="slider-item__text">
										<xsl:value-of select="text" disable-output-escaping="yes"/>
									</div> -->
									<!-- <a href="{link}" class="slider-item__button"><xsl:value-of select="link_name" disable-output-escaping="yes"/></a> -->
								</div>
							</div>
						</div>
					</xsl:for-each>
				</div>
			</div>
		</section>
		<section>
			<div class="container">
				<div class="block-title">Каталог продукции</div>
				<div class="catalog-items">
					<xsl:for-each select="/page/catalog/section">
						<div class="catalog-item">
							<xsl:variable name="sec_pic" select="if (main_pic != '') then concat(@path, main_pic) else ''"/>
							<xsl:variable name="product_pic" select="if (product/main_pic != '') then concat(product/@path, product/main_pic) else ''"/>
							<xsl:variable name="pic" select="if($sec_pic != '') then $sec_pic else if($product_pic != '') then $product_pic else 'img/no_image.png'"/>
							<a href="{show_products}" class="image-container" style="background-image: url({$pic})"><!-- <img src="{$pic_path}" onerror="$(this).attr('src', 'img/no_image.png')" alt="{name}"/> --></a>
							<div>
								<a href="{show_products}" style="height: unset;"><xsl:value-of select="name"/></a>
								<xsl:value-of select="short" disable-output-escaping="yes"/>
							</div>
						</div>
					</xsl:for-each>
				</div>
			</div>
		</section>
		<section class="special-items ptb" style="background-color: #f2f2f2;">
			<div class="container">
				<div class="block-title">Новинки и акции</div>
				<div class="special-items__devices slick-slider">
					<xsl:apply-templates select="page/main_page/product[tag='Новинка']"/>
				</div>
			</div>
		</section>
		<section class="ptb">
			<div class="container">
				<div class="banners-horizontal">
					<xsl:apply-templates select="page/banner_section[1]/banner"/>
				</div>
			</div>
		</section>
		<section class="news">
			<div class="container">
				<div class="block-title">
					Новости
				</div>
				<div class="wrap">
					<xsl:for-each select="page//news_item">
						<div class="news__item">
							<a class="news__image-container" href="{show_news_item}"><img src="{@path}{main_pic}" alt="{name}" /></a>
							<div class="date"><xsl:value-of select="tokenize(date, ' ')[1]" /></div>
							<a class="news__title" href="{show_news_item}"><xsl:value-of select="header" /></a>
						</div>
					</xsl:for-each>
				</div>
			</div>
		</section>
		<section class="ptb mtb" style="background-color: #f2f2f2;">
			<div class="container">
				<div class="banners-big-icons">
					<xsl:apply-templates select="page/banner_section[2]/banner"/>
				</div>
			</div>
		</section>
		<section class="brands ptb">
			<div class="container">
				<div class="block-title">Производители</div>
				<div class="slick-slider" style="margin: 0 -12px;">
					<div>
						<div class="brand-item">
							<img src="img/brand (1).jpg" />
						</div>
					</div>
					<div>
						<div class="brand-item">
							<img src="img/brand (2).jpg" />
						</div>
					</div>
					<div>
						<div class="brand-item">
							<img src="img/brand (3).jpg" />
						</div>
					</div>
					<div>
						<div class="brand-item">
							<img src="img/brand (4).jpg" />
						</div>
					</div>
					<div>
						<div class="brand-item">
							<img src="img/brand (5).jpg" />
						</div>
					</div>
					<div>
						<div class="brand-item">
							<img src="img/brand (6).jpg" />
						</div>
					</div>
					<div>
						<div class="brand-item">
							<img src="img/brand (7).jpg" />
						</div>
					</div>
					<div>
						<div class="brand-item">
							<img src="img/brand (1).jpg" />
						</div>
					</div>
					<div>
						<div class="brand-item">
							<img src="img/brand (2).jpg" />
						</div>
					</div>
				</div>
			</div>
		</section>
		<section class="s-info">
			<div class="container">
				<xsl:value-of select="$seo/bottom_text" disable-output-escaping="yes"/>
			</div>
		</section>
		<section class="ptb">
			<div class="container">
				<div class="page-map" id="contacts">
					<div class="page-map__map"><script type="text/javascript" charset="utf-8" async="async" src="https://api-maps.yandex.ru/services/constructor/1.0/js/?um=constructor%3A2f9a2f790522d006537ede412d3d2eeb312795427a599cbbdfdab5140aa849b4&amp;width=100%25&amp;height=300&amp;lang=ru_RU&amp;scroll=true"></script></div>
					<div class="page-map__text">
						<div class="block-title">
							Схема проезда и контакты
						</div>
						<p>Республика Беларусь, Витебская обл., 211394, г. Орша, ул. 1 Мая, 81Б-2.</p>
						<p>(+375 17) 123-45-67 - тел./факс;</p>
						<p>(+375 17) 123-45-67 - тел./факс;</p>
						<p>(+375 29) 123-45-67 - Велком;</p>
						<p>(+375 33) 123-45-67 - МТС;</p>
						<p><a href="mailto:skobtrade@mail.ru">skobtrade@mail.ru</a></p>
					</div>
				</div>
			</div>
		</section>

	</xsl:template>

	<xsl:template name="EXTRA_SCRIPTS">
		<script type="text/javascript" src="fotorama/fotorama.js"/>
	</xsl:template>

</xsl:stylesheet>
