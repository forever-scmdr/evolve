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
				"logo":"<xsl:value-of select="concat($base, '/img/logo_big.svg')"/>",
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


	<xsl:template name="CONTENT">
		<!-- <div class="slider-container">
			<div class="fotorama" data-transition="crossfade" data-width="100%" data-maxwidth="100%" data-thumbheight="40" data-thumbwidth="40" data-autoplay="true" data-loop="true" data-fit="cover">
				<xsl:for-each select="page/main_page/main_slider_frame">
					<img src="{@path}{pic}" alt="{name}"/>
				</xsl:for-each>
			</div>
		</div> -->
		<!-- <div class="actions mobile">
			<h3>Акции</h3>
			<div class="actions-container">
				<a href="{page/common/link_link}"><xsl:value-of select="page/common/link_text"/></a>
			</div>
		</div> -->
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
		<div class="container-fluid" style="padding: 0;">
			<div class="slider-container">
				<div class="fotorama" style="width: 100%;" data-width="100%" data-height="400" data-transition="crossfade" data-autoplay="true" data-loop="true" data-fit="cover">
					<xsl:for-each select="page/main_page/main_slider_frame">
						<!-- <img src="{@path}{pic}" alt="{name}"/> -->
						<div class="slider-item" data-img="img/desktop-placeholder.png" style="background-image: url({@path}{pic});">
							<div class="container">
								<div class="slider-item__block fotorama__select">
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
		</div>

		<!-- <div class="has-items-carousel">
			<div class="container">
				<div class="more-products">
					<div class="title_2">Лидеры продаж</div>
					<div class="slick-slider catalog-items">
						<xsl:apply-templates select="page/product"/>
					</div>
				</div>
			</div>
		</div> -->



		<!-- <div class="container container-tb">
			<div class="hero hero_center">
				<div class="hero-block hero-block_center">
					<div class="hero-block__icon"></div>
					<div class="hero-block__title">1972</div>
					<div class="hero-block__text">год начала работы</div>
				</div>
				<div class="hero-block hero-block_center">
					<div class="hero-block__icon"></div>
					<div class="hero-block__title">более 180</div>
					<div class="hero-block__text">довольных клиентов</div>
				</div>
				<div class="hero-block hero-block_center">
					<div class="hero-block__icon"></div>
					<div class="hero-block__title">46 лет</div>
					<div class="hero-block__text">безупречной работы</div>
				</div>
			</div>
		</div> -->



		<!-- <div class="separator"></div>
		<div class="container container-tb">
			<div class="quote quote_center">
				<p>Основное направление компании Тексимат — кожа оптом и в розницу. Мы любим свое дело и ценим наших клиентов, поэтому предлагаем только лучшие материалы от ведущих европейских производителей.</p>
			</div>
		</div> -->



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


		<div class="container-fluid" style="background-color: #fff; padding: 50px 0;">
			<div class="container .container-tb">
				<div class="banners-container">
					<xsl:for-each select="page/main_page/main_promo_bottom">
						<div class="banner">
							<div class="banner__image" style="background-image: url({@path}{pic})"></div>
							<div class="banner__title"><xsl:value-of select="text_big"/></div>
							<!-- <div class="banner__text"><xsl:value-of select="text_small"/></div> -->
							<a class="banner__link" href="{link}"></a>
						</div>
					</xsl:for-each>
				</div>
			</div>
		</div>

		<div class="container">
			<div class="about">
				<div class="about__text">
					<div class="title_2">О нас</div>
					<p>Начиная с 2006 года Общество с ограниченной ответственностью <span style="white-space:nowrap">«М-Тех»</span> успешно работает в сельскохозяйственном секторе.</p>
					<p>Основные направления деятельности нашей компании - это реализация импортного оборудования и комплектующих, гарантийное и послегарантийное обслуживание молочно-доильного оборудования, оборудования для свиноводческих комплексов, биогазовых установок.</p>
					<p>Прямое сотрудничество с европейскими производителями, позволяет нам поставлять качественную продукцию по оптимальной стоимости.</p>
				</div>
				<div class="about__blocks">
					<div class="about__block">
						<i class="fas fa-award about__icon"></i>
						<strong>Качественное оборудование от производителя по приемлемым ценам</strong>
					</div>
					<div class="about__block">
						<i class="fas fa-user-cog about__icon"></i>
						<strong>Гарантийное и послегарантийное обслуживание</strong>
					</div>
					<div class="about__block">
						<i class="fas fa-percent about__icon"></i>
						<strong>Гибкая система скидок</strong>
					</div>
				</div>
			</div>
		</div>
		<div class="container-fluid" style="background-color: #f4f4f4; padding: 40px 0;">
			<div class="container">
				<div class="title_2" style="text-align: center; margin-bottom: 48px;"><a href="img/impulsa-sert.jpg" class="magnific_popup-image">Эксклюзивный дилер Impulsa AG с подразделением<br/>Itec в Республике Беларусь</a></div>
				<div class="brand-logos">
					<div class="brand-logos__item">
						<img class="brand-logos__image" src="img/brand-logo (1).jpg" alt=""/>
					</div>
					<div class="brand-logos__item">
						<img class="brand-logos__image" src="img/brand-logo (2).jpg" alt=""/>
					</div>
					<div class="brand-logos__item">
						<img class="brand-logos__image" src="img/brand-logo (3).jpg" alt=""/>
					</div>
					<div class="brand-logos__item">
						<img class="brand-logos__image" src="img/brand-logo (4).jpg" alt=""/>
					</div>
					<div class="brand-logos__item">
						<img class="brand-logos__image" src="img/brand-logo (5).jpg" alt=""/>
					</div>
					<div class="brand-logos__item">
						<img class="brand-logos__image" src="img/brand-logo (6).jpg" alt=""/>
					</div>
					<div class="brand-logos__item">
						<img class="brand-logos__image" src="img/brand-logo (7).jpg" alt=""/>
					</div>
					<div class="brand-logos__item">
						<img class="brand-logos__image" src="img/brand-logo (8).jpg" alt=""/>
					</div>
					<div class="brand-logos__item">
						<img class="brand-logos__image" src="img/brand-logo (9).jpg" alt=""/>
					</div>
					<div class="brand-logos__item">
						<img class="brand-logos__image" src="img/brand-logo (10).jpg" alt=""/>
					</div>
				</div>
			</div>
		</div>
		<div class="container-fluid" style="background-color: #fff; padding: 40px 0;">
			<div class="container">
				<div class="hero">
					<div class="hero-block hero-block_center">
						<div class="hero-block__icon">
							<i class="fa fa-clock"></i>
						</div>
						<div class="hero-block__title hero-block__title_small">Более 10 лет <br/> на рынке</div>
						<div class="hero-block__text hero-block__text_small">Более 10 лет опыта в обслуживании сельскохозяйственного оборудования</div>
					</div>
					<div class="hero-block hero-block_center">
						<div class="hero-block__icon">
							<i class="fa fa-users"></i>
						</div>
						<div class="hero-block__title hero-block__title_small">квалифицированный <br/> персонал</div>
						<div class="hero-block__text hero-block__text_small">Помощь квалифицированного персонала в подборе товара</div>
					</div>
					<div class="hero-block hero-block_center">
						<div class="hero-block__icon">
							<i class="fa fa-warehouse"></i>
						</div>
						<div class="hero-block__title hero-block__title_small">складские <br/> помещения</div>
						<div class="hero-block__text hero-block__text_small">Складские помещения с большим перечнем товаров</div>
					</div>
					<div class="hero-block hero-block_center">
						<div class="hero-block__icon">
							<i class="fa fa-wrench"></i>
						</div>
						<div class="hero-block__title hero-block__title_small">техническая <br/> поддержка</div>
						<div class="hero-block__text hero-block__text_small">24/7 сервисная, техническая поддержка</div>
					</div>
				</div>
			</div>
		</div>
		<div class="container-fluid p-t">
			<div class="container">
				<div class="page-map" id="contacts">
					<div class="page-map__map"><script type="text/javascript" charset="utf-8" async="async" src="https://api-maps.yandex.ru/services/constructor/1.0/js/?um=constructor%3A85d23034aeb71446d25bfc2b766314cfce48883a8cb39509d1b86e360cec37c7&amp;width=100%25&amp;height=360&amp;lang=ru_RU&amp;scroll=true"></script></div>
					<div class="page-map__text">
						<h3 class="page-map__title"><strong>Схема проезда и контакты</strong></h3>
						<p>223053, Республика Беларусь, Минская обл., Минский р-н., р-н д. Боровая, корп. 1—3.</p>
						<p>
							+375 17 283 94 17 - Тел./факс;<br/>
							+375 17 377 00 39 - Тел./факс;<br/>
							+375 29 101 05 13 - Velcom;<br/>
							+375 33 664 58 69 - МТС.
						</p>
						<p><a href="mailto:mtechservice2013@mail.ru">mtechservice2013@mail.ru</a></p>
					</div>
				</div>
			</div>
		</div>
		<!-- <div class="separator"></div>
		<div class="container container-tb">
			<iframe src="https://yandex.by/map-widget/v1/-/CBumiCENKC" width="100%" height="400" frameborder="0" allowfullscreen="true"></iframe>
		</div> -->
	</xsl:template>

	<xsl:template name="EXTRA_SCRIPTS">
		<script type="text/javascript" src="fotorama/fotorama.js"/>
	</xsl:template>

</xsl:stylesheet>
