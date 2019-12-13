<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:f="f:f" version="2.0">
	
	<xsl:template match="custom_block" mode="big-product">
		<section class="bb-big-product pv">
			<div class="container">
				<div class="bb-big-product__header">
					<div class="title title_small"><xsl:value-of select="subheader" /></div>
					<div class="title title_big"><xsl:value-of select="header" /></div>
					<xsl:value-of select="text" disable-output-escaping="yes" />
				</div>
				<div class="wrap">
					<div class="bb-big-product__left-column">
						<xsl:apply-templates select="custom_element" mode="big-product"></xsl:apply-templates>
					</div>
					<div class="bb-big-product__image"><img src="{@path}{image}"/></div>
				</div>
			</div>
		</section>
	</xsl:template>

	<xsl:template match="custom_element" mode="big-product">
		<div class="bb-big-product__item">
			<div class="title title_4"><xsl:value-of select="header" /></div>
			<p class="text_small"><xsl:value-of select="subheader" /></p>
		</div>
		<hr />
	</xsl:template>

	<xsl:template match="custom_block[5]">
		<section class="bb-utp pv">
			<div class="container">
				<div class="wrap">
					<xsl:for-each select="../../../page/main_page/custom_block[5]/custom_element">
						<div class="bb-utp__item">
							<div class="bb-utp__image">
								<img src="{@path}{image}"/>
							</div>
							<div class="title title_2"><xsl:value-of select="header" /></div>
							<div class="text text_small"><xsl:value-of select="subheader" /></div>
						</div>
					</xsl:for-each>
				</div>
			</div>
		</section>
	</xsl:template>

	<xsl:template match="custom_block[6]">
		<section class="bb-gallery">
			<div class="wrap">
				<xsl:for-each select="../../../page/main_page/custom_block[6]/custom_element">
					<a class="bb-gallery__image magnific_popup-image" href="{@path}{image}"><img src="{@path}{image}" /></a>
				</xsl:for-each>
			</div>
		</section>
	</xsl:template>

	<xsl:template match="custom_block[3]">
		<section class="bb-lists pb">
			<div class="container">
				<div class="wrap">
					<xsl:for-each select="../../../page/main_page/custom_block[3]/custom_element">
						<div class="bb-lists__item">
							<div class="title title_4"><xsl:value-of select="header" /></div>
							<xsl:value-of select="text" disable-output-escaping="yes" />
						</div>
					</xsl:for-each>
				</div>
			</div>
		</section>
	</xsl:template>


	<xsl:template match="custom_block[2]">
		<section class="bb-brands pv">
			<div class="container">
				<div class="bb-brands__header pb">
					<div class="title title_1"><xsl:value-of select="header" /></div>
				</div>
				<div class="wrap">
					<xsl:for-each select="../../../page/main_page/custom_block[2]/custom_element">
						<div class="bb-brands__item">
							<div class="bb-brands__logo">
								<img src="{@path}{image}" />
							</div>
						</div>
					</xsl:for-each>
				</div>
			</div>
		</section>
	</xsl:template>

	<xsl:template match="custom_block[1]">
		<section class="bb-lead pb">
			<div class="container">
				<div class="wrap">
					<div class="bb-lead__title"><xsl:value-of select="header" /></div>
					<div class="bb-lead__text"><xsl:value-of select="text" disable-output-escaping="yes" /></div>
				</div>
			</div>
		</section>
	</xsl:template>

</xsl:stylesheet>
