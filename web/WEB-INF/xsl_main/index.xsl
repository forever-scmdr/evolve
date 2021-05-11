<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">
	<xsl:import href="common_page_base.xsl"/>
	<xsl:import href="templates.xsl"/>
	<xsl:output method="html" encoding="UTF-8" media-type="text/xhtml" indent="yes" omit-xml-declaration="yes"/>
	<xsl:strip-space elements="*"/>

	<xsl:template name="MARKUP">
		<script type="application/ld+json">
			{
				"@context":"http://schema.org",
				"@type":"Organization",
				"url":"<xsl:value-of select="$main_host"/>/",
				"name":"<xsl:value-of select="$title"/>",
				"logo":"<xsl:value-of select="concat($domain/@path, $domain/logo)"/>",
				"aggregateRating": {
					"@type": "AggregateRating",
					"ratingCount": "53",
					"reviewCount": "53",
					"bestRating": "5",
					"ratingValue": "4,9",
					"worstRating": "1",
					"name": "<xsl:value-of select="$domain/name"/>"
				},
				"contactPoint": [
					<xsl:for-each select="$common/phone" >
						<xsl:if test="position() != 1">,</xsl:if>{
						"@type":"ContactPoint",
						"telephone":"<xsl:value-of select="tokenize(., '_')[1]"/>",
						"contactType":"<xsl:value-of select="tokenize(., '_')[2]"/>"
						}
					</xsl:for-each>
				]
				<xsl:if test="$common/email != ''">
				,"email":[<xsl:for-each select="$common/email" >
						<xsl:if test="position() != 1">, </xsl:if>"<xsl:value-of select="."/>"</xsl:for-each>]
				</xsl:if>
			}
		</script>
	</xsl:template>


	<xsl:template name="MAIN_CONTENT" />

	<xsl:template name="INDEX_BLOCKS">

		<!-- slider -->
			<xsl:if test="$domain/slideshow/main_slider_frame">
				<div class="container index-slider">
					<div class="slider">
						<xsl:for-each select="$domain/slideshow/main_slider_frame">
							<div>
								<a href="{link}">
									<img src="{@path}{pic}" alt="" />
								</a>
							</div>
						</xsl:for-each>
					</div>
					<xsl:if test="count($domain/slideshow/main_slider_frame) &gt; 1">
						<div id="index-nav" class="device-nav"></div>
					</xsl:if>
				</div>
			</xsl:if>
		<!-- slider end -->

		<!-- products carousel -->
		<div class="devices-block">
			<div class="container">
				<div class="title_1">Лучшие цены</div>
				<div class="devices-block__wrap">
					<xsl:for-each select="page/product[tag = 'Акция']">
						<xsl:apply-templates select="."/>
					</xsl:for-each>
				</div>
				<div id="sale-nav" class="device-nav"></div>
			</div>
		</div>
		<div class="devices-block">
			<div class="container">
				<div class="title title_block">Хиты продаж</div>
				<div class="devices-block__wrap">
					<xsl:for-each-group select="page/product[tag = 'Хит продаж']" group-by="@id">
						<xsl:apply-templates select="current-group()[1]"/>
					</xsl:for-each-group>
				</div>
			</div>
		</div>
		<div class="devices-block">
			<div class="container">
				<div class="title title_block">Новинки</div>
				<div class="devices-block__wrap">
					<xsl:for-each-group select="page/product[tag = 'Новый товар']" group-by="@id">
						<xsl:apply-templates select="current-group()[1]"/>
					</xsl:for-each-group>
				</div>
			</div>
		</div>

	</xsl:template>

	<xsl:template name="EXTRA_SCRIPTS">
		<script type="text/javascript" src="fotorama/fotorama.js"/>
	</xsl:template>

</xsl:stylesheet>
