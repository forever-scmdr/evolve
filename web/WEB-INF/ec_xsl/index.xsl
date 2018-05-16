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
		<div class="container-fluid has-slider">
			<div class="container">
				<div class="row">
					<div class="col-md-12">
						<div class="slider-container">
							<div class="fotorama" data-width="100%" data-maxwidth="100%" data-thumbheight="40" data-thumbwidth="40" data-autoplay="true" data-loop="true">
								<xsl:for-each select="page/main_page/main_slider_frame">
									<img src="{@path}{pic}" />
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
									<a href="{show_section}" class="image-container" style="background-image: url({$pic_path})"></a>
									<div>
										<a href="{show_section}" style="height: unset;"><xsl:value-of select="name"/></a>
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
								<a href=""></a>
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