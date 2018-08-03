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
		<div class="slider-container desktop">
			<div class="fotorama" data-transition="crossfade" data-width="100%" data-maxwidth="100%" data-thumbheight="40" data-thumbwidth="40" data-autoplay="true" data-loop="true">
				<xsl:for-each select="page/main_page/main_slider_frame">
					<div class="slider-item" data-img="img/desktop-placeholder.png" style="background-image: url({@path}{pic});">
						<div class="slider-item__block">
							<div class="slider-item__title">Лодочные моторы SUZUKI</div>
							<div class="slider-item__text">
								<p>Компания ТактСервис – единственный официальный дилер SUZUKI MOTORS CORPORATION, представляющий лодочные моторы SUZUKI и товары для судоходства SUZUKI на территории Республики Беларусь.</p>
							</div>
							<a href="" class="slider-item__button">Каталог продукции</a>
						</div>
					</div>
				</xsl:for-each>
			</div>
		</div>
		<div class="slider-container mobile">
			<div class="fotorama" data-width="100%" data-height="320" data-maxwidth="100%" data-thumbheight="40" data-thumbwidth="40" data-autoplay="false" data-loop="true">
				<xsl:for-each select="page/main_page/main_slider_frame">
					<div class="slider-item" style="background-image: url({@path}{pic});">
						<div class="slider-item__block">
							<div class="slider-item__title">Лодочные моторы SUZUKI</div>
							<div class="slider-item__text">
								<p>Компания ТактСервис – единственный официальный дилер SUZUKI MOTORS CORPORATION, представляющий лодочные моторы SUZUKI и товары для судоходства SUZUKI на территории Республики Беларусь.</p>
							</div>
							<a href="" class="slider-item__button">Перейти в каталог</a>
						</div>
					</div>
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

	<xsl:template name="BANNERS">
		<div class="container">
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

		<div class="container container-ptb" style="background-color: #f2f2f2;">
			<div class="free-cols">
				<div class="free-col">
					<h1>Бензоинструменты и двигатели. Сервис и продажи</h1>
					<strong>ТактСервис – это коллектив с большим опытом и знаниями! Мы создаем для клиентов условия обладающие особой ценностью!</strong>
					<ul>
						<li>Узкая специализация компании и сотрудничество с передовыми производителями бензиновых инструментов, двигателей, коммунальной, сельско-хозяйственной и лесной техники позволяет предоставлять качественный, авторизованный сервис и гарантию на выполненные работы/</li>
						<li>Прямой контакт с инженерными службами производителей оборудования позволяет нам решать самые сложные технические задачи.</li>
						<li>Мы поставляем оригинальные запасные части и расходные материалы.</li>
						<li>Мы не продаем бензомоторную технику «с витрины» или «курьером»! Каждый клиент имеет УНИКАЛЬНУЮ! возможность испытать приобретаемое или отремонтированное оборудование! А так же получить индивидуальный урок о безопасном и эффективном применении бензомоторной техники, коммунальных машин, лодочных моторов в общении с мастером сервисного центра ТактСервис!</li>
						<li>Удобное расположение, подъезд и парковка на любом виде индивидуального, общественного или грузового транспорта. В непосредственной близости от улицы Радиальной, Партизанского проспекта.</li>
						<li>Рассрочка на товары, услуги по ремонту и обслуживанию бензомоторной техники.</li>
					</ul>
					<h4>ТактСервис - официальный дилер:</h4>
					<p>
						<a href=""><strong>SUZUKI</strong><br/> навесные лодочные моторы</a>
					</p>
					<p>
						<a href=""><strong>STIHL, VIKING</strong><br/> газонокосилки, бензопилы кусторезы, мотокосы отрезные устройства, мотобуры, воздуходувки</a>
					</p>
					<p>
						<a href=""><strong>Briggs&amp;Stratton</strong><br/> двигатели для малой строительной, дорожной и сельскохозяйственной техники</a>
					</p>
					<p>
						<a href=""><strong>LASKI</strong><br/> дробилки дерева, пней, траншеекопатели техника для коммунального хозяйства из Чехии</a>
					</p>
					<p>
						<a href=""><strong>AS-Motor</strong><br/> газонокосилки для неровной местности профессионального назначения</a>
					</p>
					<p>
						<a href=""><strong>MASTER</strong><br/> нагреватели воздуха на жидком топливе, газе или электричестве. Для строительных работ и отопления</a>
					</p>
					<p>
						<a href=""><strong>UMS</strong><br/> катера и лодки из алюминия</a>
					</p>
				</div>
				<div class="free-col">
					<h1>Актуально в данный момент!</h1>
					<strong>Приятная новость!</strong>
					<p>Теперь вы можете купить товары в рассрочку и без первого взноса. С помощью карты рассрочки №1 "Халва" от МТБанка.</p>
					<p>
						<img src="img/image.jpg" alt=""/>
					</p>
					<p>Узнать об очень простой процедуре оформления карты вы можете по ссылке.</p>
					<hr/>
					<strong>Конкурентые преимущества STIHL</strong>
					<p>Детали поршневой группы бензопил, кусторезов, мотокос производятся по одинаковой технологи...</p>
					<p>
						<img src="img/image-1.jpg" alt=""/>
					</p>
					<hr/>
					<strong>Акция!!! Новый мотор для газонокосилок Briggs&amp;Stratton</strong>
					<p>
						<img src="img/image-2.jpg" alt=""/>
					</p>
					<p>Ремонт мотора дорого стоит? Нехватает запасных частей для быстрого ремонта? А если заменить мотор целиком?</p>
					<p>Подробнее о замене мотора узнайте у специалистов "ТактСервис", а так же в статье.</p>
					<hr/>
					<strong>Revolution!!! SUZUKI представляет DF 350 A 350 л.с. V6</strong>
					<p>
						<img src="img/image-3.jpg" alt=""/>
					</p>
				</div>
				<div class="free-col">
					<h4>Статьи и обзоры</h4>
					<p>
						<a href="">Какая бензопила лучше</a>
					</p>
					<p>
						<a href="">Осенняя АКЦИЯ STIHL 2017!!!</a>
					</p>
					<p>
						<a href="">Изгиб коленчатого вала</a>
					</p>
					<p>
						<a href="">Как определить Модель Тип Код мотора Briggs and Stratton</a>
					</p>
					<p>
						<a href="">Мотор Briggs and Stratton</a>
					</p>
					<p>
						<a href="">SUZUKI ПРЕДСТАВЛЯЕТ DF350A 350 Л.С. V6</a>
					</p>
					<p>
						<a href="">АКЦИЯ!!! АКЦИЯ!!! ВЕСНА!!! 2017</a>
					</p>
					<p>
						<a href="">Новые двигатели SUZUKI DF5A - начало официальных поставок.</a>
					</p>
					<p>
						<a href="">Мотокоса или газонокосилка на колесах или...</a>
					</p>
				</div>
			</div>
		</div>







	</xsl:template>

	<xsl:template name="EXTRA_SCRIPTS">
		<script type="text/javascript" src="fotorama/fotorama.js"/>
	</xsl:template>

</xsl:stylesheet>
