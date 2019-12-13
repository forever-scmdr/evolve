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




	<xsl:template name="CONTENT"></xsl:template>


	<xsl:template match="section">
		<div class="catalog-index__cell">
			<div class="block-title"><xsl:value-of select="name" /></div>
			<div class="catalog-index__group">
				<xsl:for-each select="section">
					<div class="catalog-index__item">
						<a href="{show_products}"><img src="{@path}{if(icon != '') then icon else main_pic}" alt=""/></a>
						<a href="{show_products}"><xsl:value-of select="name" /></a>
					</div>
				</xsl:for-each>
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



	<xsl:template match="banner">
		<div class="banner {extra_style}">
			<div class="banner__background" style="{background}"></div>
			<div class="banner__title"><xsl:value-of select="header" /></div>
			<div class="banner__text"><xsl:value-of select="text" disable-output-escaping="yes" /></div>
			<div class="banner__image">
				<xsl:choose>
					<xsl:when test="image_code and not(image_code = '')"><xsl:value-of select="image_code" disable-output-escaping="yes" /></xsl:when>
					<xsl:otherwise>
						<xsl:if test="image_pic and not(image_pic = '')"><img src="{@path}{image_pic}"/></xsl:if>
					</xsl:otherwise>
				</xsl:choose>
			</div>
			<a href="{link}" class="banner__link"></a>
		</div>
	</xsl:template>

	<xsl:template name="HERO">
		<section class="hero">
			<div class="fotorama" data-width="100%" data-height="80px" data-fit="cover" data-autoplay="true" data-nav="false" data-transition="crossfade">
				<xsl:for-each select="page/main_page/main_slider_frame">
					<div class="slider-item" data-img="img/desktop-placeholder.png" style="background-image: url({@path}{pic});">
						<a href="{link}" style="display: block; position: absolute; top: 0; right: 0; bottom: 0; left: 0"></a>
						<!-- <div class="slider-item__block fotorama__select">
							<div class="slider-item__wrapper">
								<div class="slider-item__title"><xsl:value-of select="name" /></div>
								<a href="{link}" class="slider-item__button"><xsl:value-of select="link_name" disable-output-escaping="yes"/></a>
							</div>
						</div> -->
					</div>
				</xsl:for-each>
			</div>
		</section>
	</xsl:template>

	<xsl:template name="BANNERS">
		<section class="catalog-index pt pb">
			<div class="container">
				<xsl:apply-templates select="page/catalog/section[1]"/>
				<xsl:apply-templates select="page/catalog/section[2]"/>
				<xsl:apply-templates select="page/catalog/section[3]"/>
				<div class="catalog-index__cell">
					<!-- <div class="block-title">1</div> -->
					<div class="banner rexant">
						<div class="banner__title">Электротехника</div>
						<div class="banner__text">
							<xsl:for-each select="page/catalog/section[4]/section">
								<a href="{show_products}"><xsl:value-of select="name" /><xsl:if test="position() != last()">; </xsl:if></a>
							</xsl:for-each>
						</div>
						<div class="banner__image"><img src="/files/353/225f/rexant_logo.png" alt=""/></div>
						<a class="banner__link" href="{page/catalog/section[4]/show_products}">Купить здесь</a>
					</div>
				</div>
			</div>
		</section>

		<section class="special-items">
			<div class="container">
				<div class="block-title">Новинки</div>
				<div class="special-items__devices slick-slider zu">
					<xsl:apply-templates select="page/main_page/product[tag='Новинка']" mode="special"/>
				</div>
			</div>
		</section>


	</xsl:template>

	<xsl:template name="EXTRA_SCRIPTS">
		<script type="text/javascript" src="fotorama/fotorama.js"/>
	</xsl:template>

</xsl:stylesheet>
