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
			<div class="container">
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
		</div>

		<div class="container-fluid" style="background-color: #fff;">
			<div class="container">
				<div class="row">
					<div class="col-md-12 more-products p-t p-b">
						<h4>Продукция</h4>
						<div class="catalog-items main-page">
							<xsl:for-each select="page/cat_pics/section">
								<div class="catalog-item">
									<xsl:variable name="pic_path"
									              select="if (product/main_pic) then concat(product/@path, product/main_pic) else 'img/no_image.png'"/>
									<a href="{show_products}" class="image-container" style="background-image: url({$pic_path})"></a>
									<div>
										<a href="{show_products}" style="height: unset;"><xsl:value-of select="name"/></a>
										<xsl:value-of select="short" disable-output-escaping="yes"/>
									</div>
								</div>
							</xsl:for-each>
						</div>
					</div>
				</div>
			</div>
		</div>
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
		<div class="container p-t">
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
		<div class="container">
			<div class="new-list-wrap" style="display: none;">
				<div class="new-list">
					<ul>
						<li><a href="">Combinator Inox</a></li>
						<li><a href="">Flexiamant</a></li>
						<li><a href="">Flexiamant Convex, циркониевый корунд</a></li>
						<li><a href="">Flexiamant Super</a></li>
						<li><a href="">Flexiamant Super Convex, керамика</a></li>
						<li><a href="">Flexiamant Super Inox</a></li>
						<li><a href="">Flexiamant Super, алюминий</a></li>
						<li><a href="">Flexiamant Super, камень</a></li>
						<li><a href="">Flexiamant super, сталь</a></li>
						<li><a href="">Flexiamant super, сталь</a></li>
						<li><a href="">Flexiamant, нормальный корунд</a></li>
						<li><a href="">Flexiamant, сталь</a></li>
						<li><a href="">Flexiamant, сталь</a></li>
						<li><a href="">Flexiamant, циркониевый корунд</a></li>
						<li><a href="">Flexiarapid / Flexiamant Inox</a></li>
						<li><a href="">Flexiarapid Super Inox</a></li>
						<li><a href="">Fliesen-Diamantbohrkronen</a></li>
						<li><a href="">HSS-Co (кобальтовый сплав)</a></li>
					</ul>
					<ul>
						<li><a href="">Co (кобальтовый сплав)</a></li>
						<li><a href="">HSS-G (шлифованные)</a></li>
						<li><a href="">HSS-G (шлифованные)</a></li>
						<li><a href="">HSS-R (после роликовой прокатки)</a></li>
						<li><a href="">HSS-R (после роликовой прокатки)</a></li>
						<li><a href="">HSS-TiN (с покрытием TiN)</a></li>
						<li><a href="">Li-Ion</a></li>
						<li><a href="">Li-Ion</a></li>
						<li><a href="">LiHD</a></li>
						<li><a href="">Linienlaser</a></li>
						<li><a href="">M-Calibur</a></li>
						<li><a href="">M-Calibur</a></li>
						<li><a href="">MetaLoc</a></li>
						<li><a href="">Multi Cut</a></li>
						<li><a href="">Novoflex</a></li>
						<li><a href="">Novoflex stone</a></li>
						<li><a href="">Novoflex сталь</a></li>
						<li><a href="">Novoflex/ Novorapid, сталь</a></li>
						<li><a href="">Novorapid Inox</a></li>
						<li><a href="">Power Cut</a></li>
						<li><a href="">Precision Cut</a></li>
						<li><a href="">Schleifbänder 180 x 1550 mm</a></li>
					</ul>
					<ul>
						<li><a href="">SDS-max Meißel "classic"</a></li>
						<li><a href="">SDS-max Meißel "professional premium"</a></li>
						<li><a href="">SDS-plus Meißel "classic"</a></li>
						<li><a href="">SDS-plus Meißel "professional premium"</a></li>
						<li><a href="">Абразивы для дельташлифовальных машин</a></li>
						<li><a href="">Абразивы для ленточных напильников</a></li>
						<li><a href="">Абразивы для ленточных шлифовальных машин</a></li>
						<li><a href="">Абразивы для многофункциональных шлифовальных машин</a></li>
						<li><a href="">Абразивы для плоскошлифовальных машин</a></li>
						<li><a href="">Абразивы для шлифмашин с длинной ручкой</a></li>
						<li><a href="">Абразивы для шлифователей труб</a></li>
						<li><a href="">Абразивы для шлифователей угловых сварных швов</a></li>
						<li><a href="">Абразивы для эксцентриковых шлифовальных машин</a></li>
						<li><a href="">Адаптеры</a></li>
						<li><a href="">Адаптеры</a></li>
						<li><a href="">Адаптеры и соединения</a></li>
						<li><a href="">Аккумуляторная воздуходувка</a></li>
						<li><a href="">Аккумуляторная прямошлифовальная машина</a></li>
						<li><a href="">Аккумуляторная ручная дисковая пила</a></li>
						<li><a href="">Аккумуляторная сабельная пила</a></li>
						<li><a href="">Аккумуляторная угловая шлифовальная машина</a></li>
						<li><a href="">Аккумуляторная угловая шлифовальная машина с плоским редуктором</a></li>
						<li><a href="">Аккумуляторная щеточная машина</a></li>
					</ul>
					<ul>
						<li><a href="">Аккумуляторные блоки</a></li>
						<li><a href="">Аккумуляторные блоки</a></li>
						<li><a href="">Аккумуляторные дрели</a></li>
						<li><a href="">Аккумуляторные дрели-шуруповерты</a></li>
						<li><a href="">Аккумуляторные инструменты</a></li>
						<li><a href="">Аккумуляторные картриджные пистолеты</a></li>
						<li><a href="">Аккумуляторные комплекты Combo</a></li>
						<li><a href="">Аккумуляторные кусторезы</a></li>
						<li><a href="">Аккумуляторные ленточные пилы</a></li>
						<li><a href="">Аккумуляторные мешалки</a></li>
						<li><a href="">Аккумуляторные настольные дисковые пилы</a></li>
						<li><a href="">Аккумуляторные перфораторы</a></li>
						<li><a href="">Аккумуляторные радиоприемники</a></li>
						<li><a href="">Аккумуляторные торцовочные пилы</a></li>
						<li><a href="">Аккумуляторные ударные дрели</a></li>
						<li><a href="">Аккумуляторные фонари</a></li>
						<li><a href="">Аккумуляторные фрезы для обработки кромок</a></li>
						<li><a href="">Аккумуляторные шуруповерты для гипсокартона</a></li>
						<li><a href="">Аккумуляторные электролобзики</a></li>
						<li><a href="">Аккумуляторный адаптер питания</a></li>
						<li><a href="">Аккумуляторный заклепочный пистолет</a></li>
						<li><a href="">Аккумуляторный ленточный напильник</a></li>
						<li><a href="">Аккумуляторный пылесос</a></li>
						<li><a href="">Аккумуляторный резьборез</a></li>
						<li><a href="">Аккумуляторный рубанок</a></li>
					</ul>
					<ul>
						<li><a href="">Аккумуляторный ударный гайковерт</a></li>
						<li><a href="">Аккумуляторный универсальный инструмент</a></li>
						<li><a href="">Аккумуляторный шлифователь для труб</a></li>
						<li><a href="">Аккумуляторный шлифователь угловых сварных швов</a></li>
						<li><a href="">Алмазные коронки для подрозетников</a></li>
						<li><a href="">Алмазные отрезные круги</a></li>
						<li><a href="">Алмазные отрезные круги</a></li>
						<li><a href="">Алмазные отрезные круги для абразивных материалов</a></li>
						<li><a href="">Алмазные отрезные круги для бетона / твердых материалов</a></li>
						<li><a href="">Алмазные отрезные круги для плитки</a></li>
						<li><a href="">Алмазные сверла</a></li>
						<li><a href="">Алмазные сверла "Wet"</a></li>
						<li><a href="">Алмазные чашечные шлифовальные круги</a></li>
						<li><a href="">Алмазный фрезерный круг</a></li>
						<li><a href="">Базовые комплекты</a></li>
						<li><a href="">Базовые комплекты</a></li>
						<li><a href="">Бетон, камень, штукатурка</a></li>
						<li><a href="">Биметаллические коронки</a></li>
						<li><a href="">Быстрозажимной сверлильный патрон</a></li>
						<li><a href="">Быстроразъемные соединения</a></li>
						<li><a href="">Валики-оправки</a></li>
						<li><a href="">Вворачивание / крепеж</a></li>
						<li><a href="">Верхние фрезеры, фрезерно-шлифовальные двигатели</a></li>
						<li><a href="">Войлочные компактные круги "Unitized" - VKS</a></li>
						<li><a href="">Войлочные ленты</a></li>
						<li><a href="">Войлочные ленты</a></li>
						<li><a href="">Всасывающие трубы</a></li>
						<li><a href="">Всасывающие шланги</a></li>
						<li><a href="">Вспомогательные полировальные материалы</a></li>
						<li><a href="">Вставной ниппель</a></li>
					</ul>
					<ul>
						<li><a href="">Выпуклая коническая фреза (закругленная / F-форма)</a></li>
						<li><a href="">Выпуклая коническая фреза (остроконечная / G-форма)</a></li>
						<li><a href="">Высококачественная сталь</a></li>
						<li><a href="">Вытяжные колпаки</a></li>
						<li><a href="">Двойной ниппель</a></li>
						<li><a href="">Двойной редукционный ниппель</a></li>
						<li><a href="">Дельташлифовальная машина</a></li>
						<li><a href="">Деревообработка</a></li>
						<li><a href="">Детали из латуни</a></li>
						<li><a href="">Домовое водоснабжение</a></li>
						<li><a href="">Дрели</a></li>
						<li><a href="">Дрели на магнитной стойке</a></li>
						<li><a href="">Другие абразивы</a></li>
						<li><a href="">Другие принадлежности</a></li>
						<li><a href="">Другие принадлежности в области системных кофров</a></li>
						<li><a href="">Другие принадлежности для (ударных) дрелей</a></li>
						<li><a href="">Другие принадлежности для вворачивания / крепежа</a></li>
						<li><a href="">Другие принадлежности для дельташлифовальных машин</a></li>
						<li><a href="">Другие принадлежности для ленточных пил</a></li>
						<li><a href="">Другие принадлежности для ленточных шлифовальных машин</a></li>
						<li><a href="">Другие принадлежности для лобзиков</a></li>
						<li><a href="">Другие принадлежности для перфораторов / отбойных молотков</a></li>
						<li><a href="">Другие принадлежности для полировальных машин</a></li>
						<li><a href="">Другие принадлежности для полустационарных/стационарных пил</a></li>
						<li><a href="">Другие принадлежности для полустационарных/стационарных циркулярных пил</a></li>
						<li><a href="">Другие принадлежности для прямошлифовальных машин</a></li>
					</ul>
					<ul>
						<li><a href="">Другие принадлежности для прямошлифовальных машин</a></li>
						<li><a href="">Другие принадлежности для ручных рубанков</a></li>
						<li><a href="">Другие принадлежности для ручных циркулярных пил</a></li>
						<li><a href="">Другие принадлежности для сверления коронками</a></li>
						<li><a href="">Другие принадлежности для сверлильных патронов</a></li>
						<li><a href="">Другие принадлежности для торцовочно-усорезных пил</a></li>
						<li><a href="">Другие принадлежности для угловых шлифмашин</a></li>
						<li><a href="">Другие принадлежности для универсального инструмента</a></li>
						<li><a href="">Другие принадлежности для фрезеров / шлифователей для ремонта</a></li>
						<li><a href="">Другие принадлежности для шлифования / полирования</a></li>
						<li><a href="">Зажимные гайки</a></li>
						<li><a href="">Зажимы</a></li>
						<li><a href="">Зарядное устройство</a></li>
						<li><a href="">Защита от сухого хода</a></li>
						<li><a href="">Защита рук</a></li>
						<li><a href="">Защитные кожухи</a></li>
						<li><a href="">Защитный кожух шлифовальной чашки с принадлежностями</a></li>
						<li><a href="">Зенкеры</a></li>
						<li><a href="">Змеевидные сверла</a></li>
						<li><a href="">Зубила SDS-max</a></li>
						<li><a href="">Зубила SDS-plus</a></li>
						<li><a href="">Измерительная техника</a></li>
						<li><a href="">Класс качества "classic" / AC</a></li>
						<li><a href="">Класс качества "classic" / CC</a></li>
						<li><a href="">Класс качества "classic" / ТC</a></li>
						<li><a href="">Класс качества "professional" / AP</a></li>
						<li><a href="">Класс качества "professional" / CP</a></li>
						<li><a href="">Класс качества "professional" / TP</a></li>
						<li><a href="">Клеевые карандаши</a></li>
						<li><a href="">Клеящие пистолеты</a></li>
					</ul>
					<ul>
						<li><a href="">Ключ сверлильного патрона</a></li>
						<li><a href="">Ключи под два отверстия</a></li>
						<li><a href="">Комбинированные перфораторы SDS-max</a></li>
						<li><a href="">Компактные войлочные круги</a></li>
						<li><a href="">Комплект коронок для дерева</a></li>
						<li><a href="">Комплекты твердосплавных фрез</a></li>
						<li><a href="">Компрессоры</a></li>
						<li><a href="">Компрессоры для строительных площадок</a></li>
						<li><a href="">Коническая фреза (круглый конус / L-форма)</a></li>
						<li><a href="">Коническая фреза (острый конус / М-форма)</a></li>
						<li><a href="">Консоли для шлифовальной ленты</a></li>
						<li><a href="">Коронки</a></li>
						<li><a href="">Корончатое сверло HSS, длинное</a></li>
						<li><a href="">Корончатое сверло HSS, короткое</a></li>
						<li><a href="">Корончатые сверла</a></li>
						<li><a href="">Кромочные фрезеры</a></li>
						<li><a href="">Круги для мокрого шлифования</a></li>
						<li><a href="">Круглые проволочные щетки</a></li>
						<li><a href="">Кузовные пилы</a></li>
						<li><a href="">Куртки с подогревом от аккумулятора</a></li>
						<li><a href="">Кусторезы</a></li>
						<li><a href="">Лазерные дальномеры</a></li>
						<li><a href="">Ламельные шлифовальные валы</a></li>
						<li><a href="">Ламельные шлифовальные круги</a></li>
						<li><a href="">Ленточная шлифовальная машина</a></li>
						<li><a href="">Ленточные напильники</a></li>
					</ul>
					<ul>
						<li><a href="">Ленточные пилы</a></li>
						<li><a href="">Ленточные пильные полотна</a></li>
						<li><a href="">Ленточные пильные полотна для дерева и пластика</a></li>
						<li><a href="">Ленточные пильные полотна для металла</a></li>
						<li><a href="">Лобзики</a></li>
						<li><a href="">Лобзиковые пилки</a></li>
						<li><a href="">Лобзиковые пилки для особых сфер применения "Expert"</a></li>
						<li><a href="">Лобзиковые пилки для твердой древесины (биметаллические)</a></li>
						<li><a href="">Лобзиковые пилки по дереву</a></li>
						<li><a href="">Лобзиковые пилки по металлу</a></li>
						<li><a href="">Машина для мокрого шлифования</a></li>
						<li><a href="">Металлообработка</a></li>
						<li><a href="">Мешалки</a></li>
						<li><a href="">Мобильные компрессоры для цеха / мастерской</a></li>
						<li><a href="">Монтажные наборы для насосов</a></li>
						<li><a href="">Наборы SDS-plus</a></li>
						<li><a href="">Наборы бит</a></li>
						<li><a href="">Наборы бит</a></li>
						<li><a href="">Наборы для универсального инструмента</a></li>
						<li><a href="">Наборы корончатых сверл</a></li>
						<li><a href="">Наборы лобзиковых пилок</a></li>
						<li><a href="">Наборы пневматических инструментов</a></li>
						<li><a href="">Наборы принадлежностей / специальные предложения</a></li>
						<li><a href="">Наборы сверл</a></li>
						<li><a href="">Наборы сверл</a></li>
						<li><a href="">Наборы сверл для металла, камня, дерева</a></li>
						<li><a href="">Наборы сверл по бетону</a></li>
						<li><a href="">Наборы сверл по дереву</a></li>
					</ul>
					<ul>
						<li><a href="">Наборы сверл по камню</a></li>
						<li><a href="">Наборы сверл по металлу</a></li>
						<li><a href="">Наборы универсальных сверл</a></li>
						<li><a href="">Наборы фрез</a></li>
						<li><a href="">Наборы шлифовальных лент</a></li>
						<li><a href="">Наборы шлифовальных листов</a></li>
						<li><a href="">Наборы шлифовальных листов</a></li>
						<li><a href="">Наборы шлифовальных листов</a></li>
						<li><a href="">Наборы шлифовальных листов</a></li>
					</ul>
					<ul>
						<li><a href="">Насадки</a></li>
						<li><a href="">Насадки</a></li>
						<li><a href="">Насадки для перемешивания</a></li>
						<li><a href="">Насосы и насосные станции</a></li>
						<li><a href="">Настольные циркулярные пилы</a></li>
						<li><a href="">Настольные циркулярные пилы с протяжкой</a></li>
						<li><a href="">Неударопрочные сверлильные патроны</a></li>
						<li><a href="">Нормальный корунд</a></li>
						<li><a href="">Обдирочные круги</a></li>
						<li><a href="">Обдирочные круги для алюминия</a></li>
						<li><a href="">Обдирочные круги для камня</a></li>
						<li><a href="">Обдирочные круги для литья</a></li>
						<li><a href="">Обдирочные круги для обычной и высокосортной стали</a></li>
						<li><a href="">Обдирочные круги для стали</a></li>
					</ul>
					<ul>
						<li><a href="">Обдирочные круги для труб</a></li>
						<li><a href="">Опорная тарелка на липучке "Metabo Pyramid"</a></li>
						<li><a href="">Опорные тарелки для фибровых шлифовальных кругов</a></li>
						<li><a href="">Опорные тарелки и промежуточные круги</a></li>
						<li><a href="">Опорные тарелки с липучкой</a></li>
						<li><a href="">Опорные тарелки с липучкой</a></li>
						<li><a href="">Опорные фланцы</a></li>
						<li><a href="">Осушение</a></li>
						<li><a href="">Отбойные молотки SDS-max</a></li>
						<li><a href="">Отрезание, шлифование, фрезерование</a></li>
						<li><a href="">Отрезные и обдирочные работы</a></li>
						<li><a href="">Отрезные круги</a></li>
						<li><a href="">Отрезные круги</a></li>
						<li><a href="">Отрезные круги для алюминия</a></li>
						<li><a href="">Отрезные круги для камня</a></li>
						<li><a href="">Отрезные круги для обычной и высокосортной стали</a></li>
						<li><a href="">Отрезные круги для стали</a></li>
						<li><a href="">Отрезные пилы по металлу</a></li>
						<li><a href="">Перемешивание: без воздушных карманов</a></li>
						<li><a href="">Перемешивание: сверху вниз</a></li>
						<li><a href="">Перемешивание: снизу вверх</a></li>
						<li><a href="">Переходники</a></li>
						<li><a href="">Переходники</a></li>
						<li><a href="">Перовые сверла по дереву</a></li>
						<li><a href="">Перфораторные патроны с адаптером</a></li>
						<li><a href="">Перфораторы SDS-plus</a></li>
						<li><a href="">Перфораторы и отбойные молотки</a></li>
						<li><a href="">Пилы</a></li>
						<li><a href="">Пилы</a></li>
						<li><a href="">Пильные диски</a></li>
						<li><a href="">Пластиковый кофр</a></li>
						<li><a href="">Плоскошлифовальные машины</a></li>
					</ul>
					<ul>
						<li><a href="">Пневматика</a></li>
						<li><a href="">Пневматика</a></li>
						<li><a href="">Пневматические инструменты</a></li>
						<li><a href="">Пневматические краскораспылительные пистолеты</a></li>
						<li><a href="">Пневматические перфораторы</a></li>
						<li><a href="">Пневматические пескоструйные пистолеты</a></li>
						<li><a href="">Пневматические продувочные пистолеты</a></li>
						<li><a href="">Пневматические распылительные пистолеты</a></li>
						<li><a href="">Пневматические скобозабиватели / гвоздезабиватели</a></li>
						<li><a href="">Пневматические шланги</a></li>
						<li><a href="">Пневматические шланги с текстильной прослойкой</a></li>
						<li><a href="">Пневматический картриджный пистолет</a></li>
						<li><a href="">Пневматический смазочный шприц</a></li>
						<li><a href="">Подготовка сжатого воздуха</a></li>
						<li><a href="">Полив</a></li>
						<li><a href="">Полировальные машины</a></li>
					</ul>
					<ul>
						<li><a href="">Полировальные меховые круги</a></li>
						<li><a href="">Полировальный войлок на липучке</a></li>
						<li><a href="">Полотна сабельных пил</a></li>
						<li><a href="">Полотна сабельных пил для дерева и металла</a></li>
						<li><a href="">Полотна сабельных пил для особых сфер применения "Expert"</a></li>
						<li><a href="">Полотна сабельных пил по дереву</a></li>
						<li><a href="">Полотна сабельных пил по металлу</a></li>
						<li><a href="">Полустационарные машины</a></li>
						<li><a href="">Принадлежности</a></li>
						<li><a href="">Принадлежности для PowerGrip/ Power Maxx</a></li>
						<li><a href="">Принадлежности для аккумуляторных инструментов</a></li>
						<li><a href="">Принадлежности для аккумуляторных инструментов</a></li>
						<li><a href="">Принадлежности для аккумуляторных прожекторов</a></li>
						<li><a href="">Принадлежности для гидротехники и насосов</a></li>
						<li><a href="">Принадлежности для дрелей и шуруповертов "Quick"</a></li>
						<li><a href="">Принадлежности для компрессоров</a></li>
						<li><a href="">Принадлежности для краскораспылительных пистолетов</a></li>
						<li><a href="">Принадлежности для кромочных фрезеров</a></li>
						<li><a href="">Принадлежности для кузовных пил</a></li>
						<li><a href="">Принадлежности для листовых и высечных ножниц</a></li>
						<li><a href="">Принадлежности для мокрого шлифования</a></li>
						<li><a href="">Принадлежности для отбойных молотков</a></li>
						<li><a href="">Принадлежности для пескоструйных пистолетов</a></li>
						<li><a href="">Принадлежности для пневматических инструментов</a></li>
						<li><a href="">Принадлежности для полировальных машин</a></li>
						<li><a href="">Принадлежности для прямошлифовальных машин</a></li>
						<li><a href="">Принадлежности для пылесосов</a></li>
						<li><a href="">Принадлежности для пылеудаления</a></li>
						<li><a href="">Принадлежности для рубанков</a></li>
						<li><a href="">Принадлежности для садовых приборов</a></li>
						<li><a href="">Принадлежности для скобозабивателей/гвоздезабивателей</a></li>
						<li><a href="">Принадлежности для технических фенов</a></li>
						<li><a href="">Принадлежности для угловых шлифмашин с плоской головкой</a></li>
						<li><a href="">Принадлежности для угловых шлифовальных машин Inox</a></li>
						<li><a href="">Принадлежности для ударных гайковертов, винтовертов с трещоткой</a></li>
						<li><a href="">Принадлежности для универсального инструмента</a></li>
						<li><a href="">Принадлежности для фрезеров</a></li>
						<li><a href="">Принадлежности для фрезеров для снятия лака</a></li>
						<li><a href="">Принадлежности для шлифовальных машин с двумя кругами</a></li>
						<li><a href="">Принадлежности для штроборезов</a></li>
						<li><a href="">Принадлежности для шуруповертов для гипсокартона</a></li>
						<li><a href="">Принадлежности к ударным гайковертам</a></li>
						<li><a href="">Проволочные щетки</a></li>
						<li><a href="">Проволочные щетки, высокосортная сталь</a></li>
					</ul>
					<ul>
						<li><a href="">Проволочные щетки, сталь</a></li>
						<li><a href="">Промышленные держатели</a></li>
						<li><a href="">Прочее</a></li>
						<li><a href="">Прочее</a></li>
						<li><a href="">Прочее</a></li>
						<li><a href="">Прочее</a></li>
						<li><a href="">Прочие принадлежности для шлифмашин с длинной ручкой</a></li>
						<li><a href="">Прямошлифовальные машины</a></li>
						<li><a href="">Пылезащитные фильтры</a></li>
						<li><a href="">Пылеотсос и пылеудаление</a></li>
						<li><a href="">Радиусная фреза</a></li>
						<li><a href="">Распределение сжатого воздуха</a></li>
						<li><a href="">Распределители сжатого воздуха</a></li>
						<li><a href="">Рейсмусовые и строгальные станки</a></li>
						<li><a href="">Рубанки</a></li>
						<li><a href="">Рукоятки</a></li>
						<li><a href="">Ручные дисковые пилы</a></li>
						<li><a href="">Сабельные пилы</a></li>
						<li><a href="">Сверла SDS-max "Pro 4"</a></li>
						<li><a href="">Сверла SDS-plus "classic"</a></li>
						<li><a href="">Сверла SDS-plus "Pro 4 Premium"</a></li>
						<li><a href="">Сверла SDS-plus "Pro 4"</a></li>
						<li><a href="">Сверла для опалубки</a></li>
						<li><a href="">Сверла из искусственного материала</a></li>
						<li><a href="">Сверла по бетону "classic"</a></li>
						<li><a href="">Сверла по бетону "pro"</a></li>
						<li><a href="">Сверла по бетону / камню</a></li>
						<li><a href="">Сверла по дереву</a></li>
						<li><a href="">Сверла по дереву с шестигранным хвостовиком (E 6.3)</a></li>
						<li><a href="">Сверла по дереву хромованадиевые (CV)</a></li>
						<li><a href="">Сверла по камню</a></li>
						<li><a href="">Сверла по металлу</a></li>
						<li><a href="">Сверла по металлу с шестигранным хвостовиком (E 6.3)</a></li>
						<li><a href="">Сверла по стеклу</a></li>
						<li><a href="">Сверла Форстнера</a></li>
						<li><a href="">Сверла/зубила SDS-max</a></li>
						<li><a href="">Сверла/зубила SDS-plus</a></li>
						<li><a href="">Сверление / долбление</a></li>
						<li><a href="">Сверление, завинчивание, долбление, перемешивание</a></li>
						<li><a href="">Сверлильные коронки</a></li>
						<li><a href="">Сверлильные коронки с внутренней резьбой M16</a></li>
						<li><a href="">Сверлильные коронки с дюймовой резьбой</a></li>
						<li><a href="">Сверлильные патроны</a></li>
					</ul>
					<ul>
						<li><a href="">Сверлильные патроны с адаптером</a></li>
						<li><a href="">Сверлильные стойки</a></li>
						<li><a href="">Сверлильный патрон "Quick"</a></li>
						<li><a href="">Сверлильный патрон с зубчатым венцом</a></li>
						<li><a href="">Системы кофров</a></li>
						<li><a href="">Скобозабиватели</a></li>
						<li><a href="">Скобы и гвозди для скобозабивателей</a></li>
						<li><a href="">Соединительный штуцер для шлангов</a></li>
						<li><a href="">Специальные принадлежности</a></li>
						<li><a href="">Спиральные шланги</a></li>
						<li><a href="">Строгальные ножи</a></li>
						<li><a href="">Строительные циркулярные пилы</a></li>
						<li><a href="">Сумки для инструментов</a></li>
						<li><a href="">Тарельчатые щетки из синтетических материалов</a></li>
						<li><a href="">Твердосплавное корончатое сверло</a></li>
						<li><a href="">Твердосплавные фрезы</a></li>
						<li><a href="">Технические фены</a></li>
						<li><a href="">Торцовочные и настольные циркулярные пилы</a></li>
						<li><a href="">Торцовочные пилы</a></li>
						<li><a href="">Угловая шлифмашина</a></li>
						<li><a href="">Угловая шлифовальная машина Ø100-150 мм</a></li>
						<li><a href="">Угловая шлифовальная машина Ø180-230 мм</a></li>
						<li><a href="">Угловые насадки</a></li>
						<li><a href="">Угловые полировальные машины</a></li>
						<li><a href="">Угловые шлифовальные машины с плоской головкой</a></li>
						<li><a href="">Удаление пыли</a></li>
						<li><a href="">Удаление пыли</a></li>
						<li><a href="">Ударные дрели</a></li>
						<li><a href="">Ударный гайковерт</a></li>
						<li><a href="">Ударопрочные сверлильные патроны</a></li>
						<li><a href="">Универсальная коронка "pionier"</a></li>
						<li><a href="">Универсальные пылесосы</a></li>
					</ul>
					<ul>
						<li><a href="">Универсальные сверла</a></li>
						<li><a href="">Универсальный инструмент</a></li>
						<li><a href="">Установки всасывания опилок</a></li>
						<li><a href="">Фетровые ленты</a></li>
						<li><a href="">Фетровый полировальный круг</a></li>
						<li><a href="">Фибровые шлифовальные круги</a></li>
						<li><a href="">Фильтровальные кассеты и другие принадлежности</a></li>
						<li><a href="">Фильтровальные мешки</a></li>
						<li><a href="">Фильтры и принадлежности</a></li>
						<li><a href="">Фреза в форме языка пламени (H-форма)</a></li>
						<li><a href="">Фрезер / шлифователь для ремонта</a></li>
						<li><a href="">Фрезер для снятия лака</a></li>
						<li><a href="">Фрезерные головки / звезды</a></li>
						<li><a href="">Фрезерные коронки</a></li>
						<li><a href="">Фрезерные сверла</a></li>
						<li><a href="">Фрезеры для ремонтных работ</a></li>
						<li><a href="">Фрезы из твердого сплава для алюминия</a></li>
					</ul>
					<ul>
						<li><a href="">Хвостовики / центровочные сверла</a></li>
						<li><a href="">Центрирующие штифты</a></li>
						<li><a href="">Цилиндрическая фреза (с закругленным концом / С-форма)</a></li>
						<li><a href="">Цилиндрическая фреза (цилиндр / B-форма)</a></li>
						<li><a href="">Цирконовый корунд</a></li>
						<li><a href="">Чашечные шлифовальные круги (керамические)</a></li>
						<li><a href="">Чашечные шлифовальные круги, камень</a></li>
						<li><a href="">Чашечные шлифовальные круги, сталь</a></li>
						<li><a href="">Чистящее полотно</a></li>
						<li><a href="">Шарообразная фреза (шар / D-форма)</a></li>
						<li><a href="">Шарообразная фреза для алюминия (шар / D-форма)</a></li>
						<li><a href="">Шинные манометры</a></li>
						<li><a href="">Шланги и фурнитура</a></li>
						<li><a href="">Шланговые барабаны</a></li>
						<li><a href="">Шлифовальные / войлочные гильзы</a></li>
						<li><a href="">Шлифовальные губки на липучке</a></li>
						<li><a href="">Шлифовальные губки на липучке</a></li>
						<li><a href="">Шлифовальные кольца</a></li>
						<li><a href="">Шлифовальные круги, карбид кремния</a></li>
						<li><a href="">Шлифовальные круги, нормальный корунд</a></li>
						<li><a href="">Шлифовальные круги, электрокорунд высшего качества</a></li>
						<li><a href="">Шлифовальные ленты</a></li>
						<li><a href="">Шлифовальные ленты</a></li>
						<li><a href="">Шлифовальные ленты "Metabo Pyramid"</a></li>
						<li><a href="">Шлифовальные ленты 75 x 533 мм</a></li>
					</ul>
					<ul>
						<li><a href="">Шлифовальные ленты из нормального корунда</a></li>
						<li><a href="">Шлифовальные ленты с керамическим зерном</a></li>
						<li><a href="">Шлифовальные ленты с керамическим зерном</a></li>
						<li><a href="">Шлифовальные листы для дерева, серия "classic"</a></li>
						<li><a href="">Шлифовальные листы на липучке "Metabo Pyramid"</a></li>
						<li><a href="">Шлифовальные листы на липучке 225 мм, 19 отверстий</a></li>
						<li><a href="">Шлифовальные машины для ремонта</a></li>
						<li><a href="">Шлифовальные машины для стен и потолков</a></li>
						<li><a href="">Шлифовальные машины с двумя кругами</a></li>
						<li><a href="">Шлифовальные пластины</a></li>
						<li><a href="">Шлифовальные пластины с липучкой</a></li>
						<li><a href="">Шлифовальные решетки для шлифмашин с длинной ручкой</a></li>
						<li><a href="">Шлифовальные циркониево-корундовые ленты</a></li>
						<li><a href="">Шлифовальные циркониево-корундовые ленты</a></li>
						<li><a href="">Шлифовальный войлок для шлифмашин с длинной ручкой</a></li>
						<li><a href="">Шлифование / полирование</a></li>
						<li><a href="">Шлифование, полирование, перемешивание</a></li>
						<li><a href="">Шлифователь для труб</a></li>
						<li><a href="">Шлифователь угловых сварных швов</a></li>
						<li><a href="">Штативы для резания</a></li>
						<li><a href="">Штроборезы</a></li>
						<li><a href="">Штуцеры для шлангов</a></li>
						<li><a href="">Шуруповерт для гипсокартона</a></li>
						<li><a href="">Шуруповерты</a></li>
						<li><a href="">Эксцентриковые шлифовальные машины</a></li>
						<li><a href="">Эллипсообразная фреза (эллипс / E-форма)</a></li>
						<li><a href="">Ящики из листовой стали для переноски</a></li>
					</ul>
				</div>
			</div>
			<div class="dim"></div>
		</div>
	</xsl:template>

	<xsl:template name="EXTRA_SCRIPTS">
		<script type="text/javascript" src="fotorama/fotorama.js"/>
	</xsl:template>

</xsl:stylesheet>