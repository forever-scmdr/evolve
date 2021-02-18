<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:f="f:f" version="2.0">





	<xsl:template match="custom_block[type='type_about']">
		<div class="block blockquote-block ptb">
			<div class="container">
				<div class="blockquote-block__wrap">
					<div class="blockquote-block__title title title_2"><xsl:value-of select="header" /></div>
					<div class="blockquote-block__text">
						<p><xsl:value-of select="text" disable-output-escaping="yes" /></p>
					</div>
				</div>
			</div>
		</div>
	</xsl:template>


	<xsl:template match="custom_block[type='type_slider']">
		<div class="slider">
			<xsl:for-each select="custom_block">
	      <div class="slider__item">
	        <div class="slider__image">
	          <img src="{@path}{image}" alt="" />
	        </div>
	        <div class="slider__content">
	          <div class="slider__title"><xsl:value-of select="header" disable-output-escaping="yes"/></div>
	          <div class="slider__subtitle"><xsl:value-of select="subheader" disable-output-escaping="yes"/></div>
	        </div>
	        <a class="slider__link" href=""></a>
	      </div>
			</xsl:for-each>
    </div>
	</xsl:template>


	<xsl:template match="custom_block[type='type_sections']">
		<div class="block sections-block ptb">
			<div class="container">
				<div class="title title_2"><xsl:value-of select="header" /></div>
					<div class="sections-block_wrap">
						<xsl:for-each select="custom_block">
							<div class="banner-sections">
								<div class="banner-sections__image img">
									<img src="{@path}{image}" alt="" />
								</div>
								<div class="banner-sections__title"><xsl:value-of select="header" /></div>
								<a href="{link}" class="banner-sections__link"></a>
							</div>
						</xsl:for-each>
					</div>
			</div>
		</div>
	</xsl:template>

	<xsl:template match="custom_block[type='type_digits']">
		<div class="block numbers-block ptb">
			<div class="container">
				<div class="numbers-block__wrap">
					<xsl:for-each select="custom_block">
						<div class="banner-numbers">
							<div class="banner-numbers__title"><xsl:value-of select="header" /></div>
							<div class="banner-numbers__text"><xsl:value-of select="subheader" /></div>
						</div>
					</xsl:for-each>
				</div>
			</div>
		</div>
	</xsl:template>

	<xsl:template match="custom_block[type='type_utp']">
		<div class="block icons-block ptb">
			<div class="container">
				<div class="title title_2"><xsl:value-of select="header" /></div>
				<div class="icons-block__wrap">
					<xsl:for-each select="custom_block">
						<div class="banner-icons">
							<div class="banner-icons__image"><img src="{@path}{image}" alt="" /></div>
							<div class="banner-icons__title"><xsl:value-of select="header" /></div>
							<div class="banner-icons__text"><xsl:value-of select="text" disable-output-escaping="yes" /></div>
						</div>
					</xsl:for-each>
				</div>
			</div>
		</div>
	</xsl:template>

	<xsl:template match="custom_block[type='type_gifts']">
		<div class="block gifts-block ptb">
			<div class="container">
				<div class="gifts-block__wrap">
					<xsl:for-each select="custom_block">
						<div class="banner-gift">
							<div class="banner-gift__image"><img src="{@path}{image}" alt="" /></div>
							<div class="banner-gift__title"><xsl:value-of select="header" /></div>
							<div class="banner-gift__text"><xsl:value-of select="text" disable-output-escaping="yes" /></div>
						</div>
					</xsl:for-each>
				</div>
			</div>
		</div>
	</xsl:template>

	<xsl:template match="custom_block[type='type_map']">
		<div class="block contacts-block ptb">
				<div class="container">
					<div class="contacts-block__wrap">
						<div class="contacts-block_title title title_2"><xsl:value-of select="header" /></div>
						<div class="contacts-block_subtitle"><xsl:value-of select="subheader" /></div>
						<div class="contacts-block_text"><xsl:value-of select="text" disable-output-escaping="yes" /></div>
					</div>
					<div class="map">
						<iframe src="https://yandex.ru/map-widget/v1/?um=constructor%3A3bcb9da99b1b8c51f6dce673b4be2c8cfeee83c9362822124f6c3e28a3c5ebca&amp;source=constructor" width="100%" height="400" frameborder="0"></iframe>
					</div>
				</div>
			</div>
	</xsl:template>

</xsl:stylesheet>
