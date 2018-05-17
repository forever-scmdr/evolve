<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">
	<xsl:import href="common_page_base.xsl"/>
	<xsl:output method="xhtml" encoding="UTF-8" media-type="text/xhtml" indent="yes" omit-xml-declaration="yes"/>
	<xsl:strip-space elements="*"/>

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
				<h4 class="big-title">
					Каталог продукции
				</h4>
				<div class="catalog-items sections">
					<xsl:for-each select="/page/catalog/section">
						<div class="catalog-item">
							<xsl:variable name="pic_path" select="if (main_pic) then concat(@path, main_pic) else 'img/no_image.png'"/>
							<a href="{show_section}" class="image-container" style="background-image: url({$pic_path});">
								<!-- <img src="{$pic_path}" onerror="$(this).attr('src', 'img/no_image.png')"/> -->
							</a>
							<div class="name">
								<a href="{show_section}"><span>
									<xsl:value-of select="name"/></span></a>
								<xsl:value-of select="short" disable-output-escaping="yes"/>
							</div>
						</div>
					</xsl:for-each>
				</div>
			</div>
		</div>
		<!-- <div class="has-items-carousel">
			<div class="container">
				<div class="more-products">
					<h4 class="big-title">Лидеры продаж</h4>
					<div class="slick-slider catalog-items">
						<div class="catalog-item">
							<div class="tags">
								<span>Скидка</span>
							</div>
							<a href="catalog_item.html" class="image-container" style="background-image: url(/files/804/37f/main_pic_videokamera_st_702_pro_d.png);">
								<img src="/files/804/37f/main_pic_videokamera_st_702_pro_d.png" alt="" />
							</a>
							<div class="name">
								<a href="catalog_item.html" title="Видеокамера ST-702 PRO D">Видеокамера ST-702 PRO D</a>
							</div>
							<div class="price">
								<p><span>Старая цена</span>100 р.</p>
								<p><span>Новая цена</span>99 р.</p>
							</div>
							<div class="order">
								<div id="cart_list_{code}" class="product_purchase_container">
									<form action="">
										<input type="number" value="1" />
										<input type="submit" value="Заказать" />
									</form>
								</div>
								<div class="quantity">Осталось 12 шт.</div>
								<div class="links">
									<div id="compare_list_{code}">
										<span><a href="{to_compare}" ajax="true" ajax-loader-id="compare_list_{code}"><i class="fas fa-balance-scale"></i></a></span>
									</div>
									<div id="id=fav_list_{code}">
										<span><a href="{to_fav}" ajax="true" ajax-loader-id="fav_list_{code}"><i class="fas fa-star"></i></a></span>
									</div>
								</div>
							</div>
						</div>
						<div class="catalog-item">
							<div class="tags">
								<span>Скидка</span>
							</div>
							<a href="catalog_item.html" class="image-container" style="background-image: url(/files/804/37f/main_pic_videokamera_st_702_pro_d.png);">
								<img src="/files/804/37f/main_pic_videokamera_st_702_pro_d.png" alt="" />
							</a>
							<div class="name">
								<a href="catalog_item.html" title="Видеокамера ST-702 PRO D">Видеокамера ST-702 PRO D</a>
							</div>
							<div class="price">
								<p><span>Старая цена</span>100 р.</p>
								<p><span>Новая цена</span>99 р.</p>
							</div>
							<div class="order">
								<div id="cart_list_{code}" class="product_purchase_container">
									<form action="">
										<input type="number" value="1" />
										<input type="submit" value="Заказать" />
									</form>
								</div>
								<div class="quantity">Осталось 12 шт.</div>
								<div class="links">
									<div id="compare_list_{code}">
										<span><a href="{to_compare}" ajax="true" ajax-loader-id="compare_list_{code}"><i class="fas fa-balance-scale"></i></a></span>
									</div>
									<div id="id=fav_list_{code}">
										<span><a href="{to_fav}" ajax="true" ajax-loader-id="fav_list_{code}"><i class="fas fa-star"></i></a></span>
									</div>
								</div>
							</div>
						</div>
						<div class="catalog-item">
							<div class="tags">
								<span>Скидка</span>
							</div>
							<a href="catalog_item.html" class="image-container" style="background-image: url(/files/804/37f/main_pic_videokamera_st_702_pro_d.png);">
								<img src="/files/804/37f/main_pic_videokamera_st_702_pro_d.png" alt="" />
							</a>
							<div class="name">
								<a href="catalog_item.html" title="Видеокамера ST-702 PRO D">Видеокамера ST-702 PRO D</a>
							</div>
							<div class="price">
								<p><span>Старая цена</span>100 р.</p>
								<p><span>Новая цена</span>99 р.</p>
							</div>
							<div class="order">
								<div id="cart_list_{code}" class="product_purchase_container">
									<form action="">
										<input type="number" value="1" />
										<input type="submit" value="Заказать" />
									</form>
								</div>
								<div class="quantity">Осталось 12 шт.</div>
								<div class="links">
									<div id="compare_list_{code}">
										<span><a href="{to_compare}" ajax="true" ajax-loader-id="compare_list_{code}"><i class="fas fa-balance-scale"></i></a></span>
									</div>
									<div id="id=fav_list_{code}">
										<span><a href="{to_fav}" ajax="true" ajax-loader-id="fav_list_{code}"><i class="fas fa-star"></i></a></span>
									</div>
								</div>
							</div>
						</div>
						<div class="catalog-item">
							<div class="tags">
								<span>Скидка</span>
							</div>
							<a href="catalog_item.html" class="image-container" style="background-image: url(/files/804/37f/main_pic_videokamera_st_702_pro_d.png);">
								<img src="/files/804/37f/main_pic_videokamera_st_702_pro_d.png" alt="" />
							</a>
							<div class="name">
								<a href="catalog_item.html" title="Видеокамера ST-702 PRO D">Видеокамера ST-702 PRO D</a>
							</div>
							<div class="price">
								<p><span>Старая цена</span>100 р.</p>
								<p><span>Новая цена</span>99 р.</p>
							</div>
							<div class="order">
								<div id="cart_list_{code}" class="product_purchase_container">
									<form action="">
										<input type="number" value="1" />
										<input type="submit" value="Заказать" />
									</form>
								</div>
								<div class="quantity">Осталось 12 шт.</div>
								<div class="links">
									<div id="compare_list_{code}">
										<span><a href="{to_compare}" ajax="true" ajax-loader-id="compare_list_{code}"><i class="fas fa-balance-scale"></i></a></span>
									</div>
									<div id="id=fav_list_{code}">
										<span><a href="{to_fav}" ajax="true" ajax-loader-id="fav_list_{code}"><i class="fas fa-star"></i></a></span>
									</div>
								</div>
							</div>
						</div>
						<div class="catalog-item">
							<div class="tags">
								<span>Скидка</span>
							</div>
							<a href="catalog_item.html" class="image-container" style="background-image: url(/files/804/37f/main_pic_videokamera_st_702_pro_d.png);">
								<img src="/files/804/37f/main_pic_videokamera_st_702_pro_d.png" alt="" />
							</a>
							<div class="name">
								<a href="catalog_item.html" title="Видеокамера ST-702 PRO D">Видеокамера ST-702 PRO D</a>
							</div>
							<div class="price">
								<p><span>Старая цена</span>100 р.</p>
								<p><span>Новая цена</span>99 р.</p>
							</div>
							<div class="order">
								<div id="cart_list_{code}" class="product_purchase_container">
									<form action="">
										<input type="number" value="1" />
										<input type="submit" value="Заказать" />
									</form>
								</div>
								<div class="quantity">Осталось 12 шт.</div>
								<div class="links">
									<div id="compare_list_{code}">
										<span><a href="{to_compare}" ajax="true" ajax-loader-id="compare_list_{code}"><i class="fas fa-balance-scale"></i></a></span>
									</div>
									<div id="id=fav_list_{code}">
										<span><a href="{to_fav}" ajax="true" ajax-loader-id="fav_list_{code}"><i class="fas fa-star"></i></a></span>
									</div>
								</div>
							</div>
						</div>
						<div class="catalog-item">
							<div class="tags">
								<span>Скидка</span>
							</div>
							<a href="catalog_item.html" class="image-container" style="background-image: url(/files/804/37f/main_pic_videokamera_st_702_pro_d.png);">
								<img src="/files/804/37f/main_pic_videokamera_st_702_pro_d.png" alt="" />
							</a>
							<div class="name">
								<a href="catalog_item.html" title="Видеокамера ST-702 PRO D">Видеокамера ST-702 PRO D</a>
							</div>
							<div class="price">
								<p><span>Старая цена</span>100 р.</p>
								<p><span>Новая цена</span>99 р.</p>
							</div>
							<div class="order">
								<div id="cart_list_{code}" class="product_purchase_container">
									<form action="">
										<input type="number" value="1" />
										<input type="submit" value="Заказать" />
									</form>
								</div>
								<div class="quantity">Осталось 12 шт.</div>
								<div class="links">
									<div id="compare_list_{code}">
										<span><a href="{to_compare}" ajax="true" ajax-loader-id="compare_list_{code}"><i class="fas fa-balance-scale"></i></a></span>
									</div>
									<div id="id=fav_list_{code}">
										<span><a href="{to_fav}" ajax="true" ajax-loader-id="fav_list_{code}"><i class="fas fa-star"></i></a></span>
									</div>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div> -->
		
		
		<div class="has-news">
			<div class="container">
				<xsl:if test="page/main_page/link_text and not(page/main_page/link_text = '')">
					<div class="actions">
						<h4 class="big-title label-actions">Акции</h4>
						<div class="actions-container">
							<a href="{page/common/link_link}"><xsl:value-of select="page/common/link_text"/></a>
						</div>
					</div>
				</xsl:if>

				<div class="news">
					<h4 class="big-title label-news">Статьи</h4>
					<div class="news-container">
						<xsl:for-each select="page/news/news_item">
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