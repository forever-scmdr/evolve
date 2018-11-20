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


	<xsl:template name="BANNERS">
		<section class="hero">
			<div class="container">
				<div class="fotorama" data-width="100%">
					<xsl:for-each select="page/main_page/main_slider_frame">
						<img src="{@path}{pic}" alt="{name}"/>
					</xsl:for-each>
				</div>
				<div class="hero__banners">
					<div class="banner hero__banner">
						<div class="banner__background"></div>
							<div class="banner__title">Epson SureColor SC-F2000</div>
							<div class="banner__text">Производительный и надежный принтер прямой печати на ткани</div>
							<div class="banner__image"></div>
							<a href="/" class="banner__link"></a>
					</div>
					<div class="banner hero__banner">
						<div class="banner__background"></div>
							<div class="banner__title">Epson SureColor SC-F2000</div>
							<div class="banner__text">Производительный и надежный принтер прямой печати на ткани</div>
							<div class="banner__image"></div>
							<a href="/" class="banner__link"></a>
					</div>
				</div>
			</div>
		</section>
		<section class="events">
			<div class="container">
				<div>
					<div class="events__banners">
						<div class="banner events__banner">
							<div class="banner__background"></div>
							<div class="banner__title">Epson SureColor SC-F2000</div>
							<div class="banner__text">Производительный и надежный принтер прямой печати на ткани</div>
							<div class="banner__image"></div>
							<a href="/" class="banner__link"></a>
						</div>
						<div class="banner events__banner banner_blue">
							<div class="banner__background"></div>
							<div class="banner__title">Месяц скидок</div>
							<div class="banner__text">Производительный и надежный принтер прямой печати на ткани</div>
							<div class="banner__image"><i class="fas fa-percent"></i></div>
							<a href="/" class="banner__link"></a>
						</div>
					</div>
					<div class="block-title events__title">Новости</div>
					<div class="events__news">
						<div class="news-item events__news-item">
							<div class="small-text">01.01.2018</div>
							<a href="">Новая компьютерная прямострочка JUKI DDL-9000c продана в Беларуси</a>
						</div>
						<div class="news-item events__news-item">
							<div class="small-text">01.01.2018</div>
							<a href="">Новая компьютерная прямострочка JUKI DDL-9000c продана в Беларуси</a>
						</div>
					</div>
				</div>
				<div class="banner events__big-banner">
					<div class="banner__background"></div>
					<div class="banner__title"></div>
					<div class="banner__text"></div>
					<div class="banner__image"></div>
					<a href="/" class="banner__link"></a>
				</div>
			</div>
		</section>
		<section class="special-items">
			<div class="container">
				<div class="block-title">Новинки</div>
				<div class="special-items__devices slick-slider">
					<xsl:apply-templates select="page/product[tag='Новинка']"/>
				</div>
			</div>
		</section>
		<section class="special-items">
			<div class="container">
				<div class="block-title">Акции</div>
				<div class="special-items__devices slick-slider zu">
					<xsl:apply-templates select="page/product[tag='Акция']"/>
				</div>
			</div>
		</section>
		<section class="benefits">
			<div class="container">
				<div class="block-title">Почему нас выбирают клиенты</div>
				<div class="benefits__banners">
					<div class="banner benefits__banner banner_gray">
						<div class="banner__background"></div>
						<div class="banner__title">Надежность</div>
						<div class="banner__text">Нам доверяют 17 лет</div>
						<div class="banner__image"><i class="fas fa-shield-alt"></i></div>
						<a href="/" class="banner__link"></a>
					</div>
					<div class="banner benefits__banner banner_gray">
						<div class="banner__background"></div>
						<div class="banner__title">Гарантии</div>
						<div class="banner__text">Свой сервисный центр</div>
						<div class="banner__image"><i class="fas fa-trophy"></i></div>
						<a href="/" class="banner__link"></a>
					</div>
					<div class="banner benefits__banner banner_gray">
						<div class="banner__background"></div>
						<div class="banner__title">Цены</div>
						<div class="banner__text">Специальные предложения и акции</div>
						<div class="banner__image"><i class="far fa-thumbs-up"></i></div>
						<a href="/" class="banner__link"></a>
					</div>
					<div class="banner benefits__banner banner_gray">
						<div class="banner__background"></div>
						<div class="banner__title">Качество</div>
						<div class="banner__text">Только проверенные бренды</div>
						<div class="banner__image"><i class="fas fa-check"></i></div>
						<a href="/" class="banner__link"></a>
					</div>
					<div class="banner benefits__banner banner_gray">
						<div class="banner__background"></div>
						<div class="banner__title">Опыт</div>
						<div class="banner__text">Квалифицированные специалисты</div>
						<div class="banner__image"><i class="far fa-clock"></i></div>
						<a href="/" class="banner__link"></a>
					</div>
					<div class="banner benefits__banner banner_gray">
						<div class="banner__background"></div>
						<div class="banner__title">Репутация</div>
						<div class="banner__text">Ценим каждого клиента</div>
						<div class="banner__image"><i class="far fa-user"></i></div>
						<a href="/" class="banner__link"></a>
					</div>
				</div>
			</div>
		</section>
		<section class="s-info pt-4">
			<div class="container">
				<p>Sed ut perspiciatis unde omnis iste natus error sit voluptatem accusantium doloremque laudantium, totam rem aperiam, eaque ipsa quae ab illo inventore veritatis et quasi architecto beatae vitae dicta sunt explicabo. Nemo enim ipsam voluptatem quia voluptas sit aspernatur aut odit aut fugit, sed quia consequuntur magni dolores eos qui ratione voluptatem sequi nesciunt.</p>
				<p>
				Neque porro quisquam est, qui dolorem ipsum quia dolor sit amet, consectetur, adipisci velit, sed quia non numquam eius modi tempora incidunt ut labore et dolore magnam aliquam quaerat voluptatem. Ut enim ad minima veniam, quis nostrum exercitationem ullam corporis suscipit laboriosam, nisi ut aliquid ex ea commodi consequatur? Quis autem vel eum iure reprehenderit qui in ea voluptate velit esse quam nihil molestiae consequatur, vel illum qui dolorem eum fugiat quo voluptas nulla pariatur?</p>
			</div>
		</section>

		<!-- BANNER EXAMPLE -->
		<!-- <xsl:for-each select="page/main_page/main_promo_bottom">
			<div class="banner">
				<div class="banner__image" style="background-image: url({@path}{pic})"></div>
				<div class="banner__title"><xsl:value-of select="text_big"/></div>
				<div class="banner__text"><xsl:value-of select="text_small"/></div>
				<a class="banner__link" href="{link}"></a>
			</div>
		</xsl:for-each> -->
		<!-- BANNER EXAMPLE END-->

	</xsl:template>

	<xsl:template name="EXTRA_SCRIPTS">
		<script type="text/javascript" src="fotorama/fotorama.js"/>
	</xsl:template>

</xsl:stylesheet>
