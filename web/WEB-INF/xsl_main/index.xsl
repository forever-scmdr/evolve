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



		<!-- slider -->
		<div class="slider">
			<xsl:for-each select="page/main_page/main_slider_frame">
				<div class="slider__item">
					<div class="slider__content">
						<div class="container slider__container">
							<div class="slider__body">
								<div class="slider__title"><xsl:value-of select="name" disable-output-escaping="yes"/></div>
								<div class="slider__text"><xsl:value-of select="text" disable-output-escaping="yes"/></div>
								<a class="slider__button button" href=""><xsl:value-of select="link_name" disable-output-escaping="yes"/></a>
							</div>
						</div>
					</div>
					<div class="slider__image">
						<img src="{@path}{pic}" alt="" />
					</div>
				</div>
			</xsl:for-each>
		</div>
		<div class="slider-nav"></div>
		<!-- slider end -->


		<xsl:apply-templates select="page/main_page/custom_block[type='type_sections']"></xsl:apply-templates>

		<!-- products carousel -->
		<!-- <div class="block devices-block ptb">
			<div class="container">
				<div class="title title_2">Выгодные предложения</div>
				<div class="devices-block__wrap device-carousel">
					<xsl:for-each select="page/main_page/product">
						<div class="devices-block__column">
							<xsl:apply-templates select="."/>
						</div>
					</xsl:for-each>
				</div>
				<div class="device-nav"></div>
			</div>
		</div> -->

		<xsl:apply-templates select="page/main_page/custom_block[type='type_utp']"></xsl:apply-templates>

		<section class="news ptb">
			<div class="container">
				<div class="title title_2">
					<a href="/novosti" style="text-decoration: none; color: black">Новости</a>
				</div>
				<div class="grid">
					<xsl:for-each select="page//news_item">
						<div class="news__item">
							<!-- <a class="news__image-container" href="{show_news_item}"><img src="{@path}{main_pic}" alt="{name}" /></a> -->
							<div class="date"><xsl:value-of select="tokenize(date, ' ')[1]" /></div>
							<a class="news__title" href="{show_news_item}"><xsl:value-of select="header" /></a>
							<xsl:value-of select="short" disable-output-escaping="yes"/>
						</div>
					</xsl:for-each>
				</div>
			</div>
		</section>



		<!-- <section class="s-info">
			<div class="container">
				<xsl:value-of select="$seo[1]/bottom_text" disable-output-escaping="yes"/>
			</div>
		</section> -->

	</xsl:template>



	<xsl:template name="EXTRA_SCRIPTS">
		<script type="text/javascript" src="fotorama/fotorama.js"/>
	</xsl:template>

</xsl:stylesheet>
