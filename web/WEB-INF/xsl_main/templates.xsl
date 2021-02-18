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
	        <a class="slider__link" href="{link}"></a>
	      </div>
			</xsl:for-each>
    </div>
	</xsl:template>


	<xsl:template match="custom_block[type='type_banners']">
		<xsl:for-each select="custom_block">
      <div class="ads__banner">
        <div class="banner">
          <div class="banner__image">
            <img src="{@path}{image}" alt="" />
          </div>
          <div class="banner__content">
            <div class="banner__title"><xsl:value-of select="header" disable-output-escaping="yes"/></div>
            <div class="banner__subtitle"><xsl:value-of select="subheader" disable-output-escaping="yes"/></div>
          </div>
          <a class="banner__link" href="{link}"></a>
        </div>
      </div>
		</xsl:for-each>
	</xsl:template>


	<xsl:template match="custom_block[type='type_sections']">
		<div class="sections-block">
			<div class="container">
				<div class="title_1"><xsl:value-of select="header" /></div>
					<div class="sections-block__wrap">
						<xsl:for-each select="custom_block">
							<div class="catalog-section">
		            <a class="catalog-section__image" href="{link}">
		              <img src="{@path}{image}" alt="" />
		            </a>
		            <div class="catalog-section__subtitle">Раздел</div>
		            <a class="catalog-section__title" href="{link}"><xsl:value-of select="header" /></a>
		          </div>
						</xsl:for-each>
					</div>
			</div>
		</div>
	</xsl:template>

	<xsl:template match="custom_block[type='type_brands']">
		<div class="brands-block">
      <div class="container">
        <div class="title_1"><xsl:value-of select="header" /></div>
        <div class="brands-block__wrap">
					<xsl:for-each select="custom_block">
	          <div class="brands-block__item">
	            <div class="brand">
	              <img src="{@path}{image}" alt="" />
	            </div>
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
