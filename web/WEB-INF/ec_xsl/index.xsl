<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">
	<xsl:import href="common_page_base.xsl"/>
	<xsl:output method="xhtml" encoding="UTF-8" media-type="text/xhtml" indent="yes" omit-xml-declaration="yes"/>
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
					"name": "Керамическая плитка Керамо Маркет"
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
					<a href="{page/common/link_link2}"><xsl:value-of select="page/common/link_text2"/></a>
					<a href="{page/common/link_link3}"><xsl:value-of select="page/common/link_text3"/></a>
					<a href="{page/common/link_link4}"><xsl:value-of select="page/common/link_text4"/></a>
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
		<div class="has-slider">
			<div class="container">
				<div class="slider-container">
					<div class="fotorama" data-width="100%" data-maxwidth="100%" data-thumbheight="40" data-thumbwidth="40" data-autoplay="true" data-loop="true">
						<xsl:for-each select="page/main_page/main_slider_frame">
							<img src="{@path}{pic}" />
						</xsl:for-each>
					</div>
				</div>
			</div>
		</div>
		<!-- <div class="actions mobile">
			<h3>Акции</h3>
			<div class="actions-container">
				<a href="{page/main_page/link_link}"><xsl:value-of select="page/main_page/link_text"/></a>
			</div>
		</div> -->
		<div class="has-catalog-sections">
			<div class="container">
				<h1 class="big-title">
					Керамическая плитка - каталог продукции
				</h1>
				<div class="catalog-items sections">
					<xsl:for-each select="/page/catalog/section">
						<div class="catalog-item">
							<xsl:variable name="pic_path" select="if (main_pic) then concat(@path, main_pic) else 'img/no_image.png'"/>
							<a href="{if(section != '') then show_section else show_products}" class="image-container" style="background-image: url({$pic_path});">
								<!-- <img src="{$pic_path}" onerror="$(this).attr('src', 'img/no_image.png')"/> -->
							</a>
							<div class="name">
								<a href="{if(section != '') then show_section else show_products}"><span>
									<xsl:value-of select="name"/></span></a>
								<xsl:value-of select="short" disable-output-escaping="yes"/>
							</div>
						</div>
					</xsl:for-each>
				</div>
			</div>
		</div>
			<section class="about">
				<div class="container">
					<div class="title_1">О компании</div>
					<div class="wrap">
						<img src="img/about_photo.jpg" alt="" />
						<div class="about__text">
							<p><span style="font-size: 16px;">Компания "Керамо Маркет" <strong>с 2001 года</strong> занимается оптовыми и розничными поставками отделочных материалов. Мы реализуем более 3000 наименований для любых потребностей по самым доступным ценам!</span></p>
