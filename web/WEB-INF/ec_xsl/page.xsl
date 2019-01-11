<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:f="f:f" version="2.0">
	<xsl:import href="common_page_base.xsl"/>
	<xsl:output method="html" encoding="UTF-8" media-type="text/xhtml" indent="yes" omit-xml-declaration="yes"/>
	<xsl:strip-space elements="*"/>

	<xsl:variable name="title" select="$p/header"/>
	<xsl:variable name="h1" select="if($seo/h1 != '') then $seo/h1 else $title"/>
	<xsl:variable name="p" select="page/custom_page"/>

	<xsl:variable name="active_menu_item" select="$p/@key"/>

	<xsl:variable name="canonical" select="concat('/', $active_menu_item, '/')"/>


	<xsl:template name="CONTENT">
		<!-- CONTENT BEGIN -->
		<div class="path-container">
			<div class="path">
				<a href="{$main_host}">Главная страница</a> <i class="fas fa-angle-right"></i>
				<xsl:for-each select="$p/parent">
					<a href="{show_page}"><xsl:value-of select="header"/></a> <i class="fas fa-angle-right"></i>
				</xsl:for-each>
			</div>
			<xsl:call-template name="PRINT"/>
		</div>
		<h1 class="page-title"><xsl:value-of select="$h1"/></h1>

		<div class="page-content m-t">
			<div class="catalog-items info">
				<xsl:for-each select="$p/custom_page">
					<div class="catalog-item">
						<a href="{show_page}" class="image-container" style="background-image: url('{@path}{main_pic}');"><!-- <img src="http://shop4.must.by/{@path}{main_pic}" alt=""/> --></a>
						<div class="text">
							<div class="date"><xsl:value-of select="date"/></div>
							<a href="{show_page}"><xsl:value-of select="header"/></a>
							<xsl:value-of select="short" disable-output-escaping="yes"/>
						</div>
					</div>
				</xsl:for-each>
			</div>
			<xsl:value-of select="$p/text" disable-output-escaping="yes"/>
		</div>

		<xsl:call-template name="ACTIONS_MOBILE"/>
	</xsl:template>

</xsl:stylesheet>