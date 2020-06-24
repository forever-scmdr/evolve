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
					"name": "Чип электроникс"
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




	<xsl:template name="CONTENT">
		<div class="page-content m-t">
			<section class="hero pb" style="position: relative; min-height: 400px;">
				<div class="fotorama index-slider" data-minwidth="100%" data-ratio="2/1">
					<xsl:for-each select="page/main_page/main_slider_frame">
						<div class="index-fotorama-slide">
							<div class="img" style="background-image: url('{concat(@path,pic)}');"></div>
							<div class="text">
								<div class="wrap">
									<h2><xsl:value-of select="name"/></h2>
									<xsl:value-of select="text" disable-output-escaping="yes"/>
								</div>
							</div>
							<a href="{link}" style="position: absolute; top:0; bottom: 0; left:0; right:0;" ></a>
						</div>
					</xsl:for-each>
				</div>
			</section>
			<section class="bannerz pt">
				<div class="container" style="">
					<xsl:apply-templates select="page/banner_section/banner"/>
				</div>
			</section>
		</div>
	</xsl:template>



	<xsl:template name="EXTRA_SCRIPTS">
		<script type="text/javascript" src="fotorama/fotorama.js"/>
	</xsl:template>

	<xsl:template match="banner">
		<div class="banner {extra_style}">
			<div class="banner__background" style="{background}"></div>
			<div class="banner__image">
				<xsl:choose>
					<xsl:when test="image_code and not(image_code = '')"><xsl:value-of select="image_code" disable-output-escaping="yes" /></xsl:when>
					<xsl:otherwise>
						<xsl:if test="image_pic and not(image_pic = '')"><img src="{@path}{image_pic}"/></xsl:if>
					</xsl:otherwise>
				</xsl:choose>
			</div>
			<div style="position: absolute; top:0; width: 100%;">
				<div class="banner__title"><xsl:value-of select="header" /></div>
				<div class="banner__text"><xsl:value-of select="text" disable-output-escaping="yes" /></div>
			</div>
			<a href="{link}" class="banner__link"></a>
		</div>
	</xsl:template>

</xsl:stylesheet>