<p><span style="font-size: 16px;">В нашем магазине можно приобрести керамическую плитку для пола, ванной комнаты, кухни (коллекционную и неколлекционную), керамогранит Грес, керамическое декоративное панно, мозаику, клинкерную плитку, щебень.</span></p>
<p><span style="font-size: 16px;">Мы поставляем плитку от известных производителей - "АТЕМ", Нефрит-Керамика, "Сокол", "Контакт", "Брестский КСМ", ПРУП "Березастройматериалы" и др.</span></p>
<h2><span style="font-size: 24px;">Наши преимущества</span></h2>
<ul>
<li><span style="font-size: 16px;">Приобретая у нас керамическую плитку, вы получаете <strong>продукцию высокого качестваи вежливый сервис</strong>, <strong>гарантию на товар</strong> и <strong>удовольствие</strong> от удачной покупки!</span></li>
<li><span style="font-size: 16px;">У нас очень <strong>большой ассортимент</strong>, который постоянно пополняется новинками (мы следим за мировыми тенденциями).</span></li>
<li><span style="font-size: 16px;"><strong>АКЦИИ</strong>  мы периодически проводим акции и <a href="sub/superrasprodaga/">распродажи керамической плитки</a>, чтобы вы смогли желанный вариант.</span></li>
<li><span style="font-size: 16px;">В самые <strong>кратчайшие сроки</strong> вы получите товар и гарантийные документы.</span></li>
<li><span style="font-size: 16px;">Оплатить заказ можно <strong>наличными, банковской картой или через расчетный счет</strong> (безналичная оплата).</span></li>
</ul>
<p><span style="font-size: 24px;">Выбирайте и наслаждайтесь!</span></p>
<p><span style="font-size: 16px;">Закажите керамическую плитку прямо сейчас. Дополнительно мы разрабатываем интересные дизайн-проекты на любой вкус.</span></p>
<p><span style="font-size: 16px;">Сделать заказ можно:</span></p>
<ol>
<li><span style="font-size: 16px;">на сайте: выберите понравившуюся продукцию и свяжитесь с менеджером по контактным телефонам</span></li>
<li><span style="font-size: 16px;">в наших магазинах: адреса указаны в разделе Контакты (Минск, Брест)</span></li>
</ol>
<p><span style="font-size: 16px;"><strong>Мы ценим каждого клиента!</strong></span></p>
						</div>
					</div>
				</div>
			</section>
			<section class="stats">
				<div class="container">
					<div class="wrap">
						<div class="stats__item">
							<div class="stats__big">18</div>
							<div class="stats__text">лет на рынке</div>
						</div>
						<div class="stats__item">
							<div class="stats__big">194</div>
							<div class="stats__text">коллекции доступны</div>
						</div>
						<div class="stats__item">
							<div class="stats__big">1356</div>
							<div class="stats__text">вариантов плитки</div>
						</div>
					</div>
				</div>
			</section>
			<section class="recommended">
				<div class="container">
					<div class="title_1">
						Популярные коллекции керамической плитки
					</div>
					<div class="wrap">
						<div class="recommended__item">
							<img src="/files/120/310f/1305201669_2086.jpg" alt="" />
							<div class="recommended__title">Aragoza</div>
						</div>
						<div class="recommended__item">
							<img src="files/120/388f/axsrhv.jpg" alt="" />
							<div class="recommended__title">Arena YLT S</div>
						</div>
						<div class="recommended__item">
							<img src="/files/119/600f/full_300x600_silvia_b_floor_20144291632.jpg" alt="" />
							<div class="recommended__title">Silvia B</div>
						</div>
						<div class="recommended__item">
							<img src="/files/116/368f/full_veruso_flower_r_220h350_d_20152251457.jpg" alt="" />
							<div class="recommended__title">Veruso Flower</div>
						</div>
						<div class="recommended__item">
							<img src="/files/117/020f/full_200x500_romance_rose_1_w_20144251533.jpg" alt="" />
							<div class="recommended__title">Romance</div>
						</div>
					</div>
				</div>
			</section>
			<section class="icons">
				<div class="container">
					<div class="title_1">
						Нам доверяют
					</div>
					<div class="wrap">
						<div class="icons__item">
							<div class="icons__icon">
								<i class="fas fa-star"></i>
							</div>
							<div class="icons__title">Большой выбор плитки</div>
							<div class="icons__text">Коллекционная и неколлекционная плитка в большом ассортименте</div>
						</div>
						<div class="icons__item">
							<div class="icons__icon">
								<i class="fas fa-warehouse"></i>
							</div>
							<div class="icons__title">Свой склад-магазин</div>
							<div class="icons__text">Посетите наш магазин и выберите понравившийся вариант</div>
						</div>
						<div class="icons__item">
							<div class="icons__icon">
								<i class="fas fa-clock"></i>
							</div>
							<div class="icons__title">Опертивная работа</div>
							<div class="icons__text">Проконсультируем и оформим ваш заказ в короткие сроки</div>
						</div>
						<div class="icons__item">
							<div class="icons__icon">
								<i class="fas fa-award"></i>
							</div>
							<div class="icons__title">Нам доверяют тысячи клиентов</div>
							<div class="icons__text">Более 5000 клиентов стали нашими покупателями</div>
						</div>
					</div>
				</div>
			</section>
			<section class="testimonials">
				<div class="container">
					<div class="title_1">Отзывы покупателей</div>
					<div class="wrap">
						<div class="testimonials__item">
							<div class="testimonials__text">Покупал керамическую плитку для облицовки санузла. Получилось отлично. были вопросы, но все успешно решили!</div>
							<div class="testimonials__name">Олег, г. Минск</div>
						</div>
						<div class="testimonials__item">
							<div class="testimonials__text">Очень понравилась плитка мне и жене, выбрали на кухню. Отличное качество и великолепный дизайн. Рекомендую</div>
							<div class="testimonials__name">Дмитрий, Гомель </div>
						</div>
						<div class="testimonials__item">
							<div class="testimonials__text">Выражаю балгодарность магазину за оперативную работу и хороший сервис. Керамической плиткой очень довлен</div>
							<div class="testimonials__name">Сергей Владимирович, г. Брест</div>
						</div>
						<div class="testimonials__item">
							<div class="testimonials__text">Нашла и купила в Керамомаркете очень красивую плитку в нежных тонах. Спасибо что подобрали то, что мне надо)</div>
							<div class="testimonials__name">Наталья, г. Могилев</div>
						</div>
					</div>
				</div>
			</section>
			<section class="misc-info">
				<div class="container">
					<div class="wrap">
						<div>
							<div class="title_1">Оплата</div>
							<div class="title_2">Наличный расчет</div>
							<p>Оплата заказа осуществляется наличными при получении товара</p>
							<div class="title_2">Безналичный расчет</div>
							<p>Оплата банковской картой.</p>
							<div class="cards">
								<div class="card">
									<img src="img/card1.jpg" alt="" />
								</div>
								<div class="card">
									<img src="img/card2.jpg" alt="" />
								</div>
								<div class="card">
									<img src="img/card3.jpg" alt="" />
								</div>
								<div class="card">
									<img src="img/card4.jpg" alt="" />
								</div>
								<div class="card">
									<img src="img/card5.jpg" alt="" />
								</div>
								<div class="card">
									<img src="img/card6.jpg" alt="" />
								</div>
							</div>
						</div>
						<div>
							<div class="title_1">Доставка</div>
							<div class="title_2">Самовывоз из офиса продаж</div>
							<p>Получить заказ можно самостоятельно, предварительно согласовав время приезда и наличие с менеджером.</p>
							<div class="title_2">Точки самовывоза находится по адресам:</div>
							<p>Магазин-склад г.Минск, ул.Бабушкина 3 (п/з Колядичи)</p>
							<p>Магазин в г. Минске Стройрынок "Уручье" главная аллея пав. № 368 Б</p>
						</div>
					</div>
				</div>
			</section>
			<div class="has-news">
			<div class="container">
				<xsl:if test="page/main_page/link_text and not(page/main_page/link_text = '')">
					<div class="actions">
						<h4 class="big-title label-actions">&#160;</h4>
						<div class="actions-container">
							<a href="{page/common/link_link}"><xsl:value-of select="page/common/link_text"/></a>
						</div>
					</div>
				</xsl:if>
			</div>
			<div class="container">
				<div class="news">
					<h4 class="big-title label-news">&#160;</h4>
					<div class="news-container">
						<xsl:for-each select="page/news_wrap/news/news_item">
							<div>
								<a href="{show_news_item}"><xsl:value-of select="header"/></a>
								<div class="date"><xsl:value-of select="date"/></div>
							</div>
						</xsl:for-each>
					</div>
				</div>
			</div>
		</div>	
		
	</xsl:template>

	<xsl:template name="MAIN_CONTENT">
		
		<div class="container">
			<xsl:call-template name="INC_MOBILE_HEADER"/>
		</div>
		<xsl:call-template name="CONTENT"/>
					
	</xsl:template>

	<!-- <xsl:template name="BANNERS">
		<div class="has-banners">
			<div class="container">
				<div class="banners-container">
					<xsl:for-each select="page/main_page/main_promo_bottom">
						<div style="background-image: url({@path}{pic})">
							<div class="aspect-ratio"></div>
							<a href="">
								<h4><xsl:value-of select="text_big"/></h4>
								<p><xsl:value-of select="text_small"/></p>
							</a>
						</div>
				
						<a href="{link}" style="background-image: url({@path}{pic})">
							<h4><xsl:value-of select="text_big"/></h4>
							<p><xsl:value-of select="text_small"/></p>
						</a>
					</xsl:for-each>
				</div>
			</div>
		</div>
	</xsl:template> -->


	<xsl:template name="BANNERS">
		<div class="has-banners">
			<div class="container">
				<div class="banners-container">
					<xsl:for-each select="page/main_page/main_promo_bottom">
						<div class="banner">
							<div class="image-container" style="background-image: url({@path}{pic})">
								<div class="aspect-ratio"></div>
							</div>
							<div class="info">
								<h4><xsl:value-of select="text_big"/></h4>
								<p><xsl:value-of select="text_small"/></p>
							</div>
							<a href="{link}"></a>
						</div>
				
						<!-- <a href="{link}" style="background-image: url({@path}{pic})">
							
						</a> -->
					</xsl:for-each>
				</div>
			</div>
		</div>
	</xsl:template>

	<xsl:template name="EXTRA_SCRIPTS">
		<script type="text/javascript" src="fotorama/fotorama.js"/>
	</xsl:template>

</xsl:stylesheet>