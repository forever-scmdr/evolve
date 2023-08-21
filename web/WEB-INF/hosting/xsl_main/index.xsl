<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">
	<xsl:import href="common_page_base.xsl"/>
<!--	<xsl:import href="snippets/custom_blocks.xsl"/>-->
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

	<xsl:template name="MAIN_CONTENT" />

	<xsl:template name="INDEX_BLOCKS">
		<!-- slider -->
		<xsl:if test="page/main_page/main_slider_frame">
			<div class="slider">
				<xsl:for-each select="page/main_page/main_slider_frame">
					<div class="slider__item">
						<div class="slider__content">
							<div class="container slider__container">
								<div class="slider__body">
									<div class="slider__title"><xsl:value-of select="name" disable-output-escaping="yes"/></div>
									<div class="slider__text"><xsl:value-of select="text" disable-output-escaping="yes"/></div>
									<a class="slider__button button" href="{link}"><xsl:value-of select="link_name" disable-output-escaping="yes"/></a>
								</div>
							</div>
						</div>
						<div class="slider__image">
							<img src="{@path}{pic}" alt="" />
						</div>
					</div>
				</xsl:for-each>
			</div>
		</xsl:if>
		<div class="slider-nav"></div>
		<!-- slider end -->
		<xsl:variable name="unwanted_types" select="('main_promo_bottom', 'seo', 'product', 'main_slider_frame', 'banner_section')"/>


		<xsl:for-each select="page/main_page/(*[not($unwanted_types = @type) and @type != ''])">
			<xsl:if test="@type != 'custom_block'">
				<xsl:text disable-output-escaping="yes">&lt;div class="container" &gt;</xsl:text>
			</xsl:if>
			<xsl:apply-templates select="." mode="content"/>
			<xsl:if test="@type != 'custom_block'">
				<xsl:text disable-output-escaping="yes">&lt;/div&gt;</xsl:text>
			</xsl:if>
		</xsl:for-each>



		<section class="s-info">
			<div class="container">
				<xsl:value-of select="$seo[1]/bottom_text" disable-output-escaping="yes"/>
			</div>
		</section>

	</xsl:template>

	<xsl:template name="EXTRA_SCRIPTS">
		<script type="text/javascript" src="fotorama/fotorama.js"/>
	</xsl:template>

</xsl:stylesheet>
