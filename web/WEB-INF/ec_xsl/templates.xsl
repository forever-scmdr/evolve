<?xml version="1.0" encoding="UTF-8"?> 
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:f="f:f" version="2.0">
	
	
	
	

	<!-- banners -->
	<xsl:template match="custom_block[1]">
		<section class="{class}">
			<div class="container">
				<div class="block__header">
					<div class="block__title"><xsl:value-of select="header" /></div>
					<div class="block__subtitle"><xsl:value-of select="subheader" /></div>
				</div>
				<div class="grid grid_cols_3">
					<xsl:for-each select="../../../page/main_page/custom_block[1]/custom_element">
						<div class="banner banner_icon-left {../class}__item">
							<div class="{../class}__image banner__image">
								<img src="{@path}{image}"/>
							</div>
							<div class="{../class}__title banner__title"><xsl:value-of select="header" /></div>
							<div class="banner__text"><xsl:value-of select="text" disable-output-escaping="yes" /></div>
							<xsl:if test="link and not(link = '')">
								<a href="{link}" class="banner__link">Learn more</a>
							</xsl:if>
						</div>
					</xsl:for-each>
				</div>
			</div>
		</section>
	</xsl:template>

	<xsl:template match="custom_block[2]">
		<section class="{class}">
			<div class="container">
				<div class="block__header">
					<div class="block__title"><xsl:value-of select="header" /></div>
					<div class="block__subtitle"><xsl:value-of select="subheader" /></div>
				</div>
				<div class="grid">
					<xsl:for-each select="../../../page/main_page/custom_block[2]/custom_element">
						<div class="banner banner_icon-left {../class}__item">
							<div class="{../class}__image banner__image">
								<img src="{@path}{image}"/>
							</div>
							<div class="{../class}__title banner__title"><xsl:value-of select="header" /></div>
							<div class="banner__text"><xsl:value-of select="text" disable-output-escaping="yes" /></div>
							<xsl:if test="link and not(link = '')">
								<a href="{link}" class="banner__link">Learn more</a>
							</xsl:if>
						</div>
					</xsl:for-each>
				</div>
			</div>
		</section>
	</xsl:template>

	<xsl:template match="custom_block[3]">
		<section class="{class}">
			<div class="container">
				<div class="block__header">
					<div class="block__title"><xsl:value-of select="header" /></div>
					<div class="block__subtitle"><xsl:value-of select="subheader" /></div>
				</div>
				<div class="grid">
					<xsl:for-each select="../../../page/main_page/custom_block[3]/custom_element">
						<div class="banner banner_icon-left {../class}__item">
							<div class="{../class}__image banner__image">
								<img src="{@path}{image}"/>
							</div>
							<div class="{../class}__title banner__title"><xsl:value-of select="header" /></div>
							<div class="banner__text"><xsl:value-of select="text" disable-output-escaping="yes" /></div>
							<xsl:if test="link and not(link = '')">
								<a href="{link}" class="banner__link">Learn more</a>
							</xsl:if>
						</div>
					</xsl:for-each>
				</div>
			</div>
		</section>
	</xsl:template>

	<xsl:template match="custom_block[4]">
		<section class="{class}">
			<div class="container">
				<div class="block__header">
					<div class="block__title"><xsl:value-of select="header" /></div>
					<div class="block__subtitle"><xsl:value-of select="subheader" /></div>
				</div>
				<div class="grid grid_cols_8">
					<xsl:for-each select="../../../page/main_page/custom_block[4]/custom_element">
						<div class="banner banner_icon-left {../class}__item">
							<div class="{../class}__image banner__image">
								<img src="{@path}{image}"/>
							</div>
							<div class="{../class}__title banner__title"><xsl:value-of select="header" /></div>
							<div class="banner__text"><xsl:value-of select="text" disable-output-escaping="yes" /></div>
							<xsl:if test="link and not(link = '')">
								<a href="{link}" class="banner__link">Learn more</a>
							</xsl:if>
						</div>
					</xsl:for-each>
				</div>
			</div>
		</section>
	</xsl:template>



	<!-- about -->
	<xsl:template match="custom_block[5]">
		<section class="{class}">
			<div class="container">
				<div class="wrap">
					<div class="{class}__image">
						<img src="{@path}{image}"/>
					</div>
					<div class="{class}__content">
						<div class="{class}__title block__title"><xsl:value-of select="header" /></div>
						<div class="{class}__text"><xsl:value-of select="text" disable-output-escaping="yes" /></div>
						<xsl:if test="link and not(link = '')">
							<a href="{link}" class="{class}__link">Learn more</a>
						</xsl:if>
					</div>
				</div>
			</div>
		</section>
	</xsl:template>

	<!-- banners -->
	<xsl:template match="custom_block[6]">
		<section class="{class}">
			<div class="container">
				<div class="block__header">
					<div class="block__title"><xsl:value-of select="header" /></div>
					<div class="block__subtitle"><xsl:value-of select="subheader" /></div>
				</div>
				<div class="wrap wrap_columns_3">
					<xsl:for-each select="../../../page/main_page/custom_block[6]/custom_element">
						<div class="banner {../class}__item">
							<div class="{../class}__image banner__image">
								<img src="{@path}{image}"/>
							</div>
							<div class="{../class}__title banner__title"><xsl:value-of select="header" /></div>
							<div class="banner__text"><xsl:value-of select="text" disable-output-escaping="yes" /></div>
							<xsl:if test="link and not(link = '')">
								<a href="{link}" class="banner__link">Learn more</a>
							</xsl:if>
						</div>
					</xsl:for-each>
				</div>
			</div>
		</section>
	</xsl:template>

	
	
	<!-- gallery -->
	<xsl:template match="custom_block[7]">
		<section class="{class}">
			<div class="container">
				<div class="block__header block__header_left">
					<div class="block__title"><a href="/gallery" style="text-decoration: none; color: black"><xsl:value-of select="header" /></a></div>
				</div>
				<div class="wrap wrap_columns_4">
					<xsl:for-each select="../../../page/main_page/custom_block[7]/custom_element">
						<a class="{../class}__image magnific_popup-image" href="{@path}{image}"><img src="{@path}{image}" /></a>
					</xsl:for-each>
				</div>
			</div>
		</section>
	</xsl:template>

	<!-- banners -->
	<xsl:template match="custom_block[8]">
		<a name="clients"></a>
		<section class="{class}">
			<div class="container">
				<div class="block__header">
					<div class="block__title"><xsl:value-of select="header" /></div>
					<div class="block__subtitle"><xsl:value-of select="subheader" /></div>
				</div>
				<div class="wrap wrap_columns_12">
					<xsl:for-each select="../../../page/main_page/custom_block[8]/custom_element">
						<div class="banner {../class}__item">
							<div class="{../class}__image banner__image">
								<img src="{@path}{image}"/>
							</div>
							<!-- <div class="{../class}__title banner__title"><xsl:value-of select="header" /></div> -->
							<!-- <div class="banner__text"><xsl:value-of select="text" disable-output-escaping="yes" /></div> -->
							<!-- <a href="{link}" class="banner__link">Learn more</a> -->
						</div>
					</xsl:for-each>
				</div>
			</div>
		</section>
	</xsl:template>
	
	

</xsl:stylesheet>
