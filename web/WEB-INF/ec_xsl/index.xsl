<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:f="f:f" version="2.0">
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
		<div class="actions">
			<h3>Акции</h3>
			<div class="actions-container">
				<a href="">Что делать, если поломалась или разбилась сенсорная панель вашего телефона</a>
			</div>
		</div>
		<!-- <div class="contacts">
			<h3>Заказ и консультация</h3>
			<p><a href="tel:+375 29 537-11-00">+375 29 537-11-00</a> - тел./Viber</p>
			<p>Email <a href="">info@beltesto.by</a></p>
			<p><a href="">Схема проезда к офису</a></p>
		</div> -->
	</xsl:template>


	<xsl:template name="CONTENT">
		<div class="slider-container">
			<div>СЛАЙДЕР</div>
		</div>
		<div class="actions mobile">
			<h3>Акции</h3>
			<div class="actions-container">
				<a href="">Что делать, если поломалась или разбилась сенсорная панель вашего телефона</a>
			</div>
		</div>
		<div class="news">
			<h3>Новости</h3>
			<div class="news-container">
				<div>
					<a href="">Что делать, если поломалась или разбилась сенсорная панель вашего телефона</a>
				</div>
				<div>
					<a href="">Что делать, если поломалась или разбилась сенсорная панель вашего телефона</a>
				</div>
				<div>
					<a href="">Что делать, если поломалась или разбилась сенсорная панель вашего телефона</a>
				</div>
				<div>
					<a href="">Что делать, если поломалась или разбилась сенсорная панель вашего телефона</a>
				</div>

			</div>
		</div>
	</xsl:template>


	<xsl:template name="BANNERS">
		<div class="container p-t">
			<div class="row">
				<div class="col-xs-12 banners">
					<!-- <h3>Специальные предложения</h3> -->
					<div class="banners-container">
						<a href="">
							<h4>Камеры для смартфонов</h4>
							<p>Большой выбор комплектующих</p>
						</a>
						<a href="">
							<h4>Камеры для смартфонов</h4>
							<p>Большой выбор комплектующих</p>
						</a>
						<a href="">
							<h4>Камеры для смартфонов</h4>
							<p>Большой выбор комплектующих</p>
						</a>
						<a href="">
							<h4>Камеры для смартфонов</h4>
							<p>Большой выбор комплектующих</p>
						</a>
					</div>
				</div>
			</div>
		</div>
	</xsl:template>

</xsl:stylesheet>