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


		<!-- ads -->
		<div class="ads">
	  <div class="container">
		<div class="ads__wrap">
		  <div class="ads__slider">
			<xsl:apply-templates select="page/main_page/custom_block[type='type_slider']"></xsl:apply-templates>
		  </div>
		  <div class="ads__banners">
			<xsl:apply-templates select="page/main_page/custom_block[type='type_banners']"></xsl:apply-templates>
		  </div>
		</div>
	  </div>
	</div>
		<!-- ads end -->


		<xsl:apply-templates select="page/main_page/custom_block[type='type_sections']"></xsl:apply-templates>


		<!-- products carousel -->
		<div class="devices-block">
			<div class="container">
				<div class="title_1">Лучшие товары</div>
				<div class="devices-block__wrap device-carousel">
					<xsl:for-each select="page/main_page/product">
						<div class="devices-block__column">
							<xsl:apply-templates select="."/>
						</div>
					</xsl:for-each>
				</div>
			</div>
		</div>

		<div class="news-block">
			<div class="container">
				<div class="title_1">Новости</div>
				<div class="news-block__wrap">
					<xsl:for-each select="page//news_item">
						<div class="news-block__item news-item">
							<a class="news-item__image" href="{show_news_item}">
								<img src="{@path}{main_pic}" alt="" />
							</a>
							<div class="news-item__info">
								<a class="news-item__title" href="news-item.html"><xsl:value-of select="header" /></a>
								<div class="news-item__text"><xsl:value-of select="short" disable-output-escaping="yes"/></div>
							</div>
						</div>
					</xsl:for-each>
				</div>
			</div>
		</div>
		<xsl:apply-templates select="page/main_page/custom_block[type='type_brands']"></xsl:apply-templates>
		<xsl:apply-templates select="page/main_page/custom_block[type='type_map']"></xsl:apply-templates>





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
