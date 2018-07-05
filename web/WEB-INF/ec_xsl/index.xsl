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
					"name": "Белорусдом"
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
		<div class="slider-container">
			<div class="fotorama" data-width="100%" data-maxwidth="100%" data-thumbheight="40" data-thumbwidth="40" data-autoplay="true" data-loop="true">
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

		<!-- <div class="more-products">
			<h4>Лидеры продаж</h4>
			<div class="slick-slider catalog-items">
				<div class="catalog-item">
					<div class="tags">
						<span>Акция</span>
					</div>
					<a href="catalog_item.html" class="image-container"><img src="img/no_image.png" alt="" /></a>
					<div>
						<a href="catalog_item.html">Вилочный погрузчик NISSAN NJ01M15</a>
					</div>
					<div class="price">
						<p><span>Старая цена</span>100 р.</p>
						<p><span>Новая цена</span>99 р.</p>
					</div>
					<div class="order">
						<input type="number" value="1" />
						<input type="submit" value="Заказать" />
						<div class="quantity">Осталось 12 шт.</div>
						<div class="checkbox">
							<label>
								<input type="checkbox" /> cравнение
							</label>
						</div>
					</div>
				</div>
				<div class="catalog-item">
					<div class="tags">
						<span>Акция</span>
					</div>
					<a href="catalog_item.html" class="image-container"><img src="img/no_image.png" alt="" /></a>
					<div>
						<a href="catalog_item.html">Вилочный погрузчик NISSAN NJ01M15</a>
					</div>
					<div class="price">
						<p><span>Старая цена</span>100 р.</p>
						<p><span>Новая цена</span>99 р.</p>
					</div>
					<div class="order">
						<input type="number" value="1" />
						<input type="submit" value="Заказать" />
						<div class="quantity">Осталось 12 шт.</div>
						<div class="checkbox">
							<label>
								<input type="checkbox" /> cравнение
							</label>
						</div>
					</div>
				</div>
				<div class="catalog-item">
					<div class="tags">
						<span>Акция</span>
					</div>
					<a href="catalog_item.html" class="image-container"><img src="img/no_image.png" alt="" /></a>
					<div>
						<a href="catalog_item.html">Вилочный погрузчик NISSAN NJ01M15</a>
					</div>
					<div class="price">
						<p><span>Старая цена</span>100 р.</p>
						<p><span>Новая цена</span>99 р.</p>
					</div>
					<div class="order">
						<input type="number" value="1" />
						<input type="submit" value="Заказать" />
						<div class="quantity">Осталось 12 шт.</div>
						<div class="checkbox">
							<label>
								<input type="checkbox" /> cравнение
							</label>
						</div>
					</div>
				</div>
				<div class="catalog-item">
					<div class="tags">
						<span>Акция</span>
					</div>
					<a href="catalog_item.html" class="image-container"><img src="img/no_image.png" alt="" /></a>
					<div>
						<a href="catalog_item.html">Вилочный погрузчик NISSAN NJ01M15</a>
					</div>
					<div class="price">
						<p><span>Старая цена</span>100 р.</p>
						<p><span>Новая цена</span>99 р.</p>
					</div>
					<div class="order">
						<input type="number" value="1" />
						<input type="submit" value="Заказать" />
						<div class="quantity">Осталось 12 шт.</div>
						<div class="checkbox">
							<label>
								<input type="checkbox" /> cравнение
							</label>
						</div>
					</div>
				</div>
				<div class="catalog-item">
					<div class="tags">
						<span>Акция</span>
					</div>
					<a href="catalog_item.html" class="image-container"><img src="img/no_image.png" alt="" /></a>
					<div>
						<a href="catalog_item.html">Вилочный погрузчик NISSAN NJ01M15</a>
					</div>
					<div class="price">
						<p><span>Старая цена</span>100 р.</p>
						<p><span>Новая цена</span>99 р.</p>
					</div>
					<div class="order">
						<input type="number" value="1" />
						<input type="submit" value="Заказать" />
						<div class="quantity">Осталось 12 шт.</div>
						<div class="checkbox">
							<label>
								<input type="checkbox" /> cравнение
							</label>
						</div>
					</div>
				</div>
				<div class="catalog-item">
					<div class="tags">
						<span>Акция</span>
					</div>
					<a href="catalog_item.html" class="image-container"><img src="img/no_image.png" alt="" /></a>
					<div>
						<a href="catalog_item.html">Вилочный погрузчик NISSAN NJ01M15</a>
					</div>
					<div class="price">
						<p><span>Старая цена</span>100 р.</p>
						<p><span>Новая цена</span>99 р.</p>
					</div>
					<div class="order">
						<input type="number" value="1" />
						<input type="submit" value="Заказать" />
						<div class="quantity">Осталось 12 шт.</div>
						<div class="checkbox">
							<label>
								<input type="checkbox" /> cравнение
							</label>
						</div>
					</div>
				</div>
				<div class="catalog-item">
					<div class="tags">
						<span>Акция</span>
					</div>
					<a href="catalog_item.html" class="image-container"><img src="img/no_image.png" alt="" /></a>
					<div>
						<a href="catalog_item.html">Вилочный погрузчик NISSAN NJ01M15</a>
					</div>
					<div class="price">
						<p><span>Старая цена</span>100 р.</p>
						<p><span>Новая цена</span>99 р.</p>
					</div>
					<div class="order">
						<input type="number" value="1" />
						<input type="submit" value="Заказать" />
						<div class="quantity">Осталось 12 шт.</div>
						<div class="checkbox">
							<label>
								<input type="checkbox" /> cравнение
							</label>
						</div>
					</div>
				</div>
			</div>
		</div> -->

		<!-- <div class="news">
			<h3>Новости</h3>
			<div class="news-container">
				<xsl:for-each select="page/news/news_item">
					<div>
						<a href="{show_news_item}"><xsl:value-of select="header"/></a>
					</div>
				</xsl:for-each>
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
						<!-- <xsl:call-template name="SEO_TEXT"/> -->
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
		<div class="container">
			<div class="row">
				<div class="col-xs-12 banners">
					<div class="banners-container">
						<xsl:for-each select="page/main_page/main_promo_bottom">
							<div>
								<div>
									<div class="aspect-ratio" style="background-image: url({@path}{pic})"></div>
									<a href="{link}">
										<h4><xsl:value-of select="text_big"/></h4>
									</a>
								</div>
								<p><xsl:value-of select="text_small"/></p>
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
	</xsl:template>

	<xsl:template name="EXTRA_SCRIPTS">
		<script type="text/javascript" src="fotorama/fotorama.js"/>
	</xsl:template>

</xsl:stylesheet>
