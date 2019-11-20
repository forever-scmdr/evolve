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
				"logo":"<xsl:value-of select="concat($base, '/img/logo.jpg')"/>",
				"aggregateRating": {
					"@type": "AggregateRating",
					"ratingCount": "53",
					"reviewCount": "53",
					"bestRating": "5",
					"ratingValue": "4,9",
					"worstRating": "1",
					"name": "Metabo"
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
						<a href="{show_products}"><xsl:value-of select="name"/></a>
					</div>
					<xsl:if test="section">
						<div class="popup-menu" style="display:none">
							<div class="popup-coloumn">
								<xsl:for-each select="section[position() &lt;= 8]">
									<div><a href="{show_products}"><xsl:value-of select="name"/></a></div>
								</xsl:for-each>
							</div>
							<xsl:if test="count(section) &gt; 8">
								<div class="popup-coloumn">
									<xsl:for-each select="section[position() &gt; 8]">
										<div><a href="{show_products}"><xsl:value-of select="name"/></a></div>
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
		<div class="container-fluid has-slider">
			<div class="row">
				<div class="col-md-12">
					<div class="slider-container">
						<div class="fotorama" data-width="100%" data-maxwidth="100%" data-thumbheight="40" data-thumbwidth="40" data-autoplay="true" data-loop="true">
							<xsl:for-each select="page/main_page/main_slider_frame">
								<xsl:if test="link and not(link = '')">
									<div data-img="{@path}{pic}"><a href="{link}">&#160;</a></div>
								</xsl:if>
								<xsl:if test="not(link) or link = ''">
									<img src="{@path}{pic}" />
								</xsl:if>
							</xsl:for-each>
						</div>
					</div>
				</div>
			</div>
		</div>

		<div class="container-fluid" style="background-color: #f2f2f2;">
			<div class="container">
				<div class="row">
					<div class="col-md-12 more-products p-t p-b">
						<h4>Продукция</h4>
						<div class="catalog-items main-page">
							<xsl:for-each select="page/main_page/cat_pic">
								<xsl:sort select="number(@id)" order="ascending"/>
								<xsl:variable name="sec" select="//page/catalog//section[@id = current()/@id]"/>
								<xsl:variable name="main_pic" select="if(not(main_pic != '')) then concat($sec/product[1]/@path, $sec/product[1]/gallery[1]) else concat(@path, main_pic)"/>
								<div class="catalog-item">
<!--									<xsl:value-of select="main_pic"/>-->
									<xsl:variable name="pic_path" select="if ($main_pic != '') then $main_pic else 'img/no_image.png'"/>
									<a href="{show_products}" class="image-container" style="background-image: url({$pic_path})"></a>
									<div>
										<a href="{show_products}" style="height: unset;"><xsl:value-of select="name"/></a>
										<xsl:value-of select="short" disable-output-escaping="yes"/>
									</div>
								</div>
							</xsl:for-each>
						</div>
						<a href="{page/catalog_link}" class="kvld-link_big">Смотреть всю продукцию</a>
					</div>
				</div>
			</div>
		</div>
		<xsl:if test="page/main_page/brand">
			<div class="container kvld-brands">
				<h4>Популярные бренды</h4>
				<div class="wrap">
					<xsl:for-each select="page/main_page/brand">
						<div class="kvld-brands__item">
							<a href="{show_brand}" class="kvld-brands__image">
								<img src="{@path}{pic}" alt=""/>
							</a>
						</div>
					</xsl:for-each>
				</div>
				<a href="{page/brands_link}" class="kvld-link_big">Все бренды</a>
			</div>
		</xsl:if>
		<!-- <div class="container">
			<div class="row">
				<div class="col-md-12">
					<div class="actions mobile">
						<h3>Акции</h3>
						<div class="actions-container">
							<a href="{page/main_page/link_link}"><xsl:value-of select="page/main_page/link_text"/></a>
						</div>
					</div>
				</div>
			</div>
		</div> -->
		<div class="container-fluid" style="background-color: #e6e6e6;">
			<div class="container p-t p-b">
				<div class="row">
					<div class="col-md-4 col-sm-12">
						<xsl:if test="page/main_page/link_text and not(page/main_page/link_text = '')">
							<div class="actions">
								<h3>Акции</h3>
								<div class="actions-container">
									<a href="{page/common/link_link}"><xsl:value-of select="page/common/link_text"/></a>
								</div>
							</div>
						</xsl:if>
					</div>
					<div class="col-md-8 news">
						<h3>Новости</h3>
						<div class="news-container">
							<xsl:for-each select="page/news/news_item">
								<div>
									<div class="date"><xsl:value-of select="date"/></div>
									<a href="{show_news_item}"><xsl:value-of select="header"/></a>
								</div>
							</xsl:for-each>
						</div>
					</div>
				</div>
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
					</div>
				</div>
				<!-- RIGHT COLOUMN END -->
			</div>
		</div>
		<xsl:call-template name="CONTENT"/>
		<!-- MAIN COLOUMNS END -->
	</xsl:template>

	<xsl:template name="BANNERS">
		<div class="container p-t p-b">
			<div class="row">
				<div class="col-xs-12 banners">
					<!-- <h3>Специальные предложения</h3> -->
					<div class="banners-container">
						<xsl:for-each select="page/main_page/main_promo_bottom">
							<div style="background-image: url({@path}{pic})">
								<div class="aspect-ratio"></div>
								<a href="{link}"></a>
							</div>

							<!-- <a href="{link}" style="background-image: url({@path}{pic})">
								<h4><xsl:value-of select="text_big"/></h4>
								<p><xsl:value-of select="text_small"/></p>
							</a> -->
						</xsl:for-each>
					</div>
				</div>
			</div>
		</div>
		<div class="container-fluid p-t p-b" style="background-color: #fff;">
			<div class="container new-text">
				<div>
					<div>
						<h2>О нас</h2>
						<p>Мы являемся надежным, профессиональным и официальным поставщиком оборудования, электроинструментов и аксессуаров бренда Metabo в Беларуси.</p>
					</div>
					<div>
						<h2>Выгодные цены</h2>
						<p>Наши цены очень конкуренты. Мы постоянно работаем над тем, чтобы купить электроинструменты Метабо у нас по самым выгодным оптовым ценам. На странице Акции вы найдете товары по самым горячим ценам на рынке.</p>
					</div>
					<div>
						<h2>Почему нас ценят клиенты</h2>
						<ol>
						<li>Нашим приоритетом является полное удовлетворение наших клиентов. Мы постоянно работаем над улучшением качества обслуживания. Если есть необходимость, мы стараемся рассматривать каждый случай индивидуально.</li>
						<li>С начала нашей деятельности мы часто получаем сообщения о высокой довлетворенности клиентов и положительную оценку наших покупателей, со многими из которых мы сотрудничаем уже много лет.</li>
						<li>Подтверждением доверия, которое оказывают нам наши клиенты, заключается в том, что они с удовольствием возвращаются к нам за новыми покупками.</li>
						</ol>
					</div>
				</div>
				<div>
					<h2>Ассортимент: более 300 видов, более 3000 товаров</h2>
					<p>Предлагаем нашим клиентам тщательно отобранный широкий спектр устройств и инструментов, которые предназначены как для профессионалов, так и для домашнего использования.</p>
					<p>Мы хорошо знаем электроинструменты, которое реализуем, и всегда рада помочь вам выбрать правильный инструмент, соответствующий вашим потребностям и ожиданиям. Все, что вам нужно сделать, это позвонить нам ко контактным телефонам или написать по электронной почте.</p>
					<p>Наше предложение включает в себя сотни продуктов Метабо. Вы найдете шлифовальные станки, дрели, шуруповерты, электропилы, пылесосы, деревообрабатывающие станки, аккумуляторный инструмент, аксессуары, принадлежности и многое другое. Наши продукты предлагаются во многих конфигурациях для лучшей адаптации к вашим требованиям. Мы постоянно работаем над расширением нашего предложения и введением в него горячих новинок. Каждый день мы готовим привлекательные акции и подарки, перед которыми сложно устоять.</p>
					<p>На все электроинструменты распространяется гарантия: 1 + 2 года (после регистрации на сайте 3 года).</p>
				</div>
			</div>
		</div>
		<div class="container">
			<div class="new-list-wrap">
				<h2 style="text-align:center"><span>КАТЕГОРИИ ТОВАРОВ</span></h2>
				<div class="new-list">
					<xsl:for-each select="page/catalog/section">
						<ul>
							<li><xsl:value-of select="name" /></li>
							<xsl:for-each select=".//section[not(section)]">
								<li><a href="{show_products}"><xsl:value-of select="name" /></a></li>
							</xsl:for-each>
						</ul>
					</xsl:for-each>
				</div>
				<div class="dim"></div>
				<a href="#" class="toggle-list" onclick="$('.new-list-wrap').toggleClass('show'); return false;"><span>Развернуть/Свернуть</span></a>
				<!-- добавить тегу .new-list-wrap класс .show Текст заменить на Свернуть-->
			</div>
		</div>
	</xsl:template>

	<xsl:template name="EXTRA_SCRIPTS">
		<script type="text/javascript" src="fotorama/fotorama.js"/>
	</xsl:template>

</xsl:stylesheet>